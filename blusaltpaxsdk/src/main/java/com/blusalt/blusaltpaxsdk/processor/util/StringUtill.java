package com.blusalt.blusaltpaxsdk.processor.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StringUtill {


    /**
     * String 非空判断
     * @param msg
     * @return
     */
    public static boolean isEmpty(String msg){
        boolean is;
        if (msg != null&&!"".equals(msg)) {
            is = false;
        }else {
            is = true;
        }
        return is;
    }

    public static boolean isNullOrEmpty(String value) {
        if (value == null) return true;

        return value.length() == 0;
    }

    public static boolean isNull(CharSequence str) {
        return str == null || str.length() == 0;
    }


    //为 EditText 获取相应的 selection index.即设置光标位置为最右方
    public static int getSelectionIndex(CharSequence str) {
        return isNull(str) ? 0 : str.length();
    }


    /**
     * 在str左边填充fill内容，填充后的总长度为totalLength。
     *
     * @param fill
     * @param totalLength
     * @param str
     * @return
     */
    public static String leftPadding(char fill, int totalLength, String str) {
        StringBuffer buffer = new StringBuffer();
        for (int i = str.length(); i < totalLength; i++) {
            buffer.append(fill);
        }
        buffer.append(str);
        return buffer.toString();
    }

    public static String leftPadding(String fill, int totalLength, String str) {
        StringBuffer buffer = new StringBuffer();
        for (int i = str.length(); i < totalLength; i++) {
            buffer.append(fill);
        }
        buffer.append(str);
        return buffer.toString();
    }

    //左边增加一定长度的字符串
    public static String leftAppend(String fill, int appendLength, String str) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < appendLength; i++) {
            buffer.append(fill);
        }
        buffer.append(str);
        return buffer.toString();
    }

    //右边增加一定长度的字符串
    public static String rightAppend(String fill, int appendLength, String str) {
        StringBuilder buffer = new StringBuilder(str);
        for (int i = 0; i < appendLength; i++) {
            buffer.append(fill);
        }
        return buffer.toString();
    }

    public static String rightPadding(String fill, int totalLength, String str) {
        StringBuilder buffer = new StringBuilder(str);
        while (str.length() < totalLength) {
            buffer.append(fill);
        }
        return buffer.toString();
    }


    //得到字符串的字节长度
    public static int getContentByteLength(String content) {
        if (content == null || content.length() == 0)
            return 0;
        int length = 0;
        for (int i = 0; i < content.length(); i++) {
            length += getByteLength(content.charAt(i));
        }
        return length;
    }

    //得到几位字节长度
    private static int getByteLength(char a) {
        String tmp = Integer.toHexString(a);
        return tmp.length() >> 1;
    }

    //文本右边补空格
    public static String fillRightSpacePrintData(String context, int fillDataLength){

        if(context != null){
            int printDataLength = fillDataLength - context.length();
            if(printDataLength>0){
                for (int i=0;i<printDataLength;i++){
                    context+=" ";
                }
            }

        }else {
            context = "";
            for (int i=0;i<fillDataLength;i++){
                context+=" ";
            }
        }

        return context;
    }

    //文本左边补空格
    public static String fillLeftSpacePrintData(String context, int fillDataLength){

        if(context != null){
            int printDataLength = fillDataLength - context.length();
            if(printDataLength>0){
                String tempSpace = "";
                for (int i=0;i<printDataLength;i++){
                    tempSpace+=" ";
                }

                context = tempSpace + context;
            }

        }else {
            context = "";
            for (int i=0;i<fillDataLength;i++){
                context+=" ";
            }
        }

        return context;
    }



    public static String leftPad(String str, int len, char pad) {
        if(str == null)
            return null;
        StringBuilder sb = new StringBuilder();
        while (sb.length() + str.length() < len) {
            sb.append(pad);
        }
        sb.append(str);
        String paddedString = sb.toString();
        return paddedString;
    }

    public static String rightPad(String str, int len, char pad) {

        if(str == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        while (sb.length() < len) {
            sb.append(pad);
        }
        String paddedString = sb.toString();
        return paddedString;
    }

    public static String maskString(String string){
        try {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < string.length(); i++) {
                sb.append("*");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "********";
        }
    }

    public static String maskPan(String pan){
        try {
            String firstPart = pan.substring(0, 6);
            int len = pan.length();
            String lastPart = pan.substring(len - 4, len);
            int middlePartLength = len - 6;
            String middleLastPart = leftPad("", middlePartLength, '*');
            return firstPart + middleLastPart.substring(0, middleLastPart.length() - 4) + lastPart;
        } catch (Exception e) {
            e.printStackTrace();
            return "0000********0000";
        }
    }

    public static void main(String[] args) {
        String a = "阿斯顿法";
        a = leftAppend("-", 10, a);
        Log.d("TAG", a);

        a = rightAppend("*", 6, a);
        Log.d("TAG", a);

        Log.d("TAG", "51: " + convertStringToHex("51"));
    }


    /**
     * 例子：“3132ABCD”》{0x31,0x32,0xab,0xcd}
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        if(hex == null || hex.length()==0){
            return null;
        }
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toUpperCase().toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }


    //字符序列转换为16进制字符串
    /**
     * 例子 {0x31,0x32,0xab,0xcd}  >  “3132ABCD”
     * @param
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return "";
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString().toUpperCase();
    }



    public static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }


    //字符序列转换为16进制字符串,大写
    /**
     * 例子 {0x31,0x32,0xab,0xcd}  >  “31 32 AB CD”
     * @param
     * @return
     */
    public static String bytesToHexString_upcase(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");

        if (src == null || src.length <= 0) {
            return "";
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString().toUpperCase();
    }

    public static String Byte2HexString_upcase(int a){

        String result = String.format("%02X",a);
        if(result.length() == 8){
            result = result.substring(6,8);
        }
        return result;
    }


    public static String Int2HexString_upcase(int a){
        StringBuffer result = new StringBuffer();
        result.append(Integer.toHexString(a));
        if (result.length()%2 == 1){
            result.insert(0,'0');
        }
        return result.toString().toUpperCase();
    }



    public static String convertHexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }
        Log.d("TAG", "Decimal : " + temp.toString());

        return sb.toString();
    }

    public static String convertStringToHex(String str){

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }

        return hex.toString();
    }

    public static String getAssetsJson(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
