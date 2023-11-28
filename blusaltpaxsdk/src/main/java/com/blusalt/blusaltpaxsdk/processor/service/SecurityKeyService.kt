package com.blusalt.blusaltpaxsdk.processor.service

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.DownloadReasonCode
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.DownloadStatus
import com.blusalt.blusaltpaxsdk.processor.util.ByteUtil
import com.blusalt.blusaltpaxsdk.processor.util.KeyException
import com.blusalt.blusaltpaxsdk.processor.util.ConstantUtils
import com.blusalt.blusaltpaxsdk.processor.util.StringUtil
import okhttp3.internal.and
import java.io.IOException
import java.math.BigInteger
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

open class SecurityKeyService {

    protected var context: Context? = null

    fun generateRSAKey() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val generator =
                    KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
                generator.initialize(KeyGenParameterSpec.Builder(
                    ConstantUtils.RSA_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                    .setDigests(KeyProperties.DIGEST_SHA1,
                        KeyProperties.DIGEST_SHA224,
                        KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build()
                )
                generator.generateKeyPair()
            } else {
                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                end.add(Calendar.YEAR, 20)
                val spec = KeyPairGeneratorSpec.Builder(context!!)
                    .setAlias(ConstantUtils.RSA_KEY_ALIAS)
                    .setSubject(X500Principal("CN=Arca Networks ," +
                            " O=IT Dept" +
                            " C=Nigeria"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()
                val generator =
                    KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
                generator.initialize(spec)
                generator.generateKeyPair()
            }
        } catch (e: NoSuchAlgorithmException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GENERATE_KEY_PAIR,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to generate RSA key in SecurityKeyService.generateRSAKey : " + e.message, e
            )
        } catch (e: NoSuchProviderException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GENERATE_KEY_PAIR,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to generate RSA key in SecurityKeyService.generateRSAKey : " + e.message,
                e
            )
        } catch (e: InvalidAlgorithmParameterException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GENERATE_KEY_PAIR,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to generate RSA key in SecurityKeyService.generateRSAKey : " + e.message,
                e
            )
        }
    }

    fun getPrivateKey(): PrivateKey? {
        return try {
            val ks = KeyStore.getInstance("AndroidKeyStore")
            ks.load(null)
            val entry = ks.getEntry(ConstantUtils.RSA_KEY_ALIAS, null)
            if (entry !is KeyStore.PrivateKeyEntry) {
                //                Log.w(TAG, "Not an instance of a PrivateKeyEntry");
                null
            } else (entry as KeyStore.PrivateKeyEntry).privateKey
            //byte[] encodedPrivkey = privateKey.getEncoded();
            //String base64 = Base64.encodeToString(encodedPrivkey, Base64.DEFAULT);
            //return base64;
        } catch (e: KeyStoreException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PRIVATE_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Private key in SecurityKeyService.getPrivateKey : " + e.message,
                e
            )
        } catch (e: NoSuchAlgorithmException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PRIVATE_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Private key in SecurityKeyService.getPrivateKey : " + e.message,
                e
            )
        } catch (e: IOException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PRIVATE_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Private key in SecurityKeyService.getPrivateKey : " + e.message,
                e
            )
        } catch (e: CertificateException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PRIVATE_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Private key in SecurityKeyService.getPrivateKey : " + e.message,
                e
            )
        } catch (e: UnrecoverableEntryException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PRIVATE_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Private key in SecurityKeyService.getPrivateKey : " + e.message,
                e
            )
        }
    }

    val publicKey: PublicKey?
        get() {
            return try {
                val ks =
                    KeyStore.getInstance("AndroidKeyStore")
                ks.load(null)
                val entry =
                    ks.getEntry(ConstantUtils.RSA_KEY_ALIAS, null)
                if (entry !is KeyStore.PrivateKeyEntry) {
                    return null
                }
                // Get certificate of public key
                val cert =
                    ks.getCertificate(ConstantUtils.RSA_KEY_ALIAS)
                // Get public key
                cert.publicKey
            } catch (e: KeyStoreException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to get Public key in SecurityKeyService.getPublicKey : " + e.message,
                    e
                )
            } catch (e: NoSuchAlgorithmException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to get Public key in SecurityKeyService.getPublicKey : " + e.message,
                    e
                )
            } catch (e: IOException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to get Public key in SecurityKeyService.getPublicKey : " + e.message,
                    e
                )
            } catch (e: CertificateException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to get Public key in SecurityKeyService.getPublicKey : " + e.message,
                    e
                )
            } catch (e: UnrecoverableEntryException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to get Public key in SecurityKeyService.getPublicKey : " + e.message,
                    e
                )
            }
        }


    @JvmName("getBase64PublicKey1")
    fun getBase64PublicKey(): String? {
        return try {
            val ks = KeyStore.getInstance("AndroidKeyStore")
            ks.load(null)
            val entry =
                ks.getEntry(ConstantUtils.RSA_KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry
                    ?: //                Log.w(TAG, "Not an instance of a PrivateKeyEntry");
                    return null
            // Get certificate of public key
            val cert =
                ks.getCertificate(ConstantUtils.RSA_KEY_ALIAS)
            // Get public key
            val publicKey = cert.publicKey
            val encodedPublickey = publicKey.encoded
            Base64.encodeToString(encodedPublickey, Base64.NO_WRAP)
        } catch (e: KeyStoreException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Public key in SecurityKeyService.getBase64PublicKey : " + e.message,
                e
            )
        } catch (e: NoSuchAlgorithmException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Public key in SecurityKeyService.getBase64PublicKey : " + e.message,
                e
            )
        } catch (e: IOException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Public key in SecurityKeyService.getBase64PublicKey : " + e.message,
                e
            )
        } catch (e: CertificateException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Public key in SecurityKeyService.getBase64PublicKey : " + e.message,
                e
            )
        } catch (e: UnrecoverableEntryException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_GET_PUBLIC_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to get Public key in SecurityKeyService.getBase64PublicKey : " + e.message,
                e
            )
        }
    }

    fun encryptWithDES(key: String?, cipherHex: String?): String {
        return try {
            // create a binary key from the argument key (seed)
            val tmp = hex2bin(key)
            val keyBytes = ByteArray(24)
            System.arraycopy(tmp, 0, keyBytes, 0, 16)
            System.arraycopy(tmp, 0, keyBytes, 16, 8)
            val sk: SecretKey = SecretKeySpec(keyBytes, "DESede")
            // create an instance of cipher
            val cipher = Cipher.getInstance("DESede/ECB/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, sk)

            // enctypt!
            val encrypted = cipher.doFinal(hex2bin(cipherHex))
            bin2hex(encrypted)
        } catch (e: NoSuchAlgorithmException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to encrypt with DES key in SecurityKeyService.encryptWithDES : " + e.message,
                e
            )
        } catch (e: NoSuchPaddingException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to encrypt with DES key in SecurityKeyService.encryptWithDES : " + e.message,
                e
            )
        } catch (e: InvalidKeyException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to encrypt with DES key in SecurityKeyService.encryptWithDES : " + e.message,
                e
            )
        } catch (e: IllegalBlockSizeException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to encrypt with DES key in SecurityKeyService.encryptWithDES : " + e.message,
                e
            )
        } catch (e: BadPaddingException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to encrypt with DES key in SecurityKeyService.encryptWithDES : " + e.message,
                e
            )
        }
    }

    fun decryptWithDES(key: String?, encryptedHex: String?): String {
        return try {
            // create a binary key from the argument key (seed)
            val tmp = hex2bin(key)
            val keyBytes = ByteArray(24)
            System.arraycopy(tmp, 0, keyBytes, 0, 16)
            System.arraycopy(tmp, 0, keyBytes, 16, 8)
            val sk: SecretKey = SecretKeySpec(keyBytes, "DESede")

            // do the decryption with that key
            val cipher = Cipher.getInstance("DESede/ECB/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, sk)
            val decrypted = cipher.doFinal(hex2bin(encryptedHex))
            bin2hex(decrypted)
        } catch (e: NoSuchAlgorithmException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with DES key in SecurityKeyService.decryptWithDES : " + e.message,
                e
            )
        } catch (e: NoSuchPaddingException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with DES key in SecurityKeyService.decryptWithDES : " + e.message,
                e
            )
        } catch (e: InvalidKeyException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with DES key in SecurityKeyService.decryptWithDES : " + e.message,
                e
            )
        } catch (e: IllegalBlockSizeException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with DES key in SecurityKeyService.decryptWithDES : " + e.message,
                e
            )
        } catch (e: BadPaddingException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with DES key in SecurityKeyService.decryptWithDES : " + e.message,
                e
            )
        }
    }

    fun encryptWithRSAReturnBase64String(cipherText: String?): String {
        var encodedBytes: ByteArray? = null
        return try {
            val publicKey = publicKey
            val cipher =
                Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            encodedBytes = cipher.doFinal(ByteUtil.hexToBytes(cipherText.toString()))
            String(Base64.encode(encodedBytes, Base64.NO_WRAP))
        } catch (e: NoSuchAlgorithmException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSAReturnBase64String: " + e.message,
                e
            )
        } catch (e: NoSuchPaddingException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSAReturnBase64String: " + e.message,
                e
            )
        } catch (e: InvalidKeyException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSAReturnBase64String: " + e.message,
                e
            )
        } catch (e: IllegalBlockSizeException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSAReturnBase64String: " + e.message,
                e
            )
        } catch (e: BadPaddingException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSAReturnBase64String: " + e.message,
                e
            )
        }
    }

    fun decryptBase64StringWithRSA(base64CipherText: String?): String {
        return ByteUtil.bytes2HexString(decryptBase64StringWithRSAByte(base64CipherText))
    }

    fun decryptBase64StringWithRSAByte(base64CipherText: String?): ByteArray? {
        var decodedBytes: ByteArray? = null
        return try {
            val privateKeyEntry = getPrivateKey()
            val c = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            c.init(Cipher.DECRYPT_MODE, privateKeyEntry)
            decodedBytes = c.doFinal(Base64.decode(base64CipherText, Base64.NO_WRAP))
            println("Decoded Bytes: ${String(decodedBytes)}")
            decodedBytes
            //return new String(decodedBytes);
        } catch (bpex: BadPaddingException) {
            try {
                val privateKeyEntry = getPrivateKey()
                val c = Cipher.getInstance("RSA/None/NoPadding")
                c.init(Cipher.DECRYPT_MODE, privateKeyEntry)
                decodedBytes = c.doFinal(Base64.decode(base64CipherText, Base64.NO_WRAP))
                var newClearKey = ByteUtil.bytes2HexString(decodedBytes)
                if (!StringUtil.isEmpty(newClearKey) && newClearKey.length > 32) {
                    newClearKey = newClearKey.substring(0, 32)
                }
                println("Decoded Bytes: ${newClearKey}")
                ByteUtil.hexString2Bytes(newClearKey)
            } catch (ex: NoSuchAlgorithmException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to decrypt with RSA key in SecurityKeyService.decryptBase64StringWithRSAByte: " + ex.message,
                    ex
                )
            } catch (ex: NoSuchPaddingException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to decrypt with RSA key in SecurityKeyService.decryptBase64StringWithRSAByte: " + ex.message,
                    ex
                )
            } catch (ex: InvalidKeyException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to decrypt with RSA key in SecurityKeyService.decryptBase64StringWithRSAByte: " + ex.message,
                    ex
                )
            } catch (ex: IllegalBlockSizeException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to decrypt with RSA key in SecurityKeyService.decryptBase64StringWithRSAByte: " + ex.message,
                    ex
                )
            } catch (ex: BadPaddingException) {
                throw KeyException(
                    DownloadStatus.ERROR,
                    DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                    ConstantUtils.GENERIC_ERROR_CODE,
                    "Unable to decrypt with RSA key in SecurityKeyService.decryptBase64StringWithRSAByte: " + ex.message,
                    ex
                )
            }
        } catch (ex: NoSuchAlgorithmException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.decryptBase64StringWithRSAByte: " + ex.message,
                ex
            )
        } catch (ex: NoSuchPaddingException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.decryptBase64StringWithRSAByte: " + ex.message,
                ex
            )
        } catch (ex: InvalidKeyException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.decryptBase64StringWithRSAByte: " + ex.message,
                ex
            )
        } catch (ex: IllegalBlockSizeException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_DECRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.decryptBase64StringWithRSAByte: " + ex.message,
                ex
            )
        }
    }

    fun encryptWithRSA(publicKeyBase64: String?, cipherText: String?): String? {
        var encodedBytes: ByteArray? = null
        return try {
            val publicKey = publicKey
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedBytes = cipher.doFinal(cipherText?.toByteArray())
            encodedBytes = Base64.encode(encryptedBytes, Base64.NO_WRAP)
            String(encodedBytes)
        } catch (e: NoSuchAlgorithmException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSA: " + e.message,
                e
            )
        } catch (e: NoSuchPaddingException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSA: " + e.message,
                e
            )
        } catch (e: InvalidKeyException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSA: " + e.message,
                e
            )
        } catch (e: IllegalBlockSizeException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSA: " + e.message,
                e
            )
        } catch (e: BadPaddingException) {
            throw KeyException(
                DownloadStatus.ERROR,
                DownloadReasonCode.UNABLE_TO_ENCRYPT_RSA_KEY,
                ConstantUtils.GENERIC_ERROR_CODE,
                "Unable to decrypt with RSA key in SecurityKeyService.encryptWithRSA: " + e.message,
                e
            )
        }
    }

    @Throws(Exception::class)
    fun getMac(seed: String?, macDataBytes: ByteArray?): String? {
        val keyBytes = hex2bin(seed)
        val digest = MessageDigest.getInstance(ConstantUtils.SHA256)
        digest.update(keyBytes, 0, keyBytes.size)
        digest.update(macDataBytes, 0, macDataBytes?.size!!)
        val hashedBytes = digest.digest()
        var hashText = bin2hex(hashedBytes)
        hashText = hashText.replace(" ", "")
        if (hashText.length < 64) {
            val numberOfZeroes = 64 - hashText.length
            var zeroes = ""
            var temp = hashText
            for (i in 0 until numberOfZeroes) zeroes += "0"
            temp = zeroes + temp
            return temp
        }
        return hashText
    }

    private fun toHexString(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        val formatter = Formatter(sb)
        for (b in bytes) {
            formatter.format("%02x", b)
        }
        return sb.toString()
    }

    private fun hex2bin(hex: String?): ByteArray {
        if (hex == null) return ByteArray(0)
        require(hex.length and 0x01 != 0x01)
        val bytes = ByteArray(hex.length / 2)
        for (idx in bytes.indices) {
            val hi = Character.digit(hex[idx * 2], 16)
            val lo = Character.digit(hex[idx * 2 + 1], 16)
            require(!(hi < 0 || lo < 0))
            bytes[idx] = (hi shl 4 or lo).toByte()
        }
        return bytes
    }

    private fun bin2hex(bytes: ByteArray): String {
        val hex = CharArray(bytes.size * 2)
        for (idx in bytes.indices) {
            val hi: Int = bytes[idx] and 0xF0 ushr 4
            val lo: Int = bytes[idx] and 0x0F
            hex[idx * 2] = (if (hi < 10) '0'.toInt() + hi else 'A'.toInt() - 10 + hi).toChar()
            hex[idx * 2 + 1] = (if (lo < 10) '0'.toInt() + lo else 'A'.toInt() - 10 + lo).toChar()
        }
        return String(hex)
    }

    fun privateKeyExists(): Boolean {
        return getPrivateKey() != null
    }

