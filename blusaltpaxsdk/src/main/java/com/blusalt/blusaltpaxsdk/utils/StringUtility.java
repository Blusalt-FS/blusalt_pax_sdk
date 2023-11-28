package com.blusalt.blusaltpaxsdk.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtility {
    /**
     * @param strInput
     *            浼犲叆String
     * @return boolean 浼犲叆鐨凷tring鏄惁涓虹┖
     * */
    static public boolean isEmpty(String strInput) {
        /*
         * if(strInput == null) return true; return strInput.length() == 0 ?
         * true : false;
         */
        return TextUtils.isEmpty(strInput);

    }

    public static String getStringFormat(byte[] bytes) {
        String str = "";
        for (byte b : bytes) {
            str += String.format("%02X ", b);
        }
        return str;
    }
    public static String getStringFormat(byte[] bytes, int length) {
        String str = "";
        for (int i = 0; i < length; i++) {
            str += String.format("%02X ", bytes[i]);
        }
        return str;
    }
    static protected boolean CheckByte(byte byteIn) {
        // '0' - '9'
        if (byteIn <= 0x39 && byteIn >= 0x30)
            return true;
        // 'A' - 'F'
        if (byteIn <= 0x46 && byteIn >= 0x41)
            return true;
        // 'a' - 'f'
        if (byteIn <= 0x66 && byteIn >= 0x61)
            return true;
        return false;
    }

    static protected boolean CheckString(String strInput) {
        strInput = strInput.trim();
        if (strInput.length() != 2)
            return false;
        byte[] byteArry = strInput.getBytes();
        for (int i = 0; i < 2; i++) {
            if (!CheckByte(byteArry[i]))
                return false;
        }
        return true;
    }

    static protected byte StringToByte(String strInput) {
        byte[] byteArry = strInput.getBytes();
        for (int i = 0; i < 2; i++) {

            if (byteArry[i] <= 0x39 && byteArry[i] >= 0x30) {
                byteArry[i] -= 0x30;
            } else if (byteArry[i] <= 0x46 && byteArry[i] >= 0x41) {
                byteArry[i] -= 0x37;
            } else if (byteArry[i] <= 0x66 && byteArry[i] >= 0x61) {
                byteArry[i] -= 0x57;
            }
        }
        // Log.i("APP", String.format("byteArry[0] = 0x%X\n", byteArry[0]));
        // Log.i("APP", String.format("byteArry[1] = 0x%X\n", byteArry[1]));
        return (byte) ((byteArry[0] << 4) | (byteArry[1] & 0x0F));
    }

    /**
     * @param String
     *            strInput
     * @param byte[] arryByte
     * @return int
     * */
    static public int StringToByteArray(String strInput, byte[] arryByte) {
        strInput = strInput.trim();
        String[] arryString = strInput.split(" ");
        if (arryByte.length < arryString.length)
            return -1;
        for (int i = 0; i < arryString.length; i++) {
            if (!CheckString(arryString[i]))
                return -1;
            arryByte[i] = StringToByte(arryString[i]);
            Log.i("APP", String.format("%02X", arryByte[i]));
        }

        return arryString.length;
    }
    /**
     * @param String
     *            strInput
     * @param byte[] arryByte
     * @return int
     * */
    static public byte[] StringToByteArray(String strInput) {
    	if(strInput==null)return null;
        strInput = strInput.trim().replace(" ", "");
        int len=strInput.length()/2; 
        byte[] arryByte=new byte[len];
        for(int i=0;i<len;i++){
        	arryByte[i]=StringToByte(strInput.substring(2*i,2*(i+1)));
        }
        return arryByte;
    }

    static public String ByteArrayToString(byte[] arryByte, int nDataLength) {
        String strOut = new String();
        for (int i = 0; i < nDataLength; i++)
            strOut += String.format("%02X ", arryByte[i]);
        return strOut;
    }

    /**
     * @param String
     *            str 浼犲叆瀛楃锟�
     * @param String
     *            reg 鎸夌収鍝鏂瑰紡鎴栧摢涓瓧娈垫媶锟�
     * @return Stringp[] 杩斿洖鎷嗗垎鍚庣殑鏁扮粍锟�
     * */
    static public String[] spiltStrings(String str, String reg) {
        String[] arrayStr = str.split(reg);
        return arrayStr;
    }
    
    /*获取系统时间 格式为："yyyy-MM-dd HH:mm:ss"*/
    public static String getCurrentDateF() {
        Date d = new Date();
        SimpleDateFormat sf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }
}
