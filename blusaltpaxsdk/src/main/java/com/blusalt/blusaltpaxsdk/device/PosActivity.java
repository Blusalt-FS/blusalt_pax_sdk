package com.blusalt.blusaltpaxsdk.device;

import static com.blusalt.blusaltpaxsdk.utils.APIConstant.ERROR;
import static com.blusalt.blusaltpaxsdk.utils.APIConstant.incompleteParameters;
import static com.blusalt.blusaltpaxsdk.utils.APIConstant.initializationError;
import static com.blusalt.blusaltpaxsdk.utils.Constants.SUCCESS_CODE;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;

import com.blusalt.blusaltpaxsdk.MyApplication;
import com.blusalt.blusaltpaxsdk.Pos;
import com.blusalt.blusaltpaxsdk.R;
import com.blusalt.blusaltpaxsdk.models.BlusaltTerminalInfo;
import com.blusalt.blusaltpaxsdk.models.CreditCard;
import com.blusalt.blusaltpaxsdk.models.TerminalInfo;
import com.blusalt.blusaltpaxsdk.models.TerminalResponse;
import com.blusalt.blusaltpaxsdk.models.printing.BluSaltPrinter;
import com.blusalt.blusaltpaxsdk.models.printing.PrinterType;
import com.blusalt.blusaltpaxsdk.network.BaseData;
import com.blusalt.blusaltpaxsdk.network.MemoryManager;
import com.blusalt.blusaltpaxsdk.network.RetrofitClientInstance;
import com.blusalt.blusaltpaxsdk.pax.PaxServices.Printer;
import com.blusalt.blusaltpaxsdk.pax.cardprocess.TransProcessContract;
import com.blusalt.blusaltpaxsdk.pax.cardprocess.TransProcessPresenter;
import com.blusalt.blusaltpaxsdk.pax.detectcard.DetectCardContract;
import com.blusalt.blusaltpaxsdk.pax.detectcard.NeptunePollingPresenter;
import com.blusalt.blusaltpaxsdk.pax.entity.DetectCardResult;
import com.blusalt.blusaltpaxsdk.pax.entity.EnterPinResult;
import com.blusalt.blusaltpaxsdk.pax.entity.PaxConfigData;
import com.blusalt.blusaltpaxsdk.processor.LocalData;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.BlusaltTerminalInfoProcessor;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.CardData;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.EmvData;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.TerminalInfoProcessor;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.TerminalInformation;
import com.blusalt.blusaltpaxsdk.processor.service.DownloadService;
import com.blusalt.blusaltpaxsdk.processor.service.SecurityKeyManager;
import com.blusalt.blusaltpaxsdk.processor.service.TerminalKeyParamDownloadListener;
import com.blusalt.blusaltpaxsdk.processor.util.AppExecutors;
import com.blusalt.blusaltpaxsdk.processor.util.TimeUtil;
import com.blusalt.blusaltpaxsdk.processor.util.ValueGenerator;
import com.blusalt.blusaltpaxsdk.utils.AppDataUtils;
import com.blusalt.blusaltpaxsdk.utils.AppPreferenceHelper;
import com.blusalt.blusaltpaxsdk.utils.CombBitmap;
import com.blusalt.blusaltpaxsdk.utils.Constants;
import com.blusalt.blusaltpaxsdk.utils.GenerateBitmap;
import com.blusalt.blusaltpaxsdk.utils.SharedPreferencesUtils;
import com.blusalt.blusaltpaxsdk.utils.StringUtils;
import com.blusalt.blusaltpaxsdk.pax.util.TickTimer;
import com.blusalt.blusaltpaxsdk.pax.util.TimeRecordUtils;
import com.blusalt.blusaltpaxsdk.utils.TransactionListener;
import com.blusalt.blusaltpaxsdk.pax.util.Util;
import com.blusalt.blusaltpaxsdk.utils.KSNUtilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pax.commonlib.utils.ToastUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.dal.IPrinter;
import com.pax.dal.entity.EReaderType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.RetCode;
import com.paxsz.module.emv.param.EmvTransParam;
import com.paxsz.module.emv.process.contact.EmvProcess;
import com.paxsz.module.emv.process.entity.IssuerRspData;
import com.paxsz.module.emv.process.entity.TransResult;
import com.paxsz.module.emv.process.enums.CvmResultEnum;
import com.paxsz.module.emv.process.enums.TransResultEnum;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@Keep
public class PosActivity extends BaseActivity implements TransactionListener, DetectCardContract.View, TransProcessContract.View {
    private Double totalAmount = 0.0;
    public static Double totalAmountPrint = 0.0;
    private static String TAG = PosActivity.class.getName();
    private String cPin = "";
    private boolean isSupport;
    private String accountType;
    private TextView posViewUpdate;
    private TextView textView;
    private ImageView spinKit;
    private MutableLiveData<String> mutableLiveData;
    private static String terminalId;
    private String ksn;

    private CreditCard creditCard;
    boolean isConnected;
    private View bottomView;

    private TransProcessPresenter transProcessPresenter;
    private NeptunePollingPresenter detectPresenter;
    private PaxConfigData paxConfigData;
    private boolean hasDetectedCard = false;

    public static final int TXN_TYPE_MAG = 0x100;
    public static final int TXN_TYPE_ICC = 0x101;
    public static final int TXN_TYPE_PICC = 0x102;

    private static final int PROCESSING_TYPE_ONLINE = 1;
    private static final int PROCESSING_TYPE_SIGNATURE = 2;
    private static final int PROMPT_TYPE_FAILED = 1;
    private static final int PROMPT_TYPE_SUCCESS = 2;

    private TextView panText;
    private TextView expiryDateText;
    private TextView pinText;
    private int pinResult;

    private int currentTxnType = TXN_TYPE_ICC;
    private PopupWindow mEnterPinPopWindow;

    private AlertDialog processingDlg;
    private AlertDialog selectOnlineResultDlg;
    private AlertDialog transPromptDlg;

    private String firstGacTVR = "";
    private String firstGacTSI = "";
    private String firstGacCID = "";

    private TransResultEnum currTransResultEnum;
    private CvmResultEnum currentTxnCVMResult;
    private int currTransResultCode = RetCode.EMV_OK;
    private byte transType;

    private boolean isSecondTap = false;
    private IssuerRspData issuerRspData = new IssuerRspData();

    public interface onCancelClicked {
        void cancelClicked();
    }

    public onCancelClicked l;

    public static AppPreferenceHelper appPreferenceHelper;
    public static SecurityKeyManager securityKeyManager;
    public static LocalData localData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pos_loader_view);

        appPreferenceHelper = new AppPreferenceHelper(getApplicationContext());
        securityKeyManager = new SecurityKeyManager(getApplicationContext());
        localData = new LocalData(getApplicationContext());

        bottomView = findViewById(R.id.view_bottom);
        posViewUpdate = findViewById(R.id.pos_view_update);
        spinKit = findViewById(R.id.imageView);
        spinKit.setImageResource(R.drawable.card);

//        try {
////            spinKit = new GifImageView(getApplicationContext());
//            spinKit = findViewById(R.id.imageView);
////            spinKit = new GifImageView(getApplicationContext());
//            spinKit.setImageResource(R.drawable.card);
//        }catch (ExceptionInInitializerError e){
//            e.printStackTrace();
//        }


        Intent intent = getIntent();
        mutableLiveData = new MutableLiveData();

        creditCard = new CreditCard();

        if (isSecretKeyAdded()) {
            if (intent != null) {
                totalAmount = intent.getDoubleExtra(Constants.INTENT_EXTRA_AMOUNT_KEY, 0.0);
                accountType = intent.getStringExtra(Constants.INTENT_EXTRA_ACCOUNT_TYPE);
//                terminalId = intent.getStringExtra(Constants.TERMINAL_ID);
                transType = getIntent().getByteExtra(Constants.EXTRA_TRANS_TYPE, (byte) 0x00);

                totalAmountPrint = totalAmount;
                if (!TextUtils.isEmpty(String.valueOf(intent.getDoubleExtra(Constants.INTENT_EXTRA_AMOUNT_KEY, 0.0)))
                        && !TextUtils.isEmpty(intent.getStringExtra(Constants.INTENT_EXTRA_ACCOUNT_TYPE))
                ) {
                    totalAmount = intent.getDoubleExtra(Constants.INTENT_EXTRA_AMOUNT_KEY, 0.0);
                    accountType = intent.getStringExtra(Constants.INTENT_EXTRA_ACCOUNT_TYPE);
//                    terminalId = intent.getStringExtra(Constants.TERMINAL_ID);
                    totalAmountPrint = totalAmount;
                } else {
                    incompleteParameters();
                }
            } else {
                incompleteParameters();
                return;
            }
        } else {
            initializationError();
        }

        try {
            isConnected = mainApplication.isDeviceManagerConnetcted();
            Log.e(TAG, "isConnected " + isConnected);
        } catch (Exception e) {
            //  String errp = e.getLocalizedMessage();
            Log.e("RemoteException", e.getMessage());
            e.printStackTrace();
        }