//    fun getExportedTmk(tmk: String?): String? {
//        println("${this.javaClass.simpleName} ==> Calling key export service, check variable localData.ifManualTerminalSetup = ${localData.ifManualTerminalSetup}.")
//        return if (localData.ifManualTerminalSetup) {
//            val encryptedBase64Zmk: String = localData.zmk.toString()
//            if (StringUtil.isEmpty(encryptedBase64Zmk)) {
//                throw KeyException(DownloadStatus.ERROR,
//                    DownloadReasonCode.MISSING_ZMK,
//                    ConstantUtils.GENERIC_ERROR_CODE,
//                    "You have indicated you will be supplying, but have not set the ZMK for POS.")
//            }
//            val clearZmk = decryptBase64StringWithRSA(encryptedBase64Zmk)
//            val clearTmk = decryptWithDES(clearZmk, tmk)
//            encryptWithRSAReturnBase64String(clearTmk)
//        } else {
//            val latch = CountDownLatch(1)
//            val terminalKey = TerminalKey()
//            terminalKey.keyZone = "POS_NIBSS"
//            terminalKey.parentKey = getBase64PublicKey()
//            println("${this.javaClass.simpleName} ==> Calling key export service,  getBase64PublicKey()${getBase64PublicKey()}.")
//            terminalKey.parentKeyUsageType = "ZMK"
//            terminalKey.parentKeyCryptographicType = "RSA"
//            terminalKey.key = tmk
//            terminalKey.keyUsageType = "TMK"
//            terminalKey.keyCryptographicType = "TRIPLE_DES"
//            val terminalKeyExportResponse: Array<TerminalKeyExportResponse?> = arrayOfNulls(1)
//            println("${this.javaClass.simpleName} ==> Calling key export service.")
//            downloadServiceUseCase.exportTerminalKey(terminalKey,
//                object : TerminalKeyExportListener {
//                    override fun onSuccess(data: TerminalKeyExportResponse?) {
//                        latch.countDown()
//                        terminalKeyExportResponse[0] = data
//                    }
//
//                    override fun onFailed(message: String?) {
//                        latch.countDown()
//                    }
//                })
//            try {
//                latch.await()
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//            println("${this.javaClass.simpleName} ==> Calling key export service response = ${terminalKeyExportResponse[0]?.keyUnderParent}.")
//            if (terminalKeyExportResponse.isNotEmpty()) terminalKeyExportResponse[0]?.keyUnderParent else ""
//        }
//    }
}
