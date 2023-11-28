/*
 *  ===========================================================================================
 *  = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *     This software is supplied under the terms of a license agreement or nondisclosure
 *     agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *     disclosed except in accordance with the terms in that agreement.
 *          Copyright (C) 2020 -? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 *  Description: // Detail description about the function of this module,
 *               // interfaces with the other modules, and dependencies.
 *  Revision History:
 *  Date	               Author	                   Action
 *  2020/06/01 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */

package com.blusalt.blusaltpaxsdk.pax.cardprocess;

import static com.blusalt.blusaltpaxsdk.device.PosActivity.appPreferenceHelper;
import static com.blusalt.blusaltpaxsdk.utils.Constants.TXN_TYPE_ICC;
import static com.blusalt.blusaltpaxsdk.utils.Constants.TXN_TYPE_PICC;
import static com.pax.dal.entity.EBeepMode.FREQUENCE_LEVEL_5;
import static com.pax.dal.entity.EPiccRemoveMode.REMOVE;

import android.os.ConditionVariable;
import android.os.SystemClock;
import android.util.Log;

import com.blusalt.blusaltpaxsdk.MyApplication;
import com.blusalt.blusaltpaxsdk.models.CreditCard;
import com.blusalt.blusaltpaxsdk.pax.BasePresenter;
import com.blusalt.blusaltpaxsdk.pax.DeviceImplNeptune;
import com.blusalt.blusaltpaxsdk.pax.entity.EnterPinResult;
import com.blusalt.blusaltpaxsdk.pax.manager.EEnterPinType;
import com.blusalt.blusaltpaxsdk.pax.util.CardInfoUtils;
import com.blusalt.blusaltpaxsdk.pax.util.Util;
import com.blusalt.blusaltpaxsdk.utils.Constants;
import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.exceptions.PiccDevException;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.device.DeviceManager;
import com.paxsz.module.emv.param.EmvProcessParam;
import com.paxsz.module.emv.param.EmvTransParam;
import com.paxsz.module.emv.process.IStatusListener;
import com.paxsz.module.emv.process.contact.CandidateAID;
import com.paxsz.module.emv.process.contact.EmvProcess;
import com.paxsz.module.emv.process.contact.IEmvTransProcessListener;
import com.paxsz.module.emv.process.contactless.ClssProcess;
import com.paxsz.module.emv.process.contactless.IClssStatusListener;
import com.paxsz.module.emv.process.entity.IssuerRspData;
import com.paxsz.module.emv.process.entity.TransResult;
import com.paxsz.module.emv.process.enums.CvmResultEnum;
import com.paxsz.module.emv.xmlparam.entity.common.CapkParam;
import com.paxsz.module.emv.xmlparam.entity.common.Config;

import java.util.List;

public class TransProcessPresenter extends BasePresenter<TransProcessContract.View> implements TransProcessContract.Presenter {
    private static final String TAG = "TransProcessPresenter";
    private ConditionVariable enterPinCv = new ConditionVariable();
    private ConditionVariable appSelectCv = new ConditionVariable();
    private int enterPinRet = 0;
    private int appSelectRet = 0;
    private int currentTxnType = TXN_TYPE_ICC;
    private EnterPinTask enterPinTask;
    public boolean isEmptyOrLessThanThree;
    private boolean needShowRemoveCard = true;//if need to show removecard msg

    //Our Implementation
    private  String clearPinKey;
    private  byte transactionType;
    private CreditCard creditCard;

    private IEmvTransProcessListener emvTransProcessListener = new IEmvTransProcessListener() {
        @Override
        public int onWaitAppSelect(boolean isFirstSelect, List<CandidateAID> candList) {
            if (candList == null || candList.size() == 0) {
                return RetCode.EMV_NO_APP;
            }
            AppSelectTask selectAppTask = new AppSelectTask();
            selectAppTask.registerAppSelectListener(selectRetCode -> {
                appSelectRet = selectRetCode;
                appSelectCv.open();
            });
            selectAppTask.startSelectApp(isFirstSelect, candList);
            appSelectCv.block();
            return appSelectRet;
        }

        @Override
        public int onCardHolderPwd(boolean bOnlinePin, int leftTimes, byte[] pinData) {
            LogUtils.w(TAG, "onCardHolderPwd, current thread " + Thread.currentThread().getName() + ", id:" + Thread.currentThread().getId());
            enterPinProcess(true, bOnlinePin, leftTimes);
            enterPinCv.block();
            return enterPinRet;
        }
    };

