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
 *  2020/05/19 	         Qinny Zhou           	Create/Add/Modify/Delete
 *  ===========================================================================================
 */

package com.blusalt.blusaltpaxsdk.utils;

import android.util.Log;

import com.blusalt.blusaltpaxsdk.MyApplication;
import com.blusalt.blusaltpaxsdk.pax.manager.AppDataManager;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.commonlib.utils.convert.IConvert;
import com.pax.dal.IPed;
import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.EPinBlockMode;
import com.pax.dal.exceptions.PedDevException;

public class PedApiUtils {
    private static final byte[] TEST_TPK = new byte[]{0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33};

    /**
     * calculate PIN block
     *
     * @param panBlock shifted pan block
     * @return PIN block
     * @throws PedDevException exception
     */
    public static byte[] getPinBlock(String panBlock, int timeout) throws PedDevException {
        boolean supportBypass = true;
        IPed ped = MyApplication.getINSTANCE().getDal().getPed(EPedType.INTERNAL);
        String pinLen = "4,5,6,7,8,9,10,11,12";
        if (supportBypass) {
            pinLen = "0," + pinLen;
        }
        ped.setKeyboardLayoutLandscape(false);
        byte tpkIndex = (byte) AppDataManager.getInstance().getInt(AppDataManager.TPK_INDEX, 2);
        return ped.getPinBlock(tpkIndex, pinLen, panBlock.getBytes(), EPinBlockMode.ISO9564_0, timeout);
    }

    /**
     * write TPK
     *
     * @throws PedDevException exception
     */
    public static void writeTPK(byte tpkIndex) throws PedDevException {
        ECheckMode checkMode = ECheckMode.KCV_NONE;
        MyApplication.getINSTANCE().getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TMK, (byte) 0x00,
                EPedKeyType.TPK, tpkIndex, TEST_TPK, checkMode, null);

    }


    //---------------Our Implementation for writing clearMasterkey  key and EncryptedPinKey-----------------


    /**
     * write TPK
     *
     * @throws PedDevException exception
     */
    public static void writeTPKOURS(String encryptedPinKey) throws PedDevException {
        Log.e("original encryptedPinKey", encryptedPinKey);

        byte[] byte_TPK = ConvertHelper.getConvert().strToBcd(encryptedPinKey, IConvert.EPaddingPosition.PADDING_LEFT);

//        Log.e("byte Value encryptedPinKey ", "" +byte_TPK );
//        Log.e("byte back to string encryptedPinKey ", convert.bcdToStr(byte_TPK) );

        byte srcKeyIndex = 0x01;
        int indexValue = 3;
        byte INDEX_TPK = (byte) indexValue;

        MyApplication.getINSTANCE().getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TMK, srcKeyIndex, EPedKeyType.TPK, INDEX_TPK,
                byte_TPK, ECheckMode.KCV_NONE, null);

        AppDataManager.getInstance().set(AppDataManager.TPK_INDEX, indexValue);


    }


    /**
     * write TMK
     *
     * @throws PedDevException exception
     */
    public static void writeTMKOURS(String clearmasterKey) throws PedDevException {
        Log.e("original ClearMasterKey", clearmasterKey);
        byte[] byte_TMK = ConvertHelper.getConvert().strToBcd(clearmasterKey, IConvert.EPaddingPosition.PADDING_LEFT);
//        Log.e("byte Value ClearMasterKey ", "" +byte_TMK );
//        Log.e("byte back to string ClearMasterKey ", convert.bcdToStr(byte_TMK) );

        byte INDEX_TMK = 0x01;


        MyApplication.getINSTANCE().getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TLK, (byte) 0, EPedKeyType.TMK,
                INDEX_TMK, byte_TMK, ECheckMode.KCV_NONE, null);

    }

    //---------------End Our Implementation for writing clearMasterkey  key and EncryptedPinKey-----------------


    public static void eraseKey() throws PedDevException {
        MyApplication.getINSTANCE().getDal().getPed(EPedType.INTERNAL).erase();
    }
}
