package com.blusalt.blusaltpaxsdk.pax.util;

import static com.blusalt.blusaltpaxsdk.utils.Constants.TXN_TYPE_ICC;
import static com.blusalt.blusaltpaxsdk.utils.Constants.TXN_TYPE_PICC;

import android.util.Log;

import com.blusalt.blusaltpaxsdk.MyApplication;
import com.blusalt.blusaltpaxsdk.pax.manager.AppDataManager;
import com.blusalt.blusaltpaxsdk.utils.PedApiUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.commonlib.utils.convert.IConvert;
import com.pax.dal.exceptions.PedDevException;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.RetCode;
import com.paxsz.module.emv.process.contact.EmvProcess;
import com.paxsz.module.emv.process.contactless.ClssProcess;

import java.util.ArrayList;

/**
 * Created by Olije Favour on 8/11/2020.
 */
public class Util {
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        if (len >= 2) {
            len /= 2;
        }
        byte[] bbt = new byte[len];
        byte[] abt = asc.getBytes();

        for (int p = 0; p < asc.length() / 2; p++) {
            int j;
            if ((abt[(2 * p)] >= 97) && (abt[(2 * p)] <= 122)) {
                j = abt[(2 * p)] - 97 + 10;
            } else {
                if ((abt[(2 * p)] >= 65) && (abt[(2 * p)] <= 90))
                    j = abt[(2 * p)] - 65 + 10;
                else
                    j = abt[(2 * p)] - 48;
            }
            int k;
            if ((abt[(2 * p + 1)] >= 97) && (abt[(2 * p + 1)] <= 122)) {
                k = abt[(2 * p + 1)] - 97 + 10;
            } else {
                if ((abt[(2 * p + 1)] >= 65) && (abt[(2 * p + 1)] <= 90))
                    k = abt[(2 * p + 1)] - 65 + 10;
                else {
                    k = abt[(2 * p + 1)] - 48;
                }
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }


    private static final int[] EMV_TAG = {0x9F26, 0x9F27, 0x9F10,0x9F37,0x9F36, 0x95, 0x9A,0x9C,0x9F02,0x5F2A,0x82,0x9F1A,0x9F34,0x9F35, 0x9F33
            ,0x9F6E,0x5F34,0x9F1E};  //TODO ADD THE FIELDS AND IN THE RIGHT MANNER

    private static final int[] EMV_TAG_CONTACTLESS= {0x9F26, 0x9F27, 0x9F10,0x9F37,0x9F36, 0x95, 0x9A,0x9C,0x9F02,0x5F2A,0x82,0x9F1A,0x9F34,0x9F35, 0x9F33
            ,0x5F34};

//                                          {0x9F26, 0x9F27, 0x9F10, 0x9F36};  //TODO ADD THE FIELDS AND IN THE RIGHT MANNER


    public static String getTagVal(int tag, int currentTxnType,byte transactionType) {
        ByteArray byteArray = new ByteArray();
        byte[] tempArr;
        int ret = 0;
        if(currentTxnType == TXN_TYPE_ICC) {
            Log.e("is TXN_TYPE_ICC ","Yes ");
            ret = EmvProcess.getInstance().getTlv(tag, byteArray);
        }else if(currentTxnType == TXN_TYPE_PICC){
            Log.e("is TXN_TYPE_PICC ","Yes ");

            ret = ClssProcess.getInstance().getTlv(tag, byteArray);
        }
//        Log.d(TAG, "getTagVal, ret:" + ret + ", T:" + decimalToHex(tag) + ",L:" + byteArray.length + ", V:" + ConvertHelper.getConvert().bcdToStr(byteArray.data));
        if (tag == 0x9F03) {
            byteArray.length = 6;
        } else if (tag == 0x9C) {
            return getTransTypeByCode(transactionType); //Transaction type
        }
//        else if (tag == 0x9F1A){
//                    return "0566";
//        }else if (tag == 0x9F33){
//                    return "E0F0C8";
//        }else if (tag == 0x9F35){
//                    return "22";
//        }else if (tag == 0x82){
//                    return "2000";
//        }else if (tag == 0x9F36){
//                    return "0001";
//        }
        else {
            if (ret != RetCode.EMV_OK) {
                return "";
            }
        }
        tempArr = new byte[byteArray.length];
        System.arraycopy(byteArray.data, 0, tempArr, 0, byteArray.length);

        IConvert convert = ConvertHelper.getConvert();


        String taaag = convert.bcdToStr(convert.intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN));


        Log.e("Util   getTagVal",  " currentTxnType "  + currentTxnType +" tag " + taaag + "  val:" + ConvertHelper.getConvert().bcdToStr(tempArr));

//        return (tag == 0x9F1A) ?"0566" :ConvertHelper.getConvert().bcdToStr(tempArr); //Hack
        return ConvertHelper.getConvert().bcdToStr(tempArr);
    }