    private IClssStatusListener clssStatusListener = new IClssStatusListener() {

        @Override
        public void onRemoveCard() {
            while (!isCardRemove()){
                if(needShowRemoveCard) {
                    MyApplication.getINSTANCE().runOnUiThread(() -> {
                        mView.onRemoveCard();
                        needShowRemoveCard = false;
                    });
                }
            }
        }
    };

    private IStatusListener statusListener = new IStatusListener() {
        @Override
        public void onReadCardOk() {
            MyApplication.getINSTANCE().runOnUiThread(() -> {
                mView.onReadCardOK();
            });
            MyApplication.getINSTANCE().getDal().getSys().beep(FREQUENCE_LEVEL_5, 100);
            SystemClock.sleep(750);//blue yellow green clss light remain lit for a minimum of approximately 750ms
        }
    };


    public Boolean isCardRemove(){
        try {
            LogUtils.d(TAG, "isCardRemove");
             MyApplication.getINSTANCE().getDal().getPicc(EPiccType.INTERNAL).remove(REMOVE,(byte)0);
        } catch (PiccDevException e) {
            LogUtils.e(TAG, "isCardRemove : " + e.getMessage());
            return false;
        }
        return true;
    }

    private void enterPinProcess(boolean isICC, boolean bOnlinePin, int leftTimes) {
        if (enterPinTask != null) {
            enterPinTask.unregisterListener();
            enterPinTask = null;
        }
        LogUtils.w(TAG, "isOnlinePin:" + bOnlinePin + ",leftTimes:" + leftTimes);

        //TODO RECEIVE  INTERFACE FROM A method
        enterPinTask = new EnterPinTask();
        String enterPinPrompt = "Please Enter PIN";
        if (bOnlinePin) {
            String pan = "";
            if (isICC) {
                ByteArray byteArray = new ByteArray();
                EmvProcess.getInstance().getTlv(0x57, byteArray);
                String strTrack2 = CardInfoUtils.getTrack2FromTag57(byteArray.data);
                pan = CardInfoUtils.getPan(strTrack2);
            } else {
                String strTrack2 = ClssProcess.getInstance().getTrack2();
                LogUtils.e(TAG,"ClssProcess getTrack2() = " + strTrack2);
                pan = CardInfoUtils.getPan(strTrack2);
                LogUtils.e(TAG,"ClssProcess getPan() = " + pan);
            }
            LogUtils.e(TAG,"ClssProcess State = " + "ONLINE_PIN");
            enterPinTask.setOnlinePan(pan);
            enterPinTask.setEnterPinType(EEnterPinType.ONLINE_PIN);
        } else {
            LogUtils.e(TAG,"ClssProcess State = " + "OFFLINE_PCI_MODE");

            enterPinPrompt = enterPinPrompt + "(" + leftTimes + ")";
            enterPinTask.setEnterPinType(EEnterPinType.OFFLINE_PCI_MODE);
        }
        mView.onUpdatePinLen("");
        enterPinTask.registerListener(new EnterPinTask.IEnterPinListener() {
            @Override
            public void onUpdatePinLen(String pinLen) {
                mView.onUpdatePinLen(pinLen);
                Log.e("TAaAG",  pinLen);
            }

            @Override
            public String getEnteredPin() {
                return mView.getEnteredPin();
            }

            @Override
            public void onEnterPinFinish(EnterPinResult enterPinResult) {
                if (enterPinResult.getRet() == EnterPinResult.RET_OFFLINE_PIN_READY) {
                    enterPinRet = EnterPinResult.RET_SUCC;
                } else {
                    enterPinRet = enterPinResult.getRet();
                    mView.onEnterPinFinish(enterPinRet);
                    LogUtils.e(TAG, "onEnterPinFinish, enterPinRet:" + enterPinRet);
                }
                enterPinCv.open();
            }

            @Override
            public void onReturnPinBlock(byte[] pinBlock) {

//                getClearPin(Util.byteToHexString(pinBlock));
//                getClearPin(Util.bcd2str(pinBlock));
//                getClearPin(String.valueOf(pinBlock));
                LogUtils.e(TAG, "Pinblock, enterPinRet:" + pinBlock);

                GenerateEMVCardDetals(pinBlock);

            }
        });
        mView.onStartEnterPin(enterPinPrompt);
        enterPinTask.startEnterPin();
    }