//        viewObserver();
        proceedToPayment();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }


    private void initTransProcessPresenter() {
        if (transProcessPresenter == null) {
            transProcessPresenter = new TransProcessPresenter();
//            transProcessPresenter.setClearPinKey(paxDeviceGetClearPinKey()); //TODO MAKE THIS PART OF THE CONSTRUCTOR AND MAKE PRIVATE
//            transProcessPresenter.setEmvCallBack(callback);
            transProcessPresenter.attachView(this);
        }
    }

    private void transPreProcess(boolean isNeedContact) {
        try {

            EmvTransParam transParam = new EmvTransParam();
            Log.e(TAG, "transType:" + ConvertHelper.getConvert().bcdToStr(new byte[]{transType}) + ",int val:" + transType);

            transParam.setTransType(transType);
            transParam.setAmount(Long.toString(totalAmount.longValue()));
            transParam.setAmountOther(Long.toString(totalAmount.longValue()));
            transParam.setTerminalID(terminalId);
            Log.e("AppDataUtils getSN(): ", AppDataUtils.getSN());
            transParam.setTransCurrencyCode(Util.str2Bcd("0566"));
            transParam.setTransCurrencyExponent((byte) 2);
//            transParam.setTransCurrencyCode(CurrencyConverter.getCurrencyCode());
//            transParam.setTransCurrencyExponent((byte) CurrencyConverter.getCurrencyFraction()); //You can Log it to check
            transParam.setTransDate(AppDataUtils.getCurrDate());
            transParam.setTransTime(AppDataUtils.getCurrTime());
            transParam.setTransTraceNo("0001");

            transProcessPresenter.preTrans(transParam, isNeedContact);
//            EmvTransParam transParam = new EmvTransParam();
//            Log.e(TAG, "transType:" + ConvertHelper.getConvert().bcdToStr(new byte[]{transType}) + ",int val:" + transType);
//            transParam.setTransType(transType);
//            transParam.setAmount(Long.toString(transAmt));
//            transParam.setAmountOther(Long.toString(otherAmt));
//            transParam.setTerminalID(AppDataUtils.getSN());
//            transParam.setTransCurrencyCode(CurrencyConverter.getCurrencyCode());
//            transParam.setTransCurrencyExponent((byte) CurrencyConverter.getCurrencyFraction());
//            transParam.setTransDate(AppDataUtils.getCurrDate());
//            transParam.setTransTime(AppDataUtils.getCurrTime());
//            transParam.setTransTraceNo("0001");
//            transProcessPresenter.preTrans(transParam, isNeedContact);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    private void startDetectCard(EReaderType readType) {
        hasDetectedCard = false;
        TimeRecordUtils.clearTimeRecordList();
        Log.e("TAG", "Detecting Card.....");

        if (detectPresenter != null) {
            detectPresenter.stopDetectCard();
            detectPresenter.detachView();
            detectPresenter.closeReader();
            detectPresenter = null;
        }

          /* ============NOTE==============
             Detect card with API getPicc()/getIcc/getMag ==> DetectCardPresenter(PiccDetectModel,IccDetectModel,MagDetectModel),
             DetectCardPresenter has resolve the detect card conflict problem("when swipe card, some terminals may detect picc ,as a result of terminal's mag reader and picc reader are very close"),
             but it may increase the time of detecting card process
           */
//        detectPresenter = new DetectCardPresenter();

        // detect card with polling() ==> NeptunePollingPresenter
        detectPresenter = new NeptunePollingPresenter();
        detectPresenter.attachView(this);
        detectPresenter.startDetectCard(readType);
    }


    //-------------------------Pax interfaces Overidden--------------------------------------

    //Beginning of functions for  DetectCardContract Interface

    @Override
    public void onMagDetectOK(String pan, String expiryDate) {
        // magstripe Fallback(terminal fallback to a magstripe transaction when chip cannot be read)
        currentTxnType = TXN_TYPE_MAG;
        hasDetectedCard = true;
        panText.setVisibility(View.VISIBLE);
        expiryDateText.setVisibility(View.VISIBLE);

        panText.setText(pan);
        expiryDateText.setText(expiryDate);
        //add CVM process, such as enter pin or signature and so on.
        displayTransPromptDlg(PROMPT_TYPE_SUCCESS, "MSR");

    }

    @Override
    public void onIccDetectOK() {
        Log.e("onIccDetectOK ", "ICC Dected OKay ");
        currentTxnType = TXN_TYPE_ICC;
        hasDetectedCard = true;
        ToastUtils.showToast(PosActivity.this, "ICC detect succ");
        if (transProcessPresenter != null) {
//            Todo (1)
            Log.e("onIccDetectOK ", "Start Emv Trans");

            transProcessPresenter.startEmvTrans();
        }

    }

    @Override
    public void onPiccDetectOK() {
        Log.e("onPiccDetectOK", "PICC Dected OKay ");

        currentTxnType = TXN_TYPE_PICC;
        hasDetectedCard = true;
//        clssLightProcessing();
        if (transProcessPresenter != null) {
            if (currTransResultEnum == TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE) {

            } else if (currTransResultEnum == TransResultEnum.RESULT_TRY_AGAIN) {

            } else if (isSecondTap) {//visa card and other card(not contain master card) 2nd detect card
                isSecondTap = false;
                transProcessPresenter.completeClssTrans(issuerRspData);
            } else {
//                (1)
                transProcessPresenter.startClssTrans(); // first time detect card finish
            }
        }

    }

    @Override
    public void onDetectError(DetectCardResult.ERetCode errorCode) {
        //TODO Send the callback to notify the app consuming this library of callback
        if (errorCode == DetectCardResult.ERetCode.FALLBACK) {

            ToastUtils.showToast(PosActivity.this, "Fallback,Please insert card");
        } else {
            MyApplication.getINSTANCE().runOnUiThread(() -> {
                displayTransPromptDlg(PROMPT_TYPE_FAILED, errorCode.name());
            });
        }
    }

    //End of functions for  DetectCardContract Interface


    //Beginning of functions for TransProcessContract Interface

    @Override
    public void onUpdatePinLen(String pin) {
        MyApplication.getINSTANCE().runOnUiThread(() -> {
            if (pinText != null) {
                pinText.setText(pin);
                Log.d(TAG, "onUpdatePinLen: >>>>>>>>>>>>>>>>>>>>>> " + pin);
            }
        });
    }

    @Override
    public String getEnteredPin() {

        return pinText == null ? "" : pinText.getText().toString();
    }


    @Override
    public void onEnterPinFinish(int pinResult) {
        this.pinResult = pinResult;
        MyApplication.getINSTANCE().runOnUiThread(() -> {
            try {
                if (mEnterPinPopWindow != null && mEnterPinPopWindow.isShowing()) {
                    mEnterPinPopWindow.dismiss();
                }
                if (pinResult == EnterPinResult.RET_SUCC
                        || pinResult == EnterPinResult.RET_CANCEL
                        || pinResult == EnterPinResult.RET_TIMEOUT
                        || pinResult == EnterPinResult.RET_PIN_BY_PASS
                        || pinResult == EnterPinResult.RET_OFFLINE_PIN_READY
                        || pinResult == EnterPinResult.RET_NO_KEY) {
                    Log.e(TAG, "to do nothing");
                } else {
                    displayTransPromptDlg(PROMPT_TYPE_FAILED, pinResult + "");
                }
            } catch (Exception e) {

            }
        });

    }

    @Override
    public void onStartEnterPin(String prompt) {
        Log.e(TAG, "onStartEnterPin, current thread " + Thread.currentThread().getName() + ", id:" + Thread.currentThread().getId());
        MyApplication.getINSTANCE().runOnUiThread(() -> displayEnterPinDlg(prompt));
    }

    //    (4)
    //This gets response immediately the POS has finished checking the card (overriden method  - startEmvTrans in TransactionProcessPresenter )
    @Override
    public void onTransFinish(TransResult transResult) {
        currTransResultEnum = transResult.getTransResult();
        currentTxnCVMResult = transResult.getCvmResult();
        currTransResultCode = transResult.getResultCode();
        Log.e(TAG, "onTransFinish,retCode:" + currTransResultCode + ", transResult:" + currTransResultEnum + ", cvm result:" + transResult.getCvmResult());
        getFirstGACTag();
        if (transResult.getResultCode() == RetCode.EMV_OK) {
            //Check CVM in order to proceed   (5S - 1) S  Stands for Success
            processCvm();
        } else {
//            (5F - 1) F stands for fail
            //Tries to fallback to use other slots ie mag or icc or Picc
            processTransResult(transResult);
        }
    }

    //Triggered when completeEmvTransaction in the Transaction process presenter is called
    @Override
    public void onCompleteTrans(TransResult transResult) {
        currTransResultEnum = transResult.getTransResult();
        currTransResultCode = transResult.getResultCode();
        Log.e(TAG, "onCompleteTrans,retCode:" + transResult.getResultCode() + ", transResult:" + currTransResultEnum);
        if (transResult.getResultCode() == RetCode.EMV_OK) {
            //1.to Trans result page
        }
//        toTransResultPage();
//          ours
//        sendNotification(respCode);

    }

    @Override
    public void onRemoveCard() {
//        clssLighteErr();
        //TODO SEND  NOTIFICATION TO REMOVE CARD
//        useCardPromptText.setText("Please remove card");
//        useCardPromptText.setTextColor(Color.RED);
    }

    @Override
    public void onReadCardOK() {
        //Just light used here
        Log.e("Card Status: ", "Read Okay");
        Log.e("TAG", "Card Confirmed");

    }

//____________________End of functions for  TransProcessContract Interface_______________________


    //-----------------Pax External methods -----------------------

    //TODO This is where you will look into to send your callback
    private void displayTransPromptDlg(int type, String msg) {

        if (transPromptDlg != null) {
            if (transPromptDlg.isShowing()) {
                transPromptDlg.dismiss();
            }

            transPromptDlg = null;
        }
        if (type == PROMPT_TYPE_SUCCESS) {
            transPromptDlg = new AlertDialog.Builder(PosActivity.this).setCancelable(false).setIcon(R.mipmap.ic_dialog_alert_holo_light).setTitle("Transaction Prompt").setMessage(msg).create();
        } else {
            transPromptDlg = new AlertDialog.Builder(PosActivity.this).setCancelable(false).setIcon(R.mipmap.indicator_input_error).setTitle("Transaction Failed").setMessage("errCode:" + msg).create();
        }

        transPromptDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
//                ((Activity)context).finish();
                if (l != null) {
                    l.cancelClicked();
                }
            }
        });
        Log.e(TAG, "is Act Finish?" + isFinishing());

        if (!PosActivity.this.isFinishing()) {
            transPromptDlg.show();
            new TickTimer().start(3, () -> {
                if (transPromptDlg != null && transPromptDlg.isShowing()) {
                    transPromptDlg.dismiss();
                }
            });
        }

    }

    private void displayEnterPinDlg(String title) {
        if (isFinishing()) {
            return;
        }
        if (mEnterPinPopWindow != null) {
            if (mEnterPinPopWindow.isShowing()) {
                mEnterPinPopWindow.dismiss();
            }
            mEnterPinPopWindow = null;
        }

        View popView = getLayoutInflater().inflate(R.layout.dlg_enter_pin, null);
        mEnterPinPopWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pinText = popView.findViewById(R.id.tv_pin);
        TextView titleTxt = popView.findViewById(R.id.tv_title);
        titleTxt.setText(title);
        mEnterPinPopWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        mEnterPinPopWindow.setFocusable(true);
        mEnterPinPopWindow.setOutsideTouchable(false);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(200);
        mEnterPinPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                ScreenUtils.lightOn(((Activity)context));
                if (currentTxnType == TXN_TYPE_PICC) {
                    if (pinResult != 0) {
                        displayTransPromptDlg(PROMPT_TYPE_FAILED, "getString pinblock err: " + pinResult);
                    } else {
                        checkTransResult();
                    }
                }
            }
        });

        mEnterPinPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mEnterPinPopWindow.showAtLocation(bottomView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popView.startAnimation(animation);
//        ScreenUtils.lightOff((((Activity)context));
    }


    private void checkTransResult() {
        Log.e(TAG, "checkTransResult:" + currTransResultEnum);
        if (currTransResultEnum == TransResultEnum.RESULT_REQ_ONLINE) {
            // 1.online process 2.to result page
            onlineProcess();
        } else if (currTransResultEnum == TransResultEnum.RESULT_OFFLINE_APPROVED) {
         /*    //Commented this out
            //1.to result page
             toTransResultPage();
*/

            //Our Implementation
            onlineProcess();

        } else if (currTransResultEnum == TransResultEnum.RESULT_OFFLINE_DENIED) {
       /*     //Commented this out
            // 1.to result page
//            toTransResultPage();   //TODO CHECK IF YOU ARE ALLOWED TO SEND TO NIBSS .
            */

            //Our Implementation
            Log.e("TAG", "Failed to Read Card Please try Again");
        } else {
            Log.e(TAG, "unexpected result," + currTransResultEnum);

            Log.e("TAG", "unexpected result");

        }
    }

    private void onlineProcess() {
        //====online process =====
        //1.get TAG value with getTlv API
        //2.pack message, such as ISO8583
        //3.send message to acquirer host
        //4.get response of acquirer host
        //5.set value of acquirer result code and script, such as TAG 71(Issuer Script Data 1),72(Issuer Script Data 2),91(Issuer Authentication Data),8A(Response Code),89(Authorization Code) and so on.
        //6.call completeTransProcess API


//          Commented this Out  TODO
      /*  //There is a time-consuming wait dialog to simulate the online process
        displayProcessDlg(PROCESSING_TYPE_ONLINE, "Online Processing...");*/
        proceedToExChangeData(SUCCESS_CODE, creditCard);

    }

    private void displayProcessDlg(int type, String msg) {
        if (isFinishing()) {
            return;
        }
        if (processingDlg != null) {
            if (processingDlg.isShowing()) {
                processingDlg.dismiss();
            }

            processingDlg = null;
        }

        AlertDialog.Builder mProgressDlgBuilder = new AlertDialog.Builder(PosActivity.this, R.style.AlertDialog);
        View view = LayoutInflater.from(PosActivity.this).inflate(R.layout.dlg_processing, null);
        ((TextView) view.findViewById(R.id.tv_msg)).setText(msg);
        mProgressDlgBuilder.setCancelable(false);
        mProgressDlgBuilder.setView(view);
        processingDlg = mProgressDlgBuilder.create();
        processingDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (type == PROCESSING_TYPE_ONLINE) {
                    //TODO WILL GUIDE IN WRITING THE SCRIPT TO THE CARD
                    //show dialog to select online approve or online decline simulate online result
//                    displaySelectOnlineResultDlg();

//                    ours
                    onlineProcess();

                } else if (type == PROCESSING_TYPE_SIGNATURE) {
                    checkTransResult();
                }
            }
        });


        processingDlg.show();

        // TODo Sets time for the scrolling
        new TickTimer().start(3, () -> {
            if (processingDlg != null && processingDlg.isShowing()) {
                processingDlg.dismiss();
            }
        });

        final WindowManager.LayoutParams params = processingDlg.getWindow().getAttributes();
        params.width = 600;
        params.height = 400;
        processingDlg.getWindow().setAttributes(params);
        processingDlg.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
    }

    private void processTransResult(TransResult transResult) {
        //check if need to show rea light first
//        showClssErrLight(transResult);

        if (currTransResultEnum == TransResultEnum.RESULT_FALLBACK) { //contact
            //TODO Prompt the User to fallback to swipe card

//            useCardPromptText.setTextColor(Color.RED);
//            useCardPromptText.setText(" Fallback, Please swipe card");
            ToastUtils.showToast(PosActivity.this, "Fallback, Please swipe card");
            startDetectCard(EReaderType.MAG); // onMagDetectOk will callback
        } else if (currTransResultEnum == TransResultEnum.RESULT_CLSS_SEE_PHONE) { //contactless
            //PICC return  USE_CONTACT 1.restart detect(insert/swipe) card and transaction
            startClssTransAgain("See phone, Please tap phone");
        } else if (currTransResultEnum == TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE
                || transResult.getResultCode() == RetCode.CLSS_USE_CONTACT) {//contactless
            //TODO SEND  NOTIFICATION TO "Try other interface, Please Insert card"

//            useCardPromptText.setTextColor(Color.RED);
//            useCardPromptText.setText("Try other interface, Please Insert card");
            ToastUtils.showToast(PosActivity.this, "Try other interface, Please Insert card");

//            clssLightCloseAll();
            startDetectCard(EReaderType.ICC);
        } else if (currTransResultEnum == TransResultEnum.RESULT_TRY_AGAIN) {//contactless
            //PICC return  USE_CONTACT 1.restart detect card and transaction
            startClssTransAgain("Try again, Please tap card again");
        } else if (transResult.getResultCode() == RetCode.EMV_DENIAL
                || transResult.getResultCode() == RetCode.CLSS_DECLINE) {
            //to result page to get tag95 and tag 9b to find the reason of deciline
            Log.e("TAG", "EMV PROCESS FAILED ");
            ToastUtils.showToast(PosActivity.this, "Try other interface, Please Insert card");

//            toTransResultPage();
        } else {
            displayTransPromptDlg(PROMPT_TYPE_FAILED, transResult.getResultCode() + "");
        }
    }


    private void getFirstGACTag() {
        ByteArray byteArray = new ByteArray();

        int ret = EmvProcess.getInstance().getTlv(0x95, byteArray);
        if (ret == RetCode.EMV_OK) {
            byte[] dataArr = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length);
            firstGacTVR = ConvertHelper.getConvert().bcdToStr(dataArr);
        }

        byteArray = new ByteArray();
        ret = EmvProcess.getInstance().getTlv(0x9B, byteArray);
        if (ret == RetCode.EMV_OK) {
            byte[] dataArr = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length);
            firstGacTSI = ConvertHelper.getConvert().bcdToStr(dataArr);
        }

        byteArray = new ByteArray();
        ret = EmvProcess.getInstance().getTlv(0x9F27, byteArray);
        if (ret == RetCode.EMV_OK) {
            byte[] dataArr = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length);
            firstGacCID = ConvertHelper.getConvert().bcdToStr(dataArr);
        }
    }

    private void processCvm() {
        Log.e("CvmResultEnum.CVM_ONLINE_PIN currentTxnCVMResult", "" + currentTxnCVMResult);
        //get TransResult
        if (currentTxnCVMResult == CvmResultEnum.CVM_NO_CVM) {
            //1.check trans result because there is no CVM ie if online card be ready to popup the pinpad
//            (6) One of the phase cvm will lead to
            checkTransResult();
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_SIG) {
            //1.signature process 2.check trans result
            //            (6)
            signatureProcess();
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_ONLINE_PIN) {

            if (currentTxnType == TXN_TYPE_PICC) {//Online Pin for PICC
                //1.online pin process 2.check trans result
                transProcessPresenter.startOnlinePin();
            } else if (currentTxnType == TXN_TYPE_ICC) { //Online pin for ICC
                //check result
                //            (6)
                checkTransResult();
            }
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_ONLINE_PIN_SIG) {
            if (currentTxnType == TXN_TYPE_PICC) {
                //picc no this cvm
            } else if (currentTxnType == TXN_TYPE_ICC) {
                //1.signature process 2.check trans result
                //            (6)
//                signatureProcess();
            }
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_OFFLINE_PIN) {//contact trans
            //1.check trans result
            //            (6)
            checkTransResult();
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_CONSUMER_DEVICE) {//contactless trans
            //1.restart detect(tap) card and transaction
            //            (6)
            startClssTransAgain("See phone, Please tap phone");
        }
    }


    private void startClssTransAgain(String msg) {
        detectPresenter.closeReader();
        transPreProcess(false);
        isSecondTap = false;
        //TODO SEND  NOTIFICATION TO display msg

//        useCardPromptText.setTextColor(Color.RED);
//        useCardPromptText.setText(msg);
//        clssLightDetectCard();
        startDetectCard(EReaderType.PICC);
    }

    private void signatureProcess() {
        //There is a time-consuming wait dialog to simulate the signature process
//        TODO No request for signature will enanle this method on request
        displayProcessDlg(PROCESSING_TYPE_SIGNATURE, "Signature Processing...");


        //Ours
//        callback.onNotifyEmvStatus(new Status(DeviceState.AWAITING_ONLINE_RESPONSE, "Awaiting online response"));

    }

    //-----------------End of Pax External methods -----------------------


    private void stopDetectCard() {
        if (detectPresenter != null) {
            detectPresenter.stopDetectCard();
            detectPresenter.detachView();
            detectPresenter.closeReader();
            detectPresenter = null;
        }
    }


    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDetectCard();
        if (transProcessPresenter != null) {
            transProcessPresenter.detachView();
            transProcessPresenter = null;
        }
    }

    private static Handler mmHandler = new Handler(Looper.getMainLooper());

    public static boolean checkDeviceStatus() {
        boolean isValid = false;
        try {
            isValid = true;
        } catch (Exception e) {
            Log.e("checkDeviceStatus", e.getMessage());
        }
        return isValid;
    }


    public static void init(String secretKey, Context context, TerminalKeyParamDownloadListener listener) {
        if (!TextUtils.isEmpty(secretKey)) {
            try {
                new Pos().init(context.getApplicationContext());

//                appPreferenceHelper = new AppPreferenceHelper(context);
//                securityKeyManager = new SecurityKeyManager(context);
                localData = new LocalData(context);

//                KeyDownloadRequest keyDownloadRequest = new KeyDownloadRequest();
//                keyDownloadRequest.terminalId = terminalId;
//                new DownloadService(securityKeyManager, localData, appPreferenceHelper).ProcessKeyDownload(keyDownloadRequest);

                Log.e("Check Param Download", String.valueOf(SharedPreferencesUtils.getInstance().getBooleanValue(Constants.INTENT_TERMINAL_CONFIG, false)));
                Log.e("Check Key Download", String.valueOf(SharedPreferencesUtils.getInstance().getBooleanValue(Constants.INTENT_KEY_CONFIG, false)));

                if (!SharedPreferencesUtils.getInstance().getBooleanValue(Constants.INTENT_TERMINAL_CONFIG, false)){
                    Log.e("Check Config Download", "Download now");
                    downloadConfigurations(context, listener);
                }else if(!SharedPreferencesUtils.getInstance().getBooleanValue(Constants.INTENT_KEY_CONFIG, false)){
                    Log.e("Check Key Download", "Download now");
                    downloadConfigurations(context, listener);
                }else{
                    Log.e("Check Config Download", "Do nothing");
                }

                terminalId = localData.getTerminalId();
                MemoryManager.getInstance().putUserSecretKey(secretKey);
            } catch (Exception e) {
                Log.e("init", e.getMessage());
            }
        } else {
            Log.e("init", "Secret Key is Empty");
        }
    }

    public static void clearData(Context context) {
        new Pos().init(context.getApplicationContext());
        SharedPreferencesUtils.getInstance().clear();
    }

    public static void downloadConfigurations(Context context, TerminalKeyParamDownloadListener listener) {
        try {
            AppExecutors.getInstance().diskIO().execute(() -> {
                appPreferenceHelper = new AppPreferenceHelper(context);
                securityKeyManager = new SecurityKeyManager(context);
                localData = new LocalData(context);

                String serial = null;
                serial = Build.SERIAL;
                Log.e("Check Terminal Serial", serial);
                new DownloadService(securityKeyManager, localData, appPreferenceHelper).downloadTerminalParam(serial, listener);
            });

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private Boolean isSecretKeyAdded() {
        return MemoryManager.getInstance().isSecretActivated();
    }


    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "bitMap  =" + image);
        return image;
    }


    private static void setPrintLevel(int level) {
//        try {
//            printer.setPrintGray(level);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

//    private void viewObserver() {
//        mutableLiveData.observe(this, s -> {
//            if (!TextUtils.isEmpty(s)) {
//                showResult(posViewUpdate, s);
//            }
//        });
//    }


    private void proceedToPayment() {
//        if (!isConnected) {
//            posViewUpdate.setText(R.string.err_not_support_api);
//            FailedTransaction();
//            return;
//        }
        try {

            initTransProcessPresenter();
            transPreProcess(true);
            startDetectCard(EReaderType.ICC_PICC);

            try {
                isConnected = mainApplication.isDeviceManagerConnetcted();

                Log.e(TAG, "isConnected " + isConnected);
            } catch (Exception e) {
                //  String errp = e.getLocalizedMessage();
                Log.e("RemoteException", e.getMessage());
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getDeviceMac() {
        try {
            return Build.SERIAL;
        } catch (Exception e) {
            Log.e("getDeviceMac", e.getLocalizedMessage());
        }
        return "";
    }

    public static String getDeviceModel() {
        try {
//            Bundle devInfo = DeviceHelper.getSysHandle().getDeviceInfo();
//            return devInfo.getString(Build.MODEL);
            return Build.MODEL;
        } catch (Exception e) {
            Log.e("getDeviceMac", e.getLocalizedMessage());
        }
        return "";
    }

    private void proceedToExChangeData(String responseCode, CreditCard creditCard) {

//        if (creditCard.getPIN() == null) {
//            FailedTransaction();
//            return;
//        }

        TerminalInfo response = showTerminalEmvTransResult(accountType, totalAmount.longValue(), creditCard.getPIN(), responseCode, getDeviceMac());

        KSNUtilities ksnUtilitites = new KSNUtilities();
//          String workingKey = ksnUtilitites.getWorkingKey("3F2216D8297BCE9C", "000002DDDDE00002");

        String workingKey = ksnUtilitites.getWorkingKey("3F2216D8297BCE9C", getInitialKSN());
        String pinBlock = ksnUtilitites.DesEncryptDukpt(workingKey, response.pan, cPin);
        ksn = ksnUtilitites.getLatestKsn();

        response.ksn = ksn;
        response.pinBlock = creditCard.getPIN();
//        response.terminalId = terminalId;
        cPin = creditCard.getPIN();
//        response.amount = String.valueOf(totalAmount.longValue());

        // response.cardOwner = creditCard.getHolderName();
        stopEmvProcess(response);
    }


    private static TerminalInfo getDefaultTerminalInfo() {
        TerminalInfo terminalInfo = new TerminalInfo();
        terminalInfo.batteryInformation = "100";
        terminalInfo.languageInfo = "EN";
        terminalInfo.posConditionCode = "00";
        terminalInfo.printerStatus = "1";
        //  terminalInfo.minorAmount = "000000000001";
        terminalInfo.TransactionType = "00";
        terminalInfo.posEntryMode = "051";
        terminalInfo.posDataCode = "510101511344101";
        terminalInfo.posGeoCode = "00234000000000566";
        terminalInfo.pinType = "Dukpt";
        terminalInfo.stan = getNextStan();
        terminalInfo.AmountOther = "000000000000";
        terminalInfo.TransactionCurrencyCode = "0566";
        terminalInfo.TerminalCountryCode = "566";
        terminalInfo.TerminalType = "22";
        return terminalInfo;
    }


    public static String getNextStan() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date()).substring(8);
    }

    public TerminalInfo showTerminalEmvTransResult(String accountType, long amountTotal, String _PinBlock, String _responseCode, String deviceMacAddress) {
        TerminalInfo terminalInfo = getDefaultTerminalInfo();
        terminalInfo.fromAccount = "Default";// getAccountTypeString(accountType);
        terminalInfo.responseCode = _responseCode;
        terminalInfo.responseDescription = "Data collected successfully";
        terminalInfo.TerminalName = "smartpos";

        if (!(transProcessPresenter.isEmptyOrLessThanThree)) {

            if ((currTransResultEnum == TransResultEnum.RESULT_REQ_ONLINE) || (currTransResultEnum == TransResultEnum.RESULT_OFFLINE_APPROVED)) {

                Log.e(TAG, "onlineProcess: " + "Both Conditions Passed ");
//                EmvCard.PinInfo pinInfo = transProcessPresenter.getPinInfo();
//                EmvCardType cardType = transProcessPresenter.getCardType();
//                Log.e(TAG, "onlineProcess: EMV CARD TYPE  " + cardType.name());
                String iccData = "";
                if (TXN_TYPE_PICC == currentTxnType) {
                    iccData = Util.getTLVData_contactless(currentTxnType, transType);
                } else {
                    iccData = Util.getTLVData(currentTxnType, transType);
                }

                Log.e(TAG, "onlineProcess: EMV CARD Icc Data  " + iccData);

                String IAD = Util.getTagVal(0x9F10, currentTxnType, transType);
                Log.e(TAG, "onlineProcess: EMV CARD IAD " + IAD);

                String terminalCap = Util.getTagVal(0x9F33, currentTxnType, transType);
                Log.e(TAG, "onlineProcess: Terminal capability 9F33 " + terminalCap);


                String terminalCapL = Util.getTagVal(0x9f33, currentTxnType, transType);
                Log.e(TAG, "onlineProcess: Terminal capability large  9f33  " + terminalCapL);

                String _holderName = Util.getTagVal(0x5F20, currentTxnType, transType).replace(" ", "");
                String strHolder = Util.convertHexToString(_holderName);

                String holderName = strHolder.replace(" ", "");
                Log.e(TAG, "onlineProcess: holderName  5F20  " + holderName);

                String strTracktwo = Util.getTagVal(0x57, currentTxnType, transType);
                Log.e(TAG, "onlineProcess: Track2  57  " + strTracktwo);


                String strAid = Util.getTagVal(0x84, currentTxnType, transType);
                String strTvr = Util.getTagVal(0x95, currentTxnType, transType);
                String strTsi = Util.getTagVal(0x9F34, currentTxnType, transType);

                String cardHolderName = "Customer";

                try {
                    cardHolderName = holderName;
                } catch (Exception e) {

                }

                try {
//                    if (HexDump.decBytesToHex(getTag(0x5F20)) != null) {
//                        String name = HexDump.decBytesToHex(getTag(0x5A));
//                        terminalInfo.cardOwner = HexDump.formatHexString(HexDump.decBytesToHex(getTag(0x5F20)));
//                    } else {
//                        terminalInfo.cardOwner = "CUSTOMER / INSTANT";
//                    }
                    terminalInfo.cardOwner = "CUSTOMER / INSTANT";
//                    Log.d(TAG, "ICC Data: " + "\n" + tlv);
                    terminalInfo.iccData = iccData;
                    terminalInfo.DedicatedFileName = Util.getTagVal(0x84, currentTxnType, transType);
                    terminalInfo.CvmResults = Util.getTagVal(0x9F34, currentTxnType, transType);
                    terminalInfo.ApplicationInterchangeProfile = Util.getTagVal(0x82, currentTxnType, transType);
                    terminalInfo.TerminalVerificationResult = Util.getTagVal(0x95, currentTxnType, transType);
                    terminalInfo.TransactionDate = Util.getTagVal(0x9A, currentTxnType, transType);
                    terminalInfo.CryptogramInformationData = "80";
                    terminalInfo.Cryptogram = Util.getTagVal(0x9F26, currentTxnType, transType);
                    terminalInfo.TerminalCapabilities = Util.getTagVal(0x9F33, currentTxnType, transType);
                    terminalInfo.cardSequenceNumber = Util.getTagVal(0x5F34, currentTxnType, transType);
                    terminalInfo.atc = Util.getTagVal(0x9F36, currentTxnType, transType);
                    terminalInfo.iad = Util.getTagVal(0x9F10, currentTxnType, transType);
                    terminalInfo.track2 = Util.getTagVal(0x57, currentTxnType, transType);
                    String strTrack2 = terminalInfo.track2.split("F")[0];
                    String pan = strTrack2.split("D")[0];
                    String expiry = strTrack2.split("D")[1].substring(0, 4);
                    terminalInfo.track2 = strTrack2;
                    terminalInfo.pan = pan;
                    terminalInfo.expiryYear = expiry.substring(0, 2);
                    terminalInfo.expiryMonth = expiry.substring(2);
                    terminalInfo.AmountAuthorized = Util.getTagVal(0x9F02, currentTxnType, transType);
                    terminalInfo.UnpredictableNumber = Util.getTagVal(0x9F37, currentTxnType, transType);
//                    if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_APNAME) != null) {
//                        //terminalInfo.CardType = tlvDataList.getTLV(EmvTags.EMV_TAG_IC_APNAME).getGBKValue();
//                    }
                    String reult = new Gson().toJson(terminalInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e(TAG, "onlineProcess:cardHolderName " + cardHolderName);

                Log.e("TAG", "Awaiting online response");
            }
        } else {
            Log.e("TAG", "unexpected result");
        }


        return terminalInfo;
    }

    public void getPinAndKSNData(String pinBlock, String KSN) {
        ksn = KSN;
        // pinBlc = pinBlock;
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
        cPin = result;
        Log.d("result card ", result);
    }

    private String getInitialKSN() {
        SharedPreferences sharedPref = getSharedPreferences("KSNCOUNTER", Context.MODE_PRIVATE);
        int ksn = sharedPref.getInt("KSN", 00001);
        if (ksn > 9999) {
            ksn = 00000;
        }
        int latestKSN = ksn + 1;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("KSN", latestKSN);
        editor.apply();
        return "0000000002DDDDE" + String.format("%05d", latestKSN);
    }

    String time = new TimeUtil().getTimehhmmss(new Date(System.currentTimeMillis()));
    String date = new TimeUtil().getDateMMdd(new Date(System.currentTimeMillis()));

    private void stopEmvProcess(TerminalInfo response) {
        Log.d("Processing", "Transaction" + "Please wait");
        showResult(posViewUpdate, "Processing Transaction....");
        String mTerminal = new Gson().toJson(response);
        BlusaltTerminalInfo blusaltTerminalInfo = new Gson().fromJson(mTerminal, BlusaltTerminalInfo.class);
        blusaltTerminalInfo.deviceOs = "Android";
        blusaltTerminalInfo.serialNumber = getDeviceMac();
        blusaltTerminalInfo.device = "Smart POS " + getDeviceModel();
        blusaltTerminalInfo.currency = "NGN";
        blusaltTerminalInfo.currencyCode = "566";

        LocalData localData = new LocalData(getApplicationContext());
        String getTsk = localData.getTsk();
        String getTerminalId = localData.getTerminalId();
        String getMerchantId = localData.getMerchantId();
        String getMerchantLoc = localData.getMerchantLoc();
        String getMerchantCategoryCode = localData.getMerchantCategoryCode();

        Log.e("Processing", "KeyDownloadResponse" + getTsk);
        Log.e("Processing", "KeyDownloadResponse" + response.pinBlock);
        Log.e("Processing", "KeyDownloadResponse" + getMerchantId);

        cPin = appPreferenceHelper.getSharedPreferenceString(Constants.PIN_BLOCK);
        response.terminalId = getTerminalId;
        response.sessionKey = getTsk;
        TerminalInformation terminalInformation = new TerminalInformation();
        terminalInformation.merchantID = getMerchantId;
        terminalInformation.merchantNameAndLocation = getMerchantLoc;
        terminalInformation.merchantCategoryCode = getMerchantCategoryCode;
        terminalInformation.posConditionCode = "00";
        terminalInformation.posEntryMode = "051";
        terminalInformation.terminalId = response.terminalId;
        response.terminalInformation = terminalInformation;

        response.processingCode = "000000";
        response.de62 = "MA";
        response.de63 = "05660566";

        String serviceCode = new ValueGenerator().getServiceCode(response.track2, response.pan);
        CardData cardData = new CardData();
        cardData.cardHolderName = response.cardOwner;
        cardData.cardSequenceNumber = response.cardSequenceNumber;
        cardData.expiryDate = response.expiryYear + response.expiryMonth;
        cardData.pan = response.pan;
        cardData.serviceCode = response.cardSequenceNumber;
        cardData.track2Data = response.track2;
        response.cardData = cardData;

        EmvData emvData = new EmvData();
        emvData.pinData = cPin;
        emvData.iccData = response.iccData;
        response.emvData = emvData;


        response.currencyCode = "566";
        response.currency = "NGN";
        response.deviceOs = "Android";
        response.serialNumber = getDeviceMac();
        response.device = "Smart POS " + getDeviceModel();
        String rtt = new Gson().toJson(blusaltTerminalInfo);
//        ProcessTransaction(blusaltTerminalInfo);
//        onCompleteTransaction(response);


        TerminalInfoProcessor terminalInfoProcessor = new TerminalInfoProcessor();
        terminalInfoProcessor.AmountAuthorized = response.AmountAuthorized;
        terminalInfoProcessor.cardOwner = response.cardOwner;
        terminalInfoProcessor.cardSequenceNumber = response.cardSequenceNumber;
        terminalInfoProcessor.expiryDate = response.expiryYear + response.expiryMonth;
        terminalInfoProcessor.pan = response.pan;
        terminalInfoProcessor.serviceCode = response.cardSequenceNumber;
        terminalInfoProcessor.track2 = response.track2;
        terminalInfoProcessor.currencyCode = response.currencyCode;
        terminalInfoProcessor.currency = response.currency;
        terminalInfoProcessor.de62 = response.de62;
        terminalInfoProcessor.de63 = response.de63;
        terminalInfoProcessor.iccData = response.iccData;
        terminalInfoProcessor.pinData = cPin;
        terminalInfoProcessor.AmountOther = response.AmountOther;
        terminalInfoProcessor.processingCode = response.processingCode;
        terminalInfoProcessor.rrn = new ValueGenerator().retrievalReferenceNumber();
        terminalInfoProcessor.sessionKey = response.sessionKey;
        terminalInfoProcessor.stan = new ValueGenerator().systemTraceAuditNumber();
        terminalInfoProcessor.merchantCategoryCode = getMerchantCategoryCode;
        terminalInfoProcessor.terminalMerchantID = getMerchantId;
        terminalInfoProcessor.merchantNameAndLocation = getMerchantLoc;
        terminalInfoProcessor.posConditionCode = "00";
        terminalInfoProcessor.posEntryMode = "051";
        terminalInfoProcessor.terminalId = response.terminalId;
        terminalInfoProcessor.TransactionDate = date;
        terminalInfoProcessor.transactionDateTime = date + time;
        terminalInfoProcessor.transactionTime = time;
        terminalInfoProcessor.responseCode = "00";
        terminalInfoProcessor.responseDescription = "Data collected successfully";
        Log.e("PROCESSOR DATA ", new Gson().toJson(terminalInfoProcessor));

        BlusaltTerminalInfoProcessor blusaltTerminalInfoProcessor = new BlusaltTerminalInfoProcessor();
        blusaltTerminalInfoProcessor.AmountAuthorized = response.AmountAuthorized;
        blusaltTerminalInfoProcessor.cardOwner = response.cardOwner;
        blusaltTerminalInfoProcessor.cardSequenceNumber = response.cardSequenceNumber;
        blusaltTerminalInfoProcessor.expiryDate = response.expiryYear + response.expiryMonth;
        blusaltTerminalInfoProcessor.pan = response.pan;
        blusaltTerminalInfoProcessor.serviceCode = response.cardSequenceNumber;
        blusaltTerminalInfoProcessor.track2 = response.track2;
        blusaltTerminalInfoProcessor.currencyCode = response.currencyCode;
        blusaltTerminalInfoProcessor.currency = response.currency;
        blusaltTerminalInfoProcessor.de62 = response.de62;
        blusaltTerminalInfoProcessor.de63 = response.de63;
        blusaltTerminalInfoProcessor.iccData = response.iccData;
        blusaltTerminalInfoProcessor.pinData = cPin;
        blusaltTerminalInfoProcessor.AmountOther = response.AmountOther;
        blusaltTerminalInfoProcessor.processingCode = response.processingCode;
        blusaltTerminalInfoProcessor.rrn = new ValueGenerator().retrievalReferenceNumber();
        blusaltTerminalInfoProcessor.sessionKey = response.sessionKey;
        blusaltTerminalInfoProcessor.stan = new ValueGenerator().systemTraceAuditNumber();
        blusaltTerminalInfoProcessor.merchantCategoryCode = getMerchantCategoryCode;
        blusaltTerminalInfoProcessor.terminalMerchantID = getMerchantId;
        blusaltTerminalInfoProcessor.merchantNameAndLocation = getMerchantLoc;
        blusaltTerminalInfoProcessor.posConditionCode = "00";
        blusaltTerminalInfoProcessor.posEntryMode = "051";
        blusaltTerminalInfoProcessor.terminalId = response.terminalId;
        blusaltTerminalInfoProcessor.TransactionDate = date;
        blusaltTerminalInfoProcessor.transactionDateTime = date + time;
        blusaltTerminalInfoProcessor.transactionTime = time;
        Log.e("PROCESSORTransactionData", new Gson().toJson(blusaltTerminalInfoProcessor));

//        ProcessProcessorTransaction(blusaltTerminalInfoProcessor);
        onCompleteTransaction(terminalInfoProcessor);
    }

    private void ProcessProcessorTransaction(BlusaltTerminalInfoProcessor blusaltTerminalInfoProcessor) {
        RetrofitClientInstance.getInstance().getDataService().postTransactionToProcessor(blusaltTerminalInfoProcessor).enqueue(new Callback<TerminalResponse>() {
            @Override
            public void onResponse(@NonNull Call<TerminalResponse> call, @NonNull Response<TerminalResponse> response) {
                Log.e("ProcessTransaction", "ProcessTransaction" + response);
                TerminalResponse terminalResponse = new TerminalResponse("card payment failed", "01", "Unable to process transaction");
                if (response.isSuccessful()) {
                    Log.e("ProcessTransaction", "ProcessTransaction response.body()" + response.body());

                    if (response.body().message.contains("Access denied! invalid apiKey passed")) {
                        terminalResponse.responseCode = "01";
                        terminalResponse.responseDescription = "Access denied! invalid apiKey passed";

                        Log.e("ProcessTransaction", "ProcessTransaction err" + new Gson().toJson(terminalResponse));
                        apiResponseCall(terminalResponse);
                    } else {
                        terminalResponse = response.body();
                        terminalResponse.responseCode = "00";
                        terminalResponse.responseDescription = "card payment successful";

                        Log.e("ProcessTransaction", "ProcessTransaction isSuccessful" + new Gson().toJson(terminalResponse));
                        apiResponseCall(terminalResponse);
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<TerminalResponse>() {
                        }.getType();
                        terminalResponse = gson.fromJson(response.errorBody().charStream(), type);
                        terminalResponse.responseCode = "01";
                        terminalResponse.responseDescription = "card payment failed";

                        Log.e("ProcessTransaction", "ProcessTransaction failed" + new Gson().toJson(terminalResponse));
                        apiResponseCall(terminalResponse);
                    } catch (Exception e) {
                        Log.e("ProcessTransaction", "ProcessTransaction failed" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TerminalResponse> call, @NonNull Throwable t) {
                TerminalResponse terminalResponse = new TerminalResponse();
                Log.e("ProcessTransaction", "ProcessTransaction onFailure" + t.getMessage());
                terminalResponse.message = t.getMessage();
                terminalResponse.responseCode = "02";
                terminalResponse.responseDescription = "Unable to connect to the server";
                apiResponseCall(terminalResponse);
            }
        });
    }


    private void ProcessTransaction(BlusaltTerminalInfo blusaltTerminalInfo) {
        RetrofitClientInstance.getInstance().getDataService().postTransactionToMiddleWare(blusaltTerminalInfo).enqueue(new Callback<BaseData<TerminalResponse>>() {
            @Override
            public void onResponse(@NonNull Call<BaseData<TerminalResponse>> call, @NonNull Response<BaseData<TerminalResponse>> response) {
                Log.d("ProcessTransaction", "ProcessTransaction" + response);
                TerminalResponse terminalResponse = new TerminalResponse("card payment failed", "01", "Unable to process transaction");
                if (response.isSuccessful()) {

                    if (response.body().getMessage().contains("Access denied! invalid apiKey passed")) {
                        terminalResponse.responseCode = "01";
                        terminalResponse.responseDescription = "card payment failed";
                        apiResponseCall(terminalResponse);
                    } else {
                        terminalResponse = Objects.requireNonNull(response.body()).getData();
                        terminalResponse.responseCode = "00";
                        terminalResponse.responseDescription = "card payment successful";
                        apiResponseCall(terminalResponse);
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<TerminalResponse>() {
                        }.getType();
                        terminalResponse = gson.fromJson(response.errorBody().charStream(), type);
                        terminalResponse.responseCode = "01";
                        terminalResponse.responseDescription = "card payment failed";
                        apiResponseCall(terminalResponse);
                    } catch (Exception e) {
                        Log.d("ProcessTransaction", "ProcessTransaction" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseData<TerminalResponse>> call, @NonNull Throwable t) {
                TerminalResponse terminalResponse = new TerminalResponse();
                Log.d("ProcessTransaction", "ProcessTransaction" + t.getMessage());
                terminalResponse.status = false;
                terminalResponse.message = t.getMessage();
                terminalResponse.responseCode = "02";
                terminalResponse.responseDescription = "Unable to connect to the server";
                apiResponseCall(terminalResponse);
            }
        });
    }

    @Override
    public void onCompleteTransaction(TerminalInfoProcessor response) {
        try {
            String fullPay = new Gson().toJson(response);
            showResult(posViewUpdate, "");
            Log.e("TRANS DONE", new Gson().toJson(response));
            Intent intent = new Intent();
            //intent.putExtra(getString(R.string.data), response);
            intent.putExtra(getString(R.string.data), fullPay);
            setResult(Activity.RESULT_OK, intent);
            finish();
        /*
            Call<Object> userCall = mApiService.performTransaction("98220514989004", "Horizonpay", "K11", "1.0.0", msg);
            userCall.enqueue(new Callback<Object>() {

                @Override
                public void onResponse(Call<Object> call, Response<Object> res) {
                    if (res.code() == 200) {
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.data), response);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
//                        SharedPreferencesUtils.getInstance().setValue(SharedPreferencesUtils.KEYS, new Gson().toJson(response.body()));
                    }
                }
                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.data), response);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    t.printStackTrace();
                }
            });
         */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProcessingError(RuntimeException message, int errorcode) {
        try {
            if (!isFinishing()) {
                onCompleteTransaction(StringUtils.getTransactionTesponse(message.getMessage(), errorcode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void apiResponseCall(TerminalResponse terminalResponse) {
        Intent intent = new Intent();
        String fullPay = new Gson().toJson(terminalResponse);
        intent.putExtra(getString(R.string.data), fullPay);
        Log.e("TRANS DONE", new Gson().toJson(terminalResponse));
        // prepareForPrinters(PosActivity.this,terminalResponse);
        finishTransaction(intent);
    }

    private void FailedTransaction() {
        TerminalResponse terminalResponse = new TerminalResponse();
        Log.d("FailedTransaction", "FailedTransaction" + "Unable to process transaction");
        terminalResponse.status = false;
        terminalResponse.message = "Unable to process transaction";
        terminalResponse.responseCode = "03";
        terminalResponse.responseDescription = "Card Malfunction";
        apiResponseCall(terminalResponse);
    }

    private void incompleteParameters() {
        Intent intent = new Intent();
        TerminalResponse response = new TerminalResponse();
        response.responseCode = incompleteParameters;
        response.responseDescription = "Incomplete Parameter";
        String fullPay = new Gson().toJson(response);
        intent.putExtra(getString(R.string.data), fullPay);
        finishTransaction(intent);
    }

    private void initializationError() {
        Intent intent = new Intent();
        TerminalInfo response = new TerminalInfo();
        response.responseCode = initializationError;
        response.responseDescription = ERROR;
        String fullPay = new Gson().toJson(response);
        intent.putExtra(getString(R.string.data), fullPay);
        finishTransaction(intent);
    }

    private void finishTransaction(Intent intent) {
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public static TerminalInfoProcessor getOnBackPressResponse(Double totalAmount) {
        TerminalInfoProcessor response = new TerminalInfoProcessor();
        response.responseCode = "407";
        response.responseDescription = "Transaction could not complete";
        response.AmountAuthorized = String.valueOf(totalAmount);
        return response;
    }

    @Override
    public void onBackPressed() {
        stopDetectCard();
        onCompleteTransaction(getOnBackPressResponse(totalAmount));
    }

    public static void prepareForPrinter(Context context, TerminalResponse printerModel) {
//        if (printer == null) {
            try {
//                printer = DeviceHelper.getPrinter();
                printActions(context, printerModel);
            } catch (Exception e) {
                Log.e("prepareForPrinters", e.getMessage());
            }
//        } else {
//            printActions(context, printerModel);
//        }
    }

    public static void prepareForPrinter(Context context, BluSaltPrinter printerModel) {
//        if (printer == null) {
            try {
//                printer = DeviceHelper.getPrinter();
                printActions(context, printerModel);
            } catch (Exception e) {
                Log.e("prepareForPrinters", e.getMessage());
            }
//        } else {
//            printActions(context, printerModel);
//        }
    }

    public static void printActions(Context context, TerminalResponse printerModel)
    {
        Log.d("printer","start");
        try {
            final Printer services = new Printer(context);
            services.printBitmap(generateTestBlusaltBitmap(context, printerModel), new IPrinter.IPinterListener() {
                @Override
                public void onSucc() {
                    Log.d("prrinter","suc");
                    Log.e("Check Printer Status", "Done Printing!!!");
//                    Toast.makeText(getApplicationContext(), "Done Printing!!!", Toast.LENGTH_SHORT);
                    //                    services.cutPaper();
                }

                @Override
                public void onError(int i) {
                    Log.d("printer",Integer.toString(i));
                    switch (i)
                    {
                        case 0:
                            Log.e("Check Printer Status", "Success Printing!!!");
//                    Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_SHORT);
                            break;
                        case 1:
                            Log.e("Check Printer Status", "Printer is busy");
//                    Toast.makeText(getApplicationContext(),"Printer is busy", Toast.LENGTH_LONG);
                            break;
                        case 2:
                            Log.e("Check Printer Status", "Out of paper");
//                    Toast.makeText(getApplicationContext(),"Out of paper", Toast.LENGTH_LONG);
                            break;
                        case 3:
                            Log.e("Check Printer Status", "The format of print data packet error");
//                    Toast.makeText(getApplicationContext(),"The format of print data packet error", Toast.LENGTH_LONG);
                            break;
                        case 4:
                            Log.e("Check Printer Status", "Printer malfunctions");
//                    Toast.makeText(getApplicationContext(),"Printer malfunctions", Toast.LENGTH_LONG);
                            break;
                        case 8:
                            Log.e("Check Printer Status", "Printer over heats");
//                    Toast.makeText(getApplicationContext(),"Printer over heats", Toast.LENGTH_LONG);
                            break;
                        case 9:
                            Log.e("Check Printer Status", "Printer voltage is too low");
//                    Toast.makeText(getApplicationContext(),"Printer voltage is too low", Toast.LENGTH_LONG);
                            break;
                        case -16:
                            Log.e("Check Printer Status", "Printing is unfinished");
//                    Toast.makeText(getApplicationContext(),"Printing is unfinished", Toast.LENGTH_LONG);
                            break;
                        case -6:
                            Log.e("Check Printer Status", "cut jam error(only support:E500,E800)");
//                    Toast.makeText(getApplicationContext(),"cut jam error(only support:E500,E800)", Toast.LENGTH_LONG);
                            break;
                        case -5:
                            Log.e("Check Printer Status", "cover open error(only support:E500,E800)");
//                    Toast.makeText(getApplicationContext(),"cover open error(only support:E500,E800)", Toast.LENGTH_LONG);
                            break;
                        case -4:
                            Log.e("Check Printer Status", "The printer has not installed font library");
//                    Toast.makeText(getApplicationContext(),"The printer has not installed font library", Toast.LENGTH_LONG);
                            break;
                        case -2:
                            Log.e("Check Printer Status", "Data package is too long");
//                    Toast.makeText(getApplicationContext(),"Data package is too long", Toast.LENGTH_LONG);
                            break;
                        default:
                            Log.e("Check Printer Status", "Unknown error code");
//                    Toast.makeText(getApplicationContext(),"Unknown error code", Toast.LENGTH_LONG);
                            break;
                    }
                }
            });
        } catch (Exception e) {
            Log.d("printer",e.getMessage());
            e.printStackTrace();
            Log.e("Check Printer Status", "Unknown error code");
//            Toast.makeText(getApplicationContext(),"Unknown error code", Toast.LENGTH_LONG);
        }
    }
    public static void printActions(Context context, BluSaltPrinter printerModel)
    {
        Log.d("printer","start");
        try {
            final Printer services = new Printer(context);
            services.printBitmap(generateTestBlusaltBitmap(context, printerModel), new IPrinter.IPinterListener() {
                @Override
                public void onSucc() {
                    Log.d("prrinter","suc");
                    Log.e("Check Printer Status", "Done Printing!!!");
//                    Toast.makeText(getApplicationContext(), "Done Printing!!!", Toast.LENGTH_SHORT);
                    //                    services.cutPaper();
                }

                @Override
                public void onError(int i) {
                    Log.d("printer",Integer.toString(i));
                    switch (i)
                    {
                case 0:
                    Log.e("Check Printer Status", "Success Printing!!!");
//                    Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_SHORT);
                    break;
                case 1:
                    Log.e("Check Printer Status", "Printer is busy");
//                    Toast.makeText(getApplicationContext(),"Printer is busy", Toast.LENGTH_LONG);
                    break;
                case 2:
                    Log.e("Check Printer Status", "Out of paper");
//                    Toast.makeText(getApplicationContext(),"Out of paper", Toast.LENGTH_LONG);
                    break;
                case 3:
                    Log.e("Check Printer Status", "The format of print data packet error");
//                    Toast.makeText(getApplicationContext(),"The format of print data packet error", Toast.LENGTH_LONG);
                    break;
                case 4:
                    Log.e("Check Printer Status", "Printer malfunctions");
//                    Toast.makeText(getApplicationContext(),"Printer malfunctions", Toast.LENGTH_LONG);
                    break;
                case 8:
                    Log.e("Check Printer Status", "Printer over heats");
//                    Toast.makeText(getApplicationContext(),"Printer over heats", Toast.LENGTH_LONG);
                    break;
                case 9:
                    Log.e("Check Printer Status", "Printer voltage is too low");
//                    Toast.makeText(getApplicationContext(),"Printer voltage is too low", Toast.LENGTH_LONG);
                    break;
                case -16:
                    Log.e("Check Printer Status", "Printing is unfinished");
//                    Toast.makeText(getApplicationContext(),"Printing is unfinished", Toast.LENGTH_LONG);
                    break;
                case -6:
                    Log.e("Check Printer Status", "cut jam error(only support:E500,E800)");
//                    Toast.makeText(getApplicationContext(),"cut jam error(only support:E500,E800)", Toast.LENGTH_LONG);
                    break;
                case -5:
                    Log.e("Check Printer Status", "cover open error(only support:E500,E800)");
//                    Toast.makeText(getApplicationContext(),"cover open error(only support:E500,E800)", Toast.LENGTH_LONG);
                    break;
                case -4:
                    Log.e("Check Printer Status", "The printer has not installed font library");
//                    Toast.makeText(getApplicationContext(),"The printer has not installed font library", Toast.LENGTH_LONG);
                    break;
                case -2:
                    Log.e("Check Printer Status", "Data package is too long");
//                    Toast.makeText(getApplicationContext(),"Data package is too long", Toast.LENGTH_LONG);
                    break;
                default:
                    Log.e("Check Printer Status", "Unknown error code");
//                    Toast.makeText(getApplicationContext(),"Unknown error code", Toast.LENGTH_LONG);
                    break;
                    }
                }
            });
        } catch (Exception e) {
            Log.d("printer",e.getMessage());
            e.printStackTrace();
            Log.e("Check Printer Status", "Unknown error code");
//            Toast.makeText(getApplicationContext(),"Unknown error code", Toast.LENGTH_LONG);
        }
    }

    private static Bitmap generateTestBlusaltBitmap(Context context, BluSaltPrinter printer) {
        //Print Logo
        CombBitmap combBitmap = new CombBitmap();
        Bitmap bitmap;
        bitmap = getImageFromAssetsFile(context, "sdk_custom_logo.png");
        combBitmap.addBitmap(bitmap);
        // printer.printerType = PrinterType.BankTransfer;
        //Titles
        combBitmap.addBitmap(GenerateBitmap.generateGap(70)); //
        if (printer.merchantDetails.name != null) {
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("MERCHANT NAME:", printer.merchantDetails.name, 21, true, false));
            combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
        }

        if (printer.merchantDetails.address != null) {
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("ADDRESS:", printer.merchantDetails.address, 21, true, false));
            //combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.merchantDetails.address, 23, GenerateBitmap.AlignEnum.LEFT, true, false));
            combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
        }

        if (printer.transactionSource != null) {
            String transactionStatus = Objects.equals(printer.transactionSource, "cash") ? "CASH" : Objects.equals(printer.transactionSource, "bank_transfer") ? "BANK TRANSFER" : "POS";
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("SOURCE:", transactionStatus, 21, true, false));
            combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
        }

        if (printer.transactionDate != null) {
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("DATE:", printer.transactionDate, 21, true, false));
            combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
        }
        if (printer.printerType == PrinterType.PosTransaction) {
            if (printer.posResponse.data.receiptInfo.unpredictableNumber != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.posResponse.data.receiptInfo.unpredictableNumber, printer.posResponse.data.receiptInfo.merchantTID, 21, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
            }
            if (printer.posResponse.data.receiptInfo.transactionDate != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.posResponse.data.receiptInfo.transactionDate, printer.posResponse.data.receiptInfo.transactionTime, 21, true, false));
            }
            combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("PURCHASE", 28, GenerateBitmap.AlignEnum.CENTER, true, false));
            combBitmap.addBitmap(GenerateBitmap.generateLine(1)); // print one line
            combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap

            String transactionStatus = printer.posResponse.status ? "APPROVED" : "DECLINED";
            try {//Content
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**" + transactionStatus + "**", 38, GenerateBitmap.AlignEnum.CENTER, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateLine(1)); // print one line
                combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
                if (!printer.isMerchantCopy) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**CUSTOMER COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
                } else {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**MERCHANT COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
                }
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("PAYMENT METHOD:", "POS", 23, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                if (printer.posResponse.data.cardScheme != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("SCHEME:", printer.posResponse.data.cardScheme.toUpperCase(), 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                if (printer.posResponse.data.receiptInfo.customerCardExpiry != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("PAN", printer.posResponse.data.receiptInfo.customerCardPan, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                if (printer.posResponse.data.receiptInfo.customerCardExpiry != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("EXPIRY:", printer.posResponse.data.receiptInfo.customerCardExpiry, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                if (printer.posResponse.data.receiptInfo.customerCardName != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("NAME", printer.posResponse.data.receiptInfo.customerCardName, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                if (printer.posResponse.data.receiptInfo.transactionSTAN != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("STAN:", printer.posResponse.data.receiptInfo.transactionSTAN, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                if (printer.posResponse.data.receiptInfo.transactionAccountType != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("ACCOUNT:", printer.posResponse.data.receiptInfo.transactionAccountType, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }

                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 23, GenerateBitmap.AlignEnum.CENTER, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                if (printer.posResponse.data.receiptInfo.transactionAmount != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("₦" + printer.posResponse.data.receiptInfo.transactionAmount + "00", 26, GenerateBitmap.AlignEnum.CENTER, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 23, GenerateBitmap.AlignEnum.CENTER, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                if (printer.posResponse.data.receiptInfo.rrn != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("RRN", printer.posResponse.data.receiptInfo.rrn, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                if (printer.posResponse.data.receiptInfo.transactionTVR != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("TVR", printer.posResponse.data.receiptInfo.transactionTVR, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                if (printer.posResponse.data.receiptInfo.reference != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Reference", printer.posResponse.data.receiptInfo.reference, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                if (printer.posResponse.data.message != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("REASON", printer.posResponse.data.message, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }

            } catch (Exception e) {
                Log.e("Printer", e.getMessage());
            }
        }
        try {
            if (printer.printerType == PrinterType.ResAccount) {
                combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("BANK TRANSFER", 24, GenerateBitmap.AlignEnum.CENTER, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateLine(1)); // print one line
                combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap


                try {
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("PAYMENT METHOD:", "BANK TRF.", 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    if (printer.resAccTransactionData.account_number != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Account Number", StringUtils.maskValue(printer.resAccTransactionData.source_account_number), 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                    if (printer.resAccTransactionData.source_account_bank_name != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Bank Name:", printer.resAccTransactionData.source_account_bank_name, 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                    if (printer.resAccTransactionData.source_account_name != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("NAME", printer.resAccTransactionData.source_account_name, 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 21, GenerateBitmap.AlignEnum.CENTER, true, false));

                    if (printer.resAccTransactionData.amount >= 0) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("₦" + printer.resAccTransactionData.amount, 26, GenerateBitmap.AlignEnum.CENTER, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 21, GenerateBitmap.AlignEnum.CENTER, true, false));

                    if (printer.resAccTransactionData.reference != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Reference", printer.resAccTransactionData.reference, 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }

                } catch (Exception e) {
                    Log.e("Printer", e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("Printer", e.getMessage());
        }

        try {
            if (printer.printerType == PrinterType.BankTransfer) {

//                if(printer.bankTransfer.metadata.merchant.name!= null){
//                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.bankTransfer.metadata.merchant.name, 23, GenerateBitmap.AlignEnum.LEFT, true, false));
//                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
//                }
//                if(printer.bankTransfer.metadata.merchant.email != null){
//                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.bankTransfer.metadata.merchant.name, 23, GenerateBitmap.AlignEnum.LEFT, true, false));
//                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
//
//                }
//                if(printer.bankTransfer.metadata.wallet_id != null){
//                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.bankTransfer.metadata.wallet_id, printer.bankTransfer.metadata.bank.currency, 23, true, false));
//                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
//
//                }
                combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("BANK TRANSFER", 24, GenerateBitmap.AlignEnum.CENTER, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateLine(1)); // print one line
                combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap

                String transactionStatus = printer.bankTransfer.status.equals("successful") ? "APPROVED" : "DECLINED";
                try {//Content
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**" + transactionStatus + "**", 38, GenerateBitmap.AlignEnum.CENTER, true, false));
                    if (!printer.isMerchantCopy) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**CUSTOMER COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
                    } else {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**MERCHANT COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
                    }
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("PAYMENT METHOD:", "BANK TRF.", 21, true, false));

                    if (printer.bankTransfer.metadata.source_account.account_name != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Name:", printer.bankTransfer.metadata.source_account.account_name.toUpperCase(), 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                    if (printer.bankTransfer.metadata.source_account.account_number != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Account Number:", StringUtils.maskValue(printer.bankTransfer.metadata.source_account.account_number), 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                    if (printer.bankTransfer.metadata.source_account.bank_name != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Bank Name:", printer.bankTransfer.metadata.source_account.bank_name, 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 21, GenerateBitmap.AlignEnum.CENTER, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap


                    if (printer.bankTransfer.amount >= 0) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("₦" + printer.bankTransfer.amount, 26, GenerateBitmap.AlignEnum.CENTER, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 21, GenerateBitmap.AlignEnum.CENTER, true, false));
                    if (printer.bankTransfer.reference != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Reference", printer.bankTransfer.reference, 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                    }

                } catch (Exception e) {
                    Log.e("Printer", e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("Printer", e.getMessage());
        }
        if (printer.printerType == PrinterType.TransDetail) {
            if (printer.transDetail.getReference() != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("REF:", printer.transDetail.getReference(), 21, true, false));
            }
            if (printer.transDetail.getSource() != null) {
                if (Objects.requireNonNull(printer.transDetail.getSource()).equals("cash")) {
                    if (printer.transDetail.getCustomerName() != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("CUSTOMER NAME", printer.transDetail.getCustomerName(), 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                    if (printer.transDetail.getCustomerEmail() != null) {
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Email", printer.transDetail.getCustomerEmail(), 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                    }
                } else {
                    if (Objects.requireNonNull(printer.transDetail.getSource()).equals("bank_transfer")) {
                        if (Objects.requireNonNull(printer.transDetail.getMetadata()).source_account.account_name != null) {
                            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Account Name:", Objects.requireNonNull(printer.transDetail.getMetadata()).source_account.account_name.toUpperCase(), 21, true, false));
                            combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                        }
                        if (Objects.requireNonNull(printer.transDetail.getMetadata()).source_account.account_number != null) {
                            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Account Number", StringUtils.maskValue(Objects.requireNonNull(printer.transDetail.getMetadata()).source_account.account_number), 21, true, false));
                            combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                        }
                        if (Objects.requireNonNull(printer.transDetail.getMetadata()).source_account.bank_name != null) {
                            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Bank Name:", Objects.requireNonNull(printer.transDetail.getMetadata()).source_account.bank_name, 21, true, false));
                            combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                        }
                    } else {
                        if (Objects.requireNonNull(printer.transDetail.getMetadata()).agentRef != null) {
                            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Agent Ref:", printer.transDetail.getMetadata().agentRef, 21, true, false));
                            combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                        }
                    }
                    if (printer.transDetail.getStatus() != null) {
                        String transPayment = Objects.equals(printer.transDetail.getStatus(), "failed") ? "FAILED" : "SUCCESSFUL";
                        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("STATUS", transPayment, 21, true, false));
                        combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                    }
                }
            }
            if (printer.transDetail.getCurrency() != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("CURRENCY", printer.transDetail.getCurrency(), 21, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

            }
            combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Transaction Detail", 26, GenerateBitmap.AlignEnum.CENTER, true, false));
            combBitmap.addBitmap(GenerateBitmap.generateLine(1)); // print one line
            combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
            String transactionStatus = Objects.equals(printer.transDetail.getStatus(), "failed") ? "DECLINED" : "APPROVED";

            try {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**" + transactionStatus + "**", 38, GenerateBitmap.AlignEnum.CENTER, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                if (!printer.isMerchantCopy) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**CUSTOMER COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
                } else {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**MERCHANT COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
                }
                if (printer.transDetail.getType() != null) {
                    String transPayment = Objects.equals(printer.transDetail.getType(), "payment_collection") ? "PAYMENT COLLECTION" : printer.transDetail.getType();
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("TYPE", transPayment, 23, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

                }
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 23, GenerateBitmap.AlignEnum.CENTER, true, false));
                if (printer.transDetail.getAmount() != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("₦" + printer.transDetail.getAmount(), 26, GenerateBitmap.AlignEnum.CENTER, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 23, GenerateBitmap.AlignEnum.CENTER, true, false));
                if (printer.transDetail.getDescription() != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("DESC.", printer.transDetail.getDescription(), 23, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
            } catch (Exception e) {
                Log.e("Printer", e.getMessage());
            }
        }

        if (printer.printerType == PrinterType.CashRecord) {
            if (printer.cashRecord.reference != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("REF:", printer.cashRecord.reference, 21, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap

            }
            if (printer.cashRecord.customerName != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("CUSTOMER NAME", printer.cashRecord.customerName, 21, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
            }
            if (printer.cashRecord.customerEmail != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("EMAIL", printer.cashRecord.customerEmail, 21, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
            }
            if (printer.cashRecord.currency != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("CURRENCY", printer.cashRecord.currency, 21, true, false));
                combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
            }
            combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Cash Record", 26, GenerateBitmap.AlignEnum.CENTER, true, false));
            combBitmap.addBitmap(GenerateBitmap.generateLine(1)); // print one line
            combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap

            try {
                if (!printer.isMerchantCopy) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**CUSTOMER COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
                } else {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**MERCHANT COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
                }
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 21, GenerateBitmap.AlignEnum.CENTER, true, false));

                if (printer.cashRecord.amount > 0) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("₦" + printer.cashRecord.amount, 26, GenerateBitmap.AlignEnum.CENTER, true, false));
                }
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 21, GenerateBitmap.AlignEnum.CENTER, true, false));
                if (printer.cashRecord.description != null) {
                    combBitmap.addBitmap(GenerateBitmap.str2Bitmap("DESC.", printer.cashRecord.description, 21, true, false));
                    combBitmap.addBitmap(GenerateBitmap.generateGap(5)); // print row gap
                }
            } catch (Exception e) {
                Log.e("Printer", e.getMessage());
            }
        }

        combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("--------------------------------------", 18, GenerateBitmap.AlignEnum.CENTER, true, false)); // 打印一行直线
        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("POWERED BY BLUSALT".toUpperCase(), 21, GenerateBitmap.AlignEnum.CENTER, true, false));
        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("https://blusalt.net", 21, GenerateBitmap.AlignEnum.CENTER, true, false));
        if (printer.supportPhoneNumber != null) {
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.supportPhoneNumber + ", " + "+234 913 586 4288", 21, GenerateBitmap.AlignEnum.CENTER, true, false));
        } else {
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("+234 913 586 4288", 21, GenerateBitmap.AlignEnum.CENTER, true, false));
        }
        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("clientservices@blusalt.net", 21, GenerateBitmap.AlignEnum.CENTER, true, false));
        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("", "", 38, true, false)); //Add Empty Space, since generateGap() oes not seem to work
        combBitmap.addBitmap(GenerateBitmap.generateGap(60)); //

        Bitmap bp = combBitmap.getCombBitmap();
        return bp;
    }

    private static Bitmap generateTestBlusaltBitmap(Context context, TerminalResponse printer) {
        //Print Logo
        CombBitmap combBitmap = new CombBitmap();
        Bitmap bitmap;
        bitmap = getImageFromAssetsFile(context, "sdk_custom_logo.png");
        combBitmap.addBitmap(bitmap);
        //Title
        combBitmap.addBitmap(GenerateBitmap.generateGap(70)); //
        if (printer.data.receiptInfo.merchantName != null) {
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.data.receiptInfo.merchantName, 23, GenerateBitmap.AlignEnum.LEFT, true, false));
        }
        if (printer.data.receiptInfo.merchantAddress != null) {
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.data.receiptInfo.merchantAddress, 23, GenerateBitmap.AlignEnum.LEFT, true, false));
        }
        if (printer.data.receiptInfo.unpredictableNumber != null) {
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.data.receiptInfo.unpredictableNumber, printer.data.receiptInfo.merchantTID, 23, true, false));
        }
        if (printer.data.receiptInfo.transactionDate != null) {
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.data.receiptInfo.transactionDate, printer.data.receiptInfo.transactionTime, 23, true, false));
        }
        combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
        combBitmap.addBitmap(GenerateBitmap.str2Bitmap("PURCHASE", 28, GenerateBitmap.AlignEnum.CENTER, true, false));
        combBitmap.addBitmap(GenerateBitmap.generateLine(1)); // print one line
        combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap

        String transactionStatus = printer.status ? "APPROVED" : "DECLINED";
        try {//Content
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**" + transactionStatus + "**", 40, GenerateBitmap.AlignEnum.CENTER, true, false));
            if (!printer.isMerchantCopy) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**CUSTOMER COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
            } else {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("**MERCHANT COPY**", 22, GenerateBitmap.AlignEnum.CENTER, true, false));
            }
            if (printer.data.cardScheme != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("SCHEME:", printer.data.cardScheme.toUpperCase(), 23, true, false));
            }
            if (printer.data.receiptInfo.customerCardExpiry != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("PAN", printer.data.receiptInfo.customerCardPan, 23, true, false));
            }
            if (printer.data.receiptInfo.customerCardExpiry != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("EXPIRY:", printer.data.receiptInfo.customerCardExpiry, 23, true, false));
            }
            if (printer.data.receiptInfo.customerCardName != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("NAME", printer.data.receiptInfo.customerCardName, 23, true, false));
            }
            if (printer.data.receiptInfo.transactionSTAN != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("STAN:", printer.data.receiptInfo.transactionSTAN, 23, true, false));
            }
            if (printer.data.receiptInfo.transactionAccountType != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("ACCOUNT:", printer.data.receiptInfo.transactionAccountType, 23, true, false));
            }

            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 23, GenerateBitmap.AlignEnum.CENTER, true, false));

            if (printer.data.receiptInfo.transactionAmount != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("₦" + printer.data.receiptInfo.transactionAmount + "00", 28, GenerateBitmap.AlignEnum.CENTER, true, false));
            }
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("***************", 23, GenerateBitmap.AlignEnum.CENTER, true, false));
            if (printer.data.receiptInfo.rrn != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("RRN", printer.data.receiptInfo.rrn, 23, true, false));
            }
            if (printer.data.receiptInfo.transactionTVR != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("TVR", printer.data.receiptInfo.transactionTVR, 23, true, false));
            }
            if (printer.data.receiptInfo.reference != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("Reference", printer.data.receiptInfo.reference, 23, true, false));
            }
            if (printer.data.message != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("REASON", printer.data.message, 23, true, false));
            }
            combBitmap.addBitmap(GenerateBitmap.generateGap(15)); // print row gap
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("--------------------------------------", 20, GenerateBitmap.AlignEnum.CENTER, true, false)); // 打印一行直线
            if (printer.data.receiptInfo.appPoweredBy != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("POWERED BY BLUSALT".toUpperCase(), 23, GenerateBitmap.AlignEnum.CENTER, true, false));
            }
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("https://blusalt.net", 23, GenerateBitmap.AlignEnum.CENTER, true, false));
            if (printer.supportPhoneNumber != null) {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap(printer.supportPhoneNumber + ", " + "+234 913 586 4288", 21, GenerateBitmap.AlignEnum.CENTER, true, false));
            } else {
                combBitmap.addBitmap(GenerateBitmap.str2Bitmap("+234 913 586 4288", 21, GenerateBitmap.AlignEnum.CENTER, true, false));
            }
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("clientservices@blusalt.net", 23, GenerateBitmap.AlignEnum.CENTER, true, false));
            combBitmap.addBitmap(GenerateBitmap.str2Bitmap("", "", 40, true, false)); //Add Empty Space, since generateGap() oes not seem to work

            combBitmap.addBitmap(GenerateBitmap.generateGap(60)); //
        } catch (Exception e) {
            Log.e("Printer", e.getMessage());
        }
        Bitmap bp = combBitmap.getCombBitmap();
        return bp;
    }


}