    public static String convertHexToString(String hex){

        String ascii="";
        String str;

        // Convert hex string to "even" length
        int rmd,length;
        length=hex.length();
        rmd =length % 2;
        if(rmd==1)
            hex = "0"+hex;

        // split into two characters
        for( int i=0; i<hex.length()-1; i+=2 ){

            //split the hex into pairs
            String pair = hex.substring(i, (i + 2));
            //convert hex to decimal
            int dec = Integer.parseInt(pair, 16);
            str=CheckCode(dec);
            ascii=ascii+" "+str;
        }
        return ascii;
    }

    public static String CheckCode(int dec){
        String str;

        //convert the decimal to character
        str = Character.toString((char) dec);

        if(dec<32 || dec>126 && dec<161)
            str="n/a";
        return removeSpace(str);
    }


    private static String removeSpace(String input)
    {
        input.replace(" ","");
        return input;
    }


    private static String   getTransTypeByCode( byte transactionType) {
        String value ;
        switch (transactionType){

            case 0x60:value = "60";
            case 0x61:value ="61";
            case 0x20:value ="20";
            case 0x09:value ="09";

            default: value ="00";

        }

        return value;
    }


    //Get gets the icc data
    public static String getTLVData(int currentTxnType,byte transactionType) {
        int tag;
        String iccDataConcatenated = "";
        IConvert convert = ConvertHelper.getConvert();
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();

        Log.e("Util  Where ",  " TXN_TYPE_ICC "  + TXN_TYPE_ICC +" TXN_TYPE_PICC " + TXN_TYPE_PICC );


        for (int i = 0; i < EMV_TAG.length; i++) {
            tag = EMV_TAG[i];
            String val = getTagVal(tag,currentTxnType,transactionType);
//            String key = TAG_TITLE[i] + "(" + Integer.toHexString(tag).toUpperCase() + ")";
//            Log.w(TAG, "key:" + key + ", val:" + val);
            String taaag = convert.bcdToStr(convert.intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN));
            Log.e("Util",  " currentTxnType "  + currentTxnType +" tag " + taaag + "  val:" + val);
            iccDataConcatenated += ConcatLenght2Str(taaag, val);

        }

        return  iccDataConcatenated;
    }


    public static String getTLVData_contactless(int currentTxnType,byte transactionType) {
        int tag;
        String iccDataConcatenated = "";
        IConvert convert = ConvertHelper.getConvert();
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();

        Log.e("Util  Where ",  " TXN_TYPE_ICC "  + TXN_TYPE_ICC +" TXN_TYPE_PICC " + TXN_TYPE_PICC );


        for (int emvTagContactless : EMV_TAG_CONTACTLESS) {
            tag = emvTagContactless;
            String val = getTagVal(tag, currentTxnType, transactionType);
//            String key = TAG_TITLE[i] + "(" + Integer.toHexString(tag).toUpperCase() + ")";
//            Log.w(TAG, "key:" + key + ", val:" + val);
            String taaag = convert.bcdToStr(convert.intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN));
            Log.e("Util",  " currentTxnType Contactless  "  + currentTxnType +" tag " + taaag + "  val:" + val);
            iccDataConcatenated += ConcatLenght2Str(taaag, val);
        }

        return  iccDataConcatenated;
    }



    public static String ConcatLenght2Str(String tag, String value)
    {

        int len = value.length()/2;
        if (len<=9)
        {
            return tag+"0"+Integer.toHexString(len)+value;
        }
        String slen = padLeft(Integer.toHexString(len),"0",2);
        return tag+ slen +value;
    }

    public static String padLeft(String data, String padChar, Integer len){
        while (data.length() < len){
            data = padChar+data;
        }
        return data;
    }


    public  static  void injectTPK() {
        int tpkValue = AppDataManager.getInstance().getInt(AppDataManager.TPK_INDEX, -1);
        Log.e("TAG", "PaxDevice: TPK VALUE " + tpkValue );

        if(tpkValue == -1) {
            int index =1;


            MyApplication.getINSTANCE().runInBackground(new Runnable() {
                @Override
                public void run() {
                    try {
                        PedApiUtils.writeTPK((byte) index);
                        AppDataManager.getInstance().set(AppDataManager.TPK_INDEX, index);
                    } catch (PedDevException e) {
                    }
                }
            });
        }
    }

    public static String bcd2str(byte[] bcds) {
        if (bcds == null)
            return "";
        char[] ascii = "0123456789abcdef".toCharArray();
        byte[] temp = new byte[bcds.length * 2];
        for (int i = 0; i < bcds.length; i++) {
            temp[i * 2] = (byte) ((bcds[i] >> 4) & 0x0f);
            temp[i * 2 + 1] = (byte) (bcds[i] & 0x0f);
        }
        StringBuffer res = new StringBuffer();

        for (int i = 0; i < temp.length; i++) {
            res.append(ascii[temp[i]]);
        }
        return res.toString().toUpperCase();
    }


    public static final String byteToHexString(byte[] bArray) {
        if(bArray == null || bArray.length == 0){
            return null;
        }
        StringBuffer sb = new StringBuffer(bArray.length);

        String sTemp;
        int j = 0;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);

            sb.append(sTemp.toUpperCase());
            j++;

        }
        return sb.toString();
    }

}