    private void GenerateEMVCardDetals(byte[] pinBlock) {

//        EmvCardType cardType = EmvCardType.PIN_AND_CHIP;
//
////        Log.d("strTrack2 0x57","" + strTrack2);
////        Log.d("holderName 0x5F20","" +holderName);
//
//        switch (currentTxnType){
//
////              case  TXN_TYPE_ICC : {
////                  cardType =EmvCardType.PIN_AND_CHIP;
////              }
////              break;
//
//            case TXN_TYPE_PICC :{
//                cardType = EmvCardType.CONTACTLESS;
//
//            }
//            break;
//        }
//
//
////        EmvCard.PinInfo pinInfo = null;
//
        if(pinBlock != null){
            String pinBlk = ConvertHelper.getConvert().bcdToStr (pinBlock);

            LogUtils.i(TAG, "onReturnPinBlock, enterPinRet:" + enterPinRet);
            LogUtils.i(TAG, "onReturnPinBlock,  Actual PinBlock from terminal:" + pinBlk);
            LogUtils.i(TAG, "onReturnPinBlock,  Actual PinBlock from terminal:" + Util.str2Bcd(pinBlk));
            LogUtils.i(TAG, "onReturnPinBlock,  Actual PinBlock from terminal:" + Util.bcd2str(pinBlock));
            LogUtils.i(TAG, "onReturnPinBlock,  Actual PinBlock from terminal:" + Util.byteToHexString(pinBlock));
            LogUtils.i(TAG, "onReturnPinBlock,  Actual PinBlock from terminal:" + pinBlock);

//            getClearPin(Util.byteToHexString(pinBlock));
//            getClearPin(Util.bcd2str(pinBlock));
//            getClearPin(String.valueOf(pinBlock));
//            getClearPin(pinBlk);

//            pinInfo = new EmvCard.PinInfo(pinBlock,null, Util.str2Bcd(pinBlk));
            creditCard = new CreditCard();
            appPreferenceHelper.setSharedPreferenceString(Constants.PIN_BLOCK, Util.byteToHexString(pinBlock));

            creditCard.setPIN(Util.byteToHexString(pinBlock));

            LogUtils.i(TAG, "My onReturnPinBlock" + creditCard.getPIN());

        }else {
            appPreferenceHelper.setSharedPreferenceString(Constants.PIN_BLOCK, "");
        }


//        setCardType(cardType);
//        setPinInfo(pinInfo);
    }

    private void getClearPin(String data) {
        char[] ary = data.toCharArray();
        StringBuilder cardPins = new StringBuilder();
        for (int i = 0; i < ary.length; i++) {
            if (i % 2 == 1) {
                cardPins.append(ary[i]);
            }
        }
        String result = cardPins.toString();
//        cPin = result;
        Log.d("result card ", result);
    }


    //    (2) Icc
    @Override
    public void startEmvTrans() {
        //This is what links to the pinpad to poping up (registerEmvProcessListener)
        Log.e("TAG", "Obtaining  Card Details.....");

        EmvProcess.getInstance().registerEmvProcessListener(emvTransProcessListener);//Callback for when  there is an app selection to be done or card holders password
        DeviceImplNeptune deviceImplNeptune = DeviceImplNeptune.getInstance(); //Instantiate the Various type of payments  and implement IDevice
        DeviceManager.getInstance().setIDevice(deviceImplNeptune); //input the the implementation for IDevice
        MyApplication.getINSTANCE().runInBackground(() -> {
            TransResult transResult = EmvProcess.getInstance().startTransProcess();
            MyApplication.getINSTANCE().runOnUiThread(() -> {
                if (isViewAttached()) {
                    //    (3)
                    if (enterPinTask != null) {
                        this.isEmptyOrLessThanThree = enterPinTask.isEmptyOrLessThanThree;
                    }
                    currentTxnType =TXN_TYPE_ICC;
                    mView.onTransFinish(transResult);
                }
            });
        });
    }

