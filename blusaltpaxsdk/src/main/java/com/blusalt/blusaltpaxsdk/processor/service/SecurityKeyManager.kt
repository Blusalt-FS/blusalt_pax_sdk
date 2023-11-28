package com.blusalt.blusaltpaxsdk.processor.service

import android.content.Context
import android.os.RemoteException
import android.util.Log
import com.blusalt.blusaltpaxsdk.utils.HexDump
import com.blusalt.blusaltpaxsdk.utils.PedApiUtils


class SecurityKeyManager(context: Context?) : SecurityKeyService() {

    private val TAG = "PinPad3DesActivity"
    private var isSupport = false
    private val KEK_KEY_INDEX = 0
    private val MASTER_KEY_INDEX = 0
    private val WORK_KEY_INDEX = 0

    var sessionKeyInit = false

    fun saveTMK(tmk: String, clearTmk: String): Int {

        var ret = 1

//        if (!isSupport) {
//            Log.e(TAG,"KEY INJECTION API NOT SUPPORTED")
//            return ret
//        }

        return try {
            val verifyKcv = encryptWithDES(clearTmk, "0000000000000000")
            Log.e(TAG, "TMK KCV check: ClearTMK = [{$clearTmk}]")
            Log.e(TAG, "TMK KCV check: VerifiedKCV = [{$verifyKcv}]")
            try {

//                val masterKey = hexToBytes(tmk.substring(0, 32))
//                var ekcv = hexStringToByte(verifyKcv)
                Log.e(TAG, "TMK KEY: " + tmk.substring(0, 32))
                Log.e(TAG, "TMK Verify KVC: $verifyKcv")
//                ekcv = ByteArray(4)

//                val result = innerpinpad.injectSecureTMK(KEK_KEY_INDEX,
//                    MASTER_KEY_INDEX,
//                    masterKey,
//                    ekcv
//                )

                val clearmasterKey = HexDump.hexStringToByteArray(clearTmk.substring(0, 32))
                var clearekcv = HexDump.hexStringToByteArray(verifyKcv.substring(0, 8))
                Log.e(TAG, "TMK KEY: " + clearTmk.substring(0, 32))
                Log.e(TAG, "TMK Verify KVC: $verifyKcv")
//                ekcv = ByteArray(4)
//                val ClearResult = innerpinpad.injectClearTMK(MASTER_KEY_INDEX,
//                    clearmasterKey,
//                    clearekcv
//                )

                PedApiUtils.writeTMKOURS(clearTmk);
                Log.e(TAG, "Clear TMK Inject ");
                0
            } catch (e: RemoteException) {
                e.printStackTrace()
//                e.message?.let { log(it) }
            }
//            if (ret == 0) {
//                localData.tmk = clearTmk
//                Log.e(TAG, "TMK injection result: $ret")
//                ret
//            }
            0
//            Log.e(TAG, "Clear TMK Inject " + ret);
        } catch (rex: Exception) {
            rex.printStackTrace()
            ret
        }
    }

    fun saveTSK(clearTsk: String): Int {

        var ret = 1

//        if (!isSupport) {
//            Log.e(TAG, "KEY INJECTION API NOT SUPPORTED")
//            return ret
//        }

        return try {
            val verifyKcv = encryptWithDES(clearTsk, "0000000000000000")
            Log.e(TAG, "TSK KCV check: ClearTSK = [{$clearTsk}]")
            Log.e(TAG, "TSK KCV check: VerifiedKCV = [{$verifyKcv}]")
            // SK:DEK
            var ret = 1 // Acquirer.getInstance().addAcquireKey(key02);
            try {
                //Encrypted Mac key
                val emackey = HexDump.hexStringToByteArray(clearTsk.substring(0, 32))
//                var ekcv = hexStringToByte(verifyKcv)
//                ekcv = ByteArray(4)

                Log.e(TAG, "TSK Inject (WorkKey)")
                0
            } catch (e: Exception) {
                e.printStackTrace()
//                e.message?.let { log(it) }
            }
//            if (ret != 0) {
//                ret
//            } else {
////                log("TSK injection result: $ret")
//                ret
//            }
            0
        } catch (rex: Exception) {
            rex.printStackTrace()
            ret
        }
    }

    fun saveTPK(clearTpk: String): Int {
        var ret = 1

//        if (!isSupport) {
//            Log.e(TAG, "KEY INJECTION API NOT SUPPORTED")
//            return ret
//        }
        return try {
//            val verifyKcv = encryptWithDES(clearTpk, "0000000000000000")
//            var ekcv = hexStringToByte(verifyKcv)

            Log.e(TAG, "TPK KCV check: ClearTPK = [{$clearTpk}]")
//            Log.e(TAG,"TPK KCV check: VerifiedKCV = [{$verifyKcv}]")
            // SK:PEK
            ret = 1 // Acquirer.getInstance().addAcquireKey(key03);
            try {
                //Encrypted PIN key
                val epinKey = HexDump.hexStringToByteArray(clearTpk)

                PedApiUtils.writeTPKOURS(clearTpk);

                Log.e(TAG, "TPK Inject (WorkKey):")
                0
            } catch (e: RemoteException) {
//                e.message?.let { log(it) }
            }
//            if (ret != 0) {
//                ret
//            } else {
////                log("TPK injection result: $ret")
//                ret
//            }
            0
        } catch (rex: Exception) {
            rex.printStackTrace()
            ret
        }
    }


    companion object {
        private const val LOG_TAG = "POSKEYSERVICE"
        const val ZMK_KEY_ID = 1
        const val TMK_KEY_ID = 2
        const val TSK_KEY_ID = 3
        const val TPK_KEY_ID = 4
        const val PACKAGE_NAME = "com.pos"
        private const val verifyCheckDigit = true
    }

    init {
        this.context = context
        object : Thread() {
            override fun run() {
                try {
//                    pinPad = Pos.getINSTANCE().pinPad
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}
