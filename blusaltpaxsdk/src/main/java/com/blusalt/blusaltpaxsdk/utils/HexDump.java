package com.blusalt.blusaltpaxsdk.utils;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class HexDump {
    private final static char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static String getHexString(byte[] bytes) {
        String str = "";
        for (byte b : bytes) {
            str += String.format("%02X ", b);
        }
        return str;
    }

    public static String dumpHexString(byte[] array)
    {
        return dumpHexString(array, 0, array.length);
    }
    
    public static String decBytesToHex(byte[] buffs) {
		if (buffs == null || buffs.length == 0) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		int len = buffs.length;
		for (int i = 0; i < len; i++) {
			buffer.append(String.format("%02X", buffs[i]));
		}
		return buffer.toString();
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

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) return null;

        StringBuilder ret = new StringBuilder(2*bytes.length);

        for (int i = 0 ; i < bytes.length ; i++) {
            int b;

            b = 0x0f & (bytes[i] >> 4);
            ret.append("0123456789abcdef".charAt(b));

            b = 0x0f & bytes[i];
            ret.append("0123456789abcdef".charAt(b));
        }

        return ret.toString().toUpperCase();
    }

    public static String dumpHexString(byte[] array, int offset, int length)
    {
        StringBuilder result = new StringBuilder();

        byte[] line = new byte[16];
        int lineIndex = 0;

        result.append("\n0x");
        result.append(toHexString(offset));

        for (int i = offset ; i < offset + length ; i++)
        {
            if (lineIndex == 16)
            {
                result.append(" ");

                for (int j = 0 ; j < 16 ; j++)
                {
                    if (line[j] > ' ' && line[j] < '~')
                    {
                        result.append(new String(line, j, 1));
                    }
                    else
                    {
                        result.append(".");
                    }
                }

                result.append("\n0x");
                result.append(toHexString(i));
                lineIndex = 0;
            }

            byte b = array[i];
            result.append(" ");
            result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
            result.append(HEX_DIGITS[b & 0x0F]);

            line[lineIndex++] = b;
        }

        if (lineIndex != 16)
        {
            int count = (16 - lineIndex) * 3;
            count++;
            for (int i = 0 ; i < count ; i++)
            {
                result.append(" ");
            }

            for (int i = 0 ; i < lineIndex ; i++)
            {
                if (line[i] > ' ' && line[i] < '~')
                {
                    result.append(new String(line, i, 1));
                }
                else
                {
                    result.append(".");
                }
            }
        }

        return result.toString();
    }

    public static String dumpHex(byte[] array)
    {
        if(array==null)return "null";
        return dumpHex(array, 0, array.length);
    }
    
    public static String decBytesToHex(byte[] buffs, int length){
		if (buffs == null || length == 0) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append(String.format("%02X", buffs[i]));
		}
		return buffer.toString();
	}

    public static String dumpHex(byte[] array, int offset, int length)
    {
        StringBuilder result = new StringBuilder();

        for (int i = offset ; i < offset + length ; i++)
        {
            byte b = array[i];
            if(i!=offset){
                result.append(" ");
            }
            result.append("0x");
            result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
            result.append(HEX_DIGITS[b & 0x0F]);

        }
        result.append(" ");
        return result.toString();
    }

    public static String toHexString(byte b)
    {
        return toHexString(toByteArray(b));
    }

    public static String toHexStringX(byte b)
    {
        return "0x"+toHexString(toByteArray(b));
    }

    public static String toHexString(byte[] array)
    {
    	if (array == null){
    		return "null";
    	}
        return toHexString(array, 0, array.length);
    }

    public static String toHexString(byte[] array, int offset, int length)
    {
        char[] buf = new char[length * 2];

        int bufIndex = 0;
        for (int i = offset ; i < offset + length; i++)
        {
            byte b = array[i];
            buf[bufIndex++] = HEX_DIGITS[(b >>> 4) & 0x0F];
            buf[bufIndex++] = HEX_DIGITS[b & 0x0F];
        }

        return new String(buf);
    }

    public static String toHexString(int i)
    {
        return toHexString(toByteArray(i));
    }

    public static String toHexStringX(int i)
    {
        return "0x"+toHexString(toByteArray(i));
    }

    public static byte[] toByteArray(byte b)
    {
        byte[] array = new byte[1];
        array[0] = b;
        return array;
    }

    public static byte[] toByteArray(int i)
    {
        byte[] array = new byte[4];

        array[3] = (byte)(i & 0xFF);
        array[2] = (byte)((i >> 8) & 0xFF);
        array[1] = (byte)((i >> 16) & 0xFF);
        array[0] = (byte)((i >> 24) & 0xFF);

        return array;
    }

    private static int toByte(char c)
    {
        if (c >= '0' && c <= '9') return (c - '0');
        if (c >= 'A' && c <= 'F') return (c - 'A' + 10);
        if (c >= 'a' && c <= 'f') return (c - 'a' + 10);

        throw new RuntimeException ("Invalid hex char '" + c + "'");
    }

    public static byte[] hexStringToByteArray(String hexString)
    {
        int length = hexString.length();
        byte[] buffer = new byte[length / 2];

        for (int i = 0 ; i < length ; i += 2)
        {
            buffer[i / 2] = (byte)((toByte(hexString.charAt(i)) << 4) | toByte(hexString.charAt(i+1)));
        }

        return buffer;
    }

    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    public static String byte2bcd(byte[] bcds)
    {
        char[] ascii = "0123456789abcdef".toCharArray();
        byte[] temp = new byte[bcds.length * 2];
        for (int i = 0; i < bcds.length; i++) {
            temp[(i * 2)] = (byte)(bcds[i] >> 4 & 0xF);
            temp[(i * 2 + 1)] = (byte)(bcds[i] & 0xF);
        }
        StringBuffer res = new StringBuffer();

        for (int i = 0; i < temp.length; i++) {
            res.append(ascii[temp[i]]);
        }
        return res.toString().toUpperCase();
    }

    public static byte[] str2Bcd(String asc) {

        int len = asc.length();

        int mod = len % 2;

        if (mod != 0) {

            asc = "0" + asc;

            len = asc.length();

        }

        byte abt[] = new byte[len];

        if (len >= 2) {

            len = len / 2;

        }

        byte bbt[] = new byte[len];

        abt = asc.getBytes();

        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {

            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {

                j = abt[2 * p] - '0';

            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {

                j = abt[2 * p] - 'a' + 0x0a;

            } else {

                j = abt[2 * p] - 'A' + 0x0a;

            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {

                k = abt[2 * p + 1] - '0';

            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {

                k = abt[2 * p + 1] - 'a' + 0x0a;

            } else {

                k = abt[2 * p + 1] - 'A' + 0x0a;

            }

            int a = (j << 4) + k;

            byte b = (byte) a;

            bbt[p] = b;

        }

        return bbt;

    }
    
	public static int byteToInt(byte[] b) {
		if (b == null || b.length > 4){
			return 0;
		}
		int value = 0;
		for (int i = 0; i < b.length; i++) {
			int shift = (b.length - 1 - i) * 8;
			value += (b[i + 0] & 0x000000FF) << shift;
		}
		return value;
	}


    public static String formatHexString(String hexString) {
        if (hexString != null && hexString.length() > 0) {
            String ret = hexString.toUpperCase();
            char[] retChars = ret.toCharArray();
            boolean formatCorrect = true;
            char[] var4 = retChars;
            int var5 = retChars.length;

            int var6;
            for(var6 = 0; var6 < var5; ++var6) {
                char c = var4[var6];
                if ((c < '0' || c > '9') && (c < 'A' || c > 'F')) {
                    formatCorrect = false;
                    break;
                }
            }

            if (formatCorrect) {
                return ret.length() % 2 != 0 ? "0" + ret : ret;
            } else {
                ret = ret.replaceAll("0X", "");
                retChars = ret.toCharArray();
                StringBuilder sb = new StringBuilder();
                char[] var10 = retChars;
                var6 = retChars.length;

                for(int var11 = 0; var11 < var6; ++var11) {
                    char c = var10[var11];
                    if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F') {
                        sb.append(c);
                    }
                }

                if (sb.length() % 2 != 0) {
                    sb.insert(0, '0');
                }

                return sb.toString();
            }
        } else {
            return null;
        }
    }

//    public static byte[] hexStringToByte(String hex)
//    {
//        int len = hex.length() / 2;
//        byte[] result = new byte[len];
//        char[] achar = hex.toCharArray();
//        for (int i = 0; i < len; i++) {
//            int pos = i * 2;
//            result[i] = (byte)(tobyte(achar[pos]) << 4 | tobyte(achar[(pos + 1)]));
//        }
//        return result;
//    }
//
//    private static byte tobyte(char c) {
//        byte b = (byte)"0123456789ABCDEF".indexOf(c);
//        return b;
//    }
}