    //    (2) contactless
    @Override
    public void startClssTrans() {

//        callback.onNotifyEmvStatus(new EmvCardReader.Status(DeviceState.PROCESSING, "Obtaining Card Details ....."));
        DeviceImplNeptune deviceImplNeptune = DeviceImplNeptune.getInstance();
        DeviceManager.getInstance().setIDevice(deviceImplNeptune);
        MyApplication.getINSTANCE().runInBackground(() -> {

            //        Todo path to amount been set 0

            TransResult transResult = ClssProcess.getInstance().startTransProcess();
            MyApplication.getINSTANCE().runOnUiThread(() -> {
                if (isViewAttached()) {
                    //    (3)
                    currentTxnType =TXN_TYPE_PICC;

                    if (currentTxnType == TXN_TYPE_PICC && transResult.getCvmResult() == CvmResultEnum.CVM_NO_CVM){
                        GenerateEMVCardDetals(null);
                    }

                    mView.onTransFinish(transResult);
                }
            });
        });
    }

    //Need contect was intended to distinguish contact fom contactless
    @Override
    public void preTrans(EmvTransParam transParam, boolean needContact) {
        MyApplication.getINSTANCE().runInBackground(() -> {
            int ret = 0;
            Config configParam = MyApplication.getParamManager().getConfigParam();
            CapkParam capkParam = MyApplication.getParamManager().getCapkParam();

            if(true) {
//            if(needContact) {
                ret = EmvProcess.getInstance().preTransProcess(new EmvProcessParam.Builder(transParam, configParam, capkParam)
                        .setEmvAidList(MyApplication.getParamManager().getEmvAidList())
                        .create());
                LogUtils.d(TAG, "transPreProcess, emv ret:" + ret);
            }

            Log.e("GetCurrencyCode",  ConvertHelper.getConvert().bcdToStr(transParam.getTransCurrencyCode()));

            ret = ClssProcess.getInstance().preTransProcess(new EmvProcessParam.Builder(transParam, configParam, capkParam)
                    .setPayPassAidList(MyApplication.getParamManager().getPayPassAidList())
                    .setPayWaveParam(MyApplication.getParamManager().getPayWaveParam())
                    .create());
            LogUtils.d(TAG, "transPreProcess, clss ret:" + ret);
            needShowRemoveCard = true;
            ClssProcess.getInstance().registerClssStatusListener(clssStatusListener);
            ClssProcess.getInstance().registerStatusListener(statusListener);
        });
    }

    @Override
    public void startMagTrans() {

    }

    //        (6 a) Particular to PICC ie contactless card
    @Override
    public void startOnlinePin() {
        // for contactless (PICC ) online pin process
        enterPinProcess(false, true, 0);
    }

    @Override
    public void completeEmvTrans(IssuerRspData issuerRspData) {
//        callback.onNotifyEmvStatus(new EmvCardReader.Status(DeviceState.PROCESSING, "Writing Script to Card......."));

        DeviceImplNeptune deviceImplNeptune = DeviceImplNeptune.getInstance();
        DeviceManager.getInstance().setIDevice(deviceImplNeptune);
        MyApplication.getINSTANCE().runInBackground(() -> {
            TransResult transResult = EmvProcess.getInstance().completeTransProcess(issuerRspData);
            MyApplication.getINSTANCE().runOnUiThread(() -> {
                if (isViewAttached()) {
                    mView.onCompleteTrans(transResult);
                }
            });
        });
    }

    @Override
    public void completeClssTrans(IssuerRspData issuerRspData) {
//        callback.onNotifyEmvStatus(new EmvCardReader.Status(DeviceState.PROCESSING, "Writing Script to Card......."));

        DeviceImplNeptune deviceImplNeptune = DeviceImplNeptune.getInstance();
        DeviceManager.getInstance().setIDevice(deviceImplNeptune);
        MyApplication.getINSTANCE().runInBackground(() -> {
            //TODO THIS is WHERE TO WRITE TO CARD  on Second Tap of the PICC CARD
            TransResult transResult = ClssProcess.getInstance().completeTransProcess(issuerRspData);
            MyApplication.getINSTANCE().runOnUiThread(() -> {
                if (isViewAttached()) {
                    mView.onCompleteTrans(transResult);
                }
            });
        });
    }



}
