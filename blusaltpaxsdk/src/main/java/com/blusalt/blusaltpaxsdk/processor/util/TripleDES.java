package com.blusalt.blusaltpaxsdk.processor.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class TripleDES {

   /* private static final String ALGORITHM = "TripleDES";
    private static final String MODE = "ECB";
   // private static final String PADDING = "NoPadding";

    */
    private static final String ALGORITHM = "DESede";
    //private static final String MODE = "CBC";
    private static final String MODE = "ECB";
    private static final String PADDING = "NoPadding";
   // private static final String PADDING = "PKCS5Padding";

    String convertere;
    String pinEncoded ;

    /** algorithm/mode/padding */
    private static final String TRANSFORMATION = ALGORITHM+"/"+MODE+"/"+PADDING;

    final String key;
    int pinLength;

    public TripleDES(String key, int pinLength) {
        this.key = key;
        this.pinLength = pinLength;
    }

    public TripleDES(String key) {
        this.key = key;
    }

    public String encrypt(String pan, String pinClear) throws Exception {

        if(pinClear.length() != pinLength) {
            System.out.println("Incorrect PIN length given. Please fix! pinClear.size() " + "!= " + " pinLength : " + pinClear.length() + " !=" + pinLength);
        }

        String pinEncoded = encodePinBlockAsHex(pan, pinClear);
        byte[] tmp = h2b(this.key);
        byte[] key = new byte[24];
        System.arraycopy(tmp, 0, key, 0, 16);
        System.arraycopy(tmp, 0, key, 16, 8);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        //Cipher cipher = Cipher.getInstance("TripleDES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
        byte[] plaintext = cipher.doFinal(h2b(pinEncoded));
        return b2h(plaintext);
    }

    public String encryptPinBlock(String clearPinBlock){

        try{
            byte[] tmp = h2b(this.key);
            byte[] key = new byte[24];
            System.arraycopy(tmp, 0, key, 0, 16);
            System.arraycopy(tmp, 0, key, 16, 8);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
            byte[] plaintext = cipher.doFinal(h2b(clearPinBlock));
            convertere=b2h(plaintext);

        }catch(Exception e){
            e.printStackTrace();
        }

        return convertere;


    }

    public String decryptPinBlock(String encryptedPinBlock){
        try {
            byte[] tmp = h2b(this.key);
            byte[] key = new byte[24];
            System.arraycopy(tmp, 0, key, 0, 16);
            System.arraycopy(tmp, 0, key, 16, 8);
            Cipher cipher = null;

            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
            byte[] plaintext = cipher.doFinal(h2b(encryptedPinBlock));
            pinEncoded = b2h(plaintext);
        }catch (Exception e){
            e.printStackTrace();
        }
        return pinEncoded;
    }

    public String decrypt(String pan, String encryptedPin) throws Exception {
        byte[] tmp = h2b(this.key);
        byte[] key = new byte[24];
        System.arraycopy(tmp, 0, key, 0, 16);
        System.arraycopy(tmp, 0, key, 16, 8);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM));
        byte[] plaintext = cipher.doFinal(h2b(encryptedPin));
        String pinEncoded = b2h(plaintext);
        return decodePinBlock(pan, pinEncoded);
    }

    public String decodePinBlock(String pan, String pinEncoded) throws Exception {
        pan = pan.substring(pan.length() - 12 - 1, pan.length() - 1);
        String paddingPAN = "0000".concat(pan);
        byte[] pinBlock = xorBytes(h2b(paddingPAN), h2b(pinEncoded));
        return b2h(pinBlock).substring(2, pinLength+2);
    }

    public String encodePinBlockAsHex(String pan, String pin) throws Exception {
        pan = pan.substring(pan.length() - 12 - 1, pan.length() - 1);
        String paddingPAN = "0000".concat(pan);

        String Fs = "FFFFFFFFFFFFFFFF";
        String paddingPIN = "0" + pin.length() + pin + Fs.substring(2 + pin.length(), Fs.length());

        byte[] pinBlock = xorBytes(h2b(paddingPAN), h2b(paddingPIN));

        return b2h(pinBlock);
    }

    private static byte[] xorBytes(byte[] a, byte[] b) throws Exception {
        if (a.length != b.length) {
            throw new Exception();
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            int r = 0;
            r = a[i] ^ b[i];
            r &= 0xFF;
            result[i] = (byte) r;
        }
        return result;
    }

    private static byte[] h2b(String hex) {
        if ((hex.length() & 0x01) == 0x01)
            throw new IllegalArgumentException();
        byte[] bytes = new byte[hex.length() / 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            int hi = Character.digit((int) hex.charAt(idx * 2), 16);
            int lo = Character.digit((int) hex.charAt(idx * 2 + 1), 16);
            if ((hi < 0) || (lo < 0))
                throw new IllegalArgumentException();
            bytes[idx] = (byte) ((hi << 4) | lo);
        }
        return bytes;
    }

    private static String b2h(byte[] bytes) {
        char[] hex = new char[bytes.length * 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            int hi = (bytes[idx] & 0xF0) >>> 4;
            int lo = (bytes[idx] & 0x0F);
            hex[idx * 2] = (char) (hi < 10 ? '0' + hi : 'A' - 10 + hi);
            hex[idx * 2 + 1] = (char) (lo < 10 ? '0' + lo : 'A' - 10 + lo);
        }
        return new String(hex);
    }

    public static String threeDesDecrypt(String encryptedToken, String key) {

        Log.d("TripleDES: encryptedKey ", encryptedToken);
        Log.d("TripleDES: decryptingKey ", key);

//        Log.d("processOnlineTransaction:  isTestPlaform  >>>>>> ", isTestPlaform);

        byte[] mkB = TripleDES.hexToByte(key + key.substring(0, 16));
        SecretKey keyse = TripleDES.readKey(mkB);
//        Log.d("processOnlineTransaction:  isTestPlaform  >>>>>> ", new Gs);

        return TripleDES.Decrypt(keyse, encryptedToken);
    }

    public static String Decrypt(Key key, String cipherComp) {

        try {

            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] ciphertext = hexToByte(cipherComp);
            CipherOutputStream out;

            out = new CipherOutputStream(bytes, cipher);
            out.write(ciphertext);
            out.flush();
            out.close();
            byte[] deciphertext = bytes.toByteArray();
            bytes.flush();
            bytes.close();

            String decrypted = ToHexString(deciphertext);
            java.util.Arrays.fill(ciphertext, (byte) 0);
            java.util.Arrays.fill(deciphertext, (byte) 0);
            return decrypted;
        } catch (IOException ex) {
            //LOG.error(" Caused: ", ex);
            return null;
        } catch (NoSuchPaddingException ex) {

            //LOG.error(" Caused: ", ex);
            return null;
        } catch (NoSuchAlgorithmException ex) {

            //LOG.error(" Caused: ", ex);
            return null;
        } catch (InvalidKeyException ex) {

            //LOG.error("Caused:  ", ex);
            return null;
        }
    }

    public static String ToHexString(byte[] toAsciiData) {

        String hexString = "";
        for (byte b : toAsciiData) {
            hexString += String.format("%02X", b);
        }
        return hexString;
    }

    public static byte[] hexToByte(String hexString) {

        String str = new String("0123456789ABCDEF");
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0, j = 0; i < hexString.length(); i++) {

            byte firstQuad = (byte) ((str.indexOf(hexString.charAt(i))) << 4);
            byte secondQuad = (byte) str.indexOf(hexString.charAt(++i));
            bytes[j++] = (byte) (firstQuad | secondQuad);
        }
        return bytes;
    }

    public static SecretKey readKey(byte[] rawkey) {
        // Read the raw bytes from the keyfile
        try {
            DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
            SecretKey key;
            key = keyfactory.generateSecret(keyspec);
            key = keyfactory.translateKey(key);
            return key;
        } catch (InvalidKeySpecException ex) {

            //LOG.error("Error reading key", ex);
            return null;
        } catch (NoSuchAlgorithmException ex) {

            //LOG.error("Error reading key", ex);
            return null;
        } catch (InvalidKeyException ex) {

            //LOG.error("Error reading key", ex);
            return null;
        }
    }

    public static String threeDesEncrypt(String keyComponent1, String keyComponent2, String encryptedToken) {

        byte[] keyB1 = TripleDES.hexToByte(keyComponent1 + keyComponent1.substring(0, 16));
        byte[] keyB2 = TripleDES.hexToByte(keyComponent2 + keyComponent2.substring(0, 16));

        for (int i = 0; i < keyB2.length; i++) {
            keyB1[i] = (byte) (((byte) (keyB1[i] ^ keyB2[i])));
        }

        SecretKey key = TripleDES.readKey(keyB1);
        return TripleDES.Encrypt(key, encryptedToken);
    }

    public static String Encrypt(Key key, String clearComp) {
        try {
            Cipher cipher;
            cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] clearText = hexToByte(clearComp);

            CipherOutputStream out = new CipherOutputStream(bytes, cipher);
            out.write(clearText);
            out.flush();
            out.close();
            byte[] ciphertext = bytes.toByteArray();
            bytes.flush();
            bytes.close();

            String encrypted = ToHexString(ciphertext);
            java.util.Arrays.fill(clearText, (byte) 0);
            java.util.Arrays.fill(ciphertext, (byte) 0);
            return encrypted;
        } catch (IOException ex) {

            //LOG.error(" Caused: ", ex);
            return null;
        } catch (NoSuchPaddingException ex) {

            //LOG.error(" Caused: ", ex);
            return null;
        } catch (NoSuchAlgorithmException ex) {

            //LOG.error(" Caused: ", ex);
            return null;
        } catch (InvalidKeyException ex) {

            //LOG.error("Caused:  ", ex);
            return null;
        }
    }

}