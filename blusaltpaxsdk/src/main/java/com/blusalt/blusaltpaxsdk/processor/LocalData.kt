package com.blusalt.blusaltpaxsdk.processor

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.blusalt.blusaltpaxsdk.processor.util.ConstantUtils
import com.google.gson.Gson

class LocalData(private var mContext: Context) {

    private var mPrefs: SharedPreferences =
        mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var mEditor: SharedPreferences.Editor
    private val gson = Gson()

    init {
        mEditor = mPrefs.edit()
    }
//    var remoteData: FincraAPIResponse
//        get() {
//            val json: String = mPrefs.getString(PREF_CONGIG_DATA, null).toString()
//            return gson.fromJson(json, FincraAPIResponse::class.java)
//        }
//        set(fincraAPIResponse) {
//            val userString: String = gson.toJson(fincraAPIResponse)
//            mEditor.putString(PREF_CONGIG_DATA, userString)
//            mEditor.apply()
//        }
//
//    var userData: FincraAPIResponse?
//        get() {
//            val json: String = mPrefs.getString(PREF_USER_DATA, null).toString()
//            return gson.fromJson(json, FincraAPIResponse::class.java)
//        }
//        set(user) {
//            val userString: String = gson.toJson(user ?: FincraAPIResponse() )
//            mEditor.putString(PREF_USER_DATA, userString)
//            mEditor.apply()
//        }

    fun setLocationLatitude(latitude: Float) {
        mEditor = mPrefs.edit()
        mEditor.putFloat(ConstantUtils.LOCATION_LATITUDE, latitude)
        mEditor.apply()
    }

    val locationLatitude: Double
        get() = mPrefs.getFloat(ConstantUtils.LOCATION_LATITUDE, 0.0f).toDouble()

    fun setLocationLongitude(longitude: Float) {
        mEditor = mPrefs.edit()
        mEditor.putFloat(ConstantUtils.LOCATION_LONGITUDE, longitude)
        mEditor.apply()
    }

    val locationLongitude: Double
        get() = mPrefs.getFloat(ConstantUtils.LOCATION_LONGITUDE, 0.0f).toDouble()

    var acquirerLogo: String?
        get() = mPrefs.getString(ConstantUtils.ACQUIRER_LOGO, "")
        set(acquirerlogo) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.ACQUIRER_LOGO, acquirerlogo)
            mEditor.apply()
        }

    var merchantId: String?
        get() = mPrefs.getString(ConstantUtils.ARCA_MERCHANT_ID, "000000")
        set(merchantId) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.ARCA_MERCHANT_ID, merchantId)
            mEditor.apply()
        }

    var merchantExtId: String?
        get() = mPrefs.getString(ConstantUtils.MERCHANT_EXTERNAL_ID, "000000000")
        set(merchantExtId) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.MERCHANT_EXTERNAL_ID, merchantExtId)
            mEditor.apply()
        }

    var merchantLogo: String?
        get() = mPrefs.getString(ConstantUtils.MERCHANT_LOGO, "")
        set(merchantlogo) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.MERCHANT_LOGO, merchantlogo)
            mEditor.apply()
        }

    var keysLoadedFlag: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.KEYSLOADED, false)
        set(ifset) {
            mEditor.putBoolean(ConstantUtils.KEYSLOADED, ifset)
            mEditor.commit()
        }

    var localMasterKeyLoadedFlag: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.LOCALMASTERKEYLOADED, false)
        set(ifset) {
            mEditor.putBoolean(ConstantUtils.LOCALMASTERKEYLOADED, ifset)
            mEditor.commit()
        }

    var tMKKeyFlag: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.SETTMKKEY, false)
        set(ifset) {
            mEditor.putBoolean(ConstantUtils.SETTMKKEY, ifset)
            mEditor.commit()
        }

    var macKeyFlag: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.SETMACKEY, false)
        set(ifset) {
            mEditor.putBoolean(ConstantUtils.SETMACKEY, ifset)
            mEditor.commit()
        }

    var pinKeyFlag: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.SETPINKEY, false)
        set(ifset) {
            mEditor.putBoolean(ConstantUtils.SETPINKEY, ifset)
            mEditor.commit()
        }

    var aIDLoadedFlag: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.AID_LOADED, false)
        set(ifAIDLoaded) {
            mEditor.putBoolean(ConstantUtils.AID_LOADED, ifAIDLoaded)
            mEditor.commit()
        }

    var cAPKLoadedFlag: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.CAPK_LOADED, false)
        set(ifCAPKLoaded) {
            mEditor.putBoolean(ConstantUtils.CAPK_LOADED, ifCAPKLoaded)
            mEditor.commit()
        }

    var eMVParamsLoadedFlag: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.EMVPARAMSLOADED, false)
        set(ifset) {
            mEditor.putBoolean(ConstantUtils.EMVPARAMSLOADED, ifset)
            mEditor.commit()
        }

    var terminalParamsLoadedFlag: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.TERMPARAMSLOADED, false)
        set(ifset) {
            mEditor.putBoolean(ConstantUtils.TERMPARAMSLOADED, ifset)
            mEditor.commit()
        }

    var login: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.LOGIN, false)
        set(ifLogin) {
            mEditor.putBoolean(ConstantUtils.LOGIN, ifLogin)
            mEditor.commit()
        }

    var lastKeyDownloadDate: Long
        get() = mPrefs.getLong(
            ConstantUtils.KEYDOWNLOADDATE,
            ConstantUtils.DEFAULTKEYDOWNLOADDATE
        )
        set(date) {
            mEditor.putLong(ConstantUtils.KEYDOWNLOADDATE, date)
            mEditor.commit()
        }

    var paymentOptions: String?
        get() = mPrefs.getString(ConstantUtils.TERMINAL_PAYMENT_OPTIONS, "")
        set(paymentOptions) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.TERMINAL_PAYMENT_OPTIONS, paymentOptions)
            mEditor.apply()
        }

    var maxTerminalDownloadFreq: Int
        get() = mPrefs.getInt(ConstantUtils.TERMINAL_DOWNLOAD_FREQ, 3)
        set(maxTerminalDownloadFreq) {
            mEditor = mPrefs.edit()
            mEditor.putInt(ConstantUtils.TERMINAL_DOWNLOAD_FREQ, maxTerminalDownloadFreq)
            mEditor.apply()
        }

    var currentBatchId: Long
        get() = mPrefs.getLong(ConstantUtils.CURRENT_BATCH_ID, -1)
        set(currentBatchId) {
            mEditor.putLong(ConstantUtils.CURRENT_BATCH_ID, currentBatchId)
            mEditor.commit()
        }

    var stan: Int
        get() = mPrefs.getInt(ConstantUtils.STAN, 0)
        set(stan) {
            mEditor.putInt(ConstantUtils.STAN, stan)
            mEditor.commit()
        }

    var retrievalRef: Long
        get() = mPrefs.getLong(ConstantUtils.RETRIEVAL_REF, 0L)
        set(retrievalRef) {
            mEditor.putLong(ConstantUtils.RETRIEVAL_REF, retrievalRef)
            mEditor.commit()
        }

    var merchantLoc: String?
        get() = if (mPrefs == null) null else mPrefs.getString(ConstantUtils.MERCHANT_LOC, "")
        set(merchantLoc) {
            mEditor.putString(ConstantUtils.MERCHANT_LOC, merchantLoc)
            mEditor.commit()
        }

    var terminalId: String?
        get() = if (mPrefs == null) null else mPrefs.getString(ConstantUtils.TERMINAL_ID, "")
        set(terminalId) {
            mEditor.putString(ConstantUtils.TERMINAL_ID, terminalId)
            mEditor.commit()
        }

    var nibssMerchantId: String?
        get() = if (mPrefs == null) null else mPrefs.getString(ConstantUtils.MERCHANT_ID, "")
        set(merchantId) {
            mEditor.putString(ConstantUtils.MERCHANT_ID, merchantId)
            mEditor.commit()
        }

    var merchantName: String?
        get() = mPrefs.getString(ConstantUtils.MERCHANT_NAME, "")
        set(merchantName) {
            mEditor.putString(ConstantUtils.MERCHANT_NAME, merchantName)
            mEditor.commit()
        }

    var merchantCategoryCode: String?
        get() = if (mPrefs == null) null else mPrefs.getString(
            ConstantUtils.MERCHANT_CATEGORY_CODE,
            "5399"
        )
        set(merchantCategoryCode) {
            mEditor.putString(ConstantUtils.MERCHANT_CATEGORY_CODE, merchantCategoryCode)
            mEditor.commit()
        }

    var purchaseSurcharge: String?
        get() = mPrefs.getString(ConstantUtils.PURCHASE_SURCHARGE, "0")
        set(purchaseSurcharge) {
            mEditor.putString(ConstantUtils.PURCHASE_SURCHARGE, purchaseSurcharge)
            mEditor.commit()
        }

    var acquirerId: String?
        get() = if (mPrefs == null) null else mPrefs.getString(ConstantUtils.ACQUIRER_ID, "")
        set(acquirerId) {
            mEditor.putString(ConstantUtils.ACQUIRER_ID, acquirerId)
            mEditor.commit()
        }

    var arcaAcquirerId: String?
        get() = mPrefs.getString(ConstantUtils.ARCA_ACQUIRER_ID, "0")
        set(acquirerId) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.ARCA_ACQUIRER_ID, acquirerId)
            mEditor.apply()
        }

    var acquirerCardNetworkIdList: String?
        get() = mPrefs.getString(ConstantUtils.ACQUIRER_CARD_NETWORK_ID, "{}")
        set(acquirerIdObject) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.ACQUIRER_CARD_NETWORK_ID, acquirerIdObject)
            mEditor.apply()
        }

    var pTSP: String?
        get() = mPrefs.getString(ConstantUtils.PTSP, "Glade Networks")
        set(ptsp) {
            mEditor.putString(ConstantUtils.PTSP, ptsp)
            mEditor.commit()
        }

    var appName: String?
        get() = mPrefs.getString(ConstantUtils.APP_NAME_ID, "Glade POS")
        set(appName) {
            mEditor.putString(ConstantUtils.APP_NAME_ID, appName)
            mEditor.commit()
        }

    var currencyCode: String?
        get() = if (mPrefs == null) null else mPrefs.getString(ConstantUtils.CURRENCY_CODE, "566")
        set(currencyCode) {
            mEditor.putString(ConstantUtils.CURRENCY_CODE, currencyCode)
            mEditor.commit()
        }

    var countryCode: String?
        get() = if (mPrefs == null) null else mPrefs.getString(ConstantUtils.COUNTRY_CODE, "566")
        set(countryCode) {
            mEditor.putString(ConstantUtils.COUNTRY_CODE, countryCode)
            mEditor.commit()
        }

    var pOSDataCode: String?
        get() = if (mPrefs == null) null else mPrefs.getString(ConstantUtils.POS_DATA_CODE, "")
        set(posDataCode) {
            mEditor.putString(ConstantUtils.POS_DATA_CODE, posDataCode)
            mEditor.commit()
        }

    var cTMSHost: String?
        get() = mPrefs.getString(ConstantUtils.CTMS_HOST, "")
        set(ctmsHost) {
            mEditor.putString(ConstantUtils.CTMS_HOST, ctmsHost)
            mEditor.commit()
        }

    var cTMSIP: String?
        get() = mPrefs.getString(ConstantUtils.CTMS_IP, "")
        set(ctmsIp) {
            mEditor.putString(ConstantUtils.CTMS_IP, ctmsIp)
            mEditor.commit()
        }

    var cTMSPort: Int
        get() = mPrefs.getInt(ConstantUtils.CTMS_PORT, 0)
        set(ctmsPort) {
            mEditor.putInt(ConstantUtils.CTMS_PORT, ctmsPort)
            mEditor.commit()
        }

    var cTMSTimeout: Int
        get() = mPrefs.getInt(ConstantUtils.CTMS_TIMEOUT, 60)
        set(ctmsTimeout) {
            mEditor.putInt(ConstantUtils.CTMS_TIMEOUT, ctmsTimeout)
            mEditor.commit()
        }

    var ifCTMSSSL: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.CTMS_SSL, false)
        set(ifCTMSSSL) {
            mEditor.putBoolean(ConstantUtils.CTMS_SSL, ifCTMSSSL)
            mEditor.commit()
        }

    //54.76.112.168,
    var mgtServerHost: String?
        get() = mPrefs.getString(ConstantUtils.MGT_SERVER_HOST, "") //54.76.112.168,
        set(mgtServerHost) {
            mEditor.putString(ConstantUtils.MGT_SERVER_HOST, mgtServerHost)
            mEditor.commit()
        }

    fun setMgtServerPort(mgtServerPort: Int) {
        mEditor.putInt(ConstantUtils.MGT_SERVER_PORT, mgtServerPort)
        mEditor.commit()
    }

    //80
    //8080
    val mgtServerServerPort: Int
        get() = mPrefs.getInt(ConstantUtils.MGT_SERVER_PORT, 0) //80

    var mgtServerTimeout: Int
        get() = mPrefs.getInt(ConstantUtils.MGT_SERVER_TIMEOUT, 60)
        set(mgtServerTimeout) {
            mEditor.putInt(ConstantUtils.MGT_SERVER_TIMEOUT, mgtServerTimeout)
            mEditor.commit()
        }

    var ifMgtServerSSL: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.MGT_SERVER_SSL, false)
        set(ifMgtServerSSL) {
            mEditor.putBoolean(ConstantUtils.MGT_SERVER_SSL, ifMgtServerSSL)
            mEditor.commit()
        }

    var securityModuleHost: String?
        get() = mPrefs.getString(ConstantUtils.SECURITY_MODULE_HOST, "")
        set(securityModuleHost) {
            mEditor.putString(ConstantUtils.SECURITY_MODULE_HOST, securityModuleHost)
            mEditor.commit()
        }

    var securityModulePort: Int
        get() = mPrefs.getInt(ConstantUtils.SECURITY_MODULE_PORT, 0)
        set(tmsPort) {
            mEditor.putInt(ConstantUtils.SECURITY_MODULE_PORT, tmsPort)
            mEditor.commit()
        }

    var securityModuleTimeout: Int
        get() = mPrefs.getInt(ConstantUtils.SECURITY_MODULE_TIMEOUT, 60)
        set(securityModuleTimeout) {
            mEditor.putInt(ConstantUtils.SECURITY_MODULE_TIMEOUT, securityModuleTimeout)
            mEditor.commit()
        }

    var ifSecurityModuleSSL: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.SECURITY_MODULE_SSL, false)
        set(ifsecurityModuleSSL) {
            mEditor.putBoolean(ConstantUtils.SECURITY_MODULE_SSL, ifsecurityModuleSSL)
            mEditor.commit()
        }

    var acquirerHost: String?
        get() = mPrefs.getString(ConstantUtils.ACQUIRER_HOST, "")
        set(acquirerHost) {
            mEditor.putString(ConstantUtils.ACQUIRER_HOST, acquirerHost)
            mEditor.commit()
        }

    var acquirerPort: Int
        get() = mPrefs.getInt(ConstantUtils.ACQUIRER_PORT, 0)
        set(acquirerPort) {
            mEditor.putInt(ConstantUtils.ACQUIRER_PORT, acquirerPort)
            mEditor.commit()
        }

    var acquirerTimeout: Int
        get() = mPrefs.getInt(ConstantUtils.ACQUIRER_TIMEOUT, 60)
        set(acquirerTimeout) {
            mEditor.putInt(ConstantUtils.ACQUIRER_TIMEOUT, acquirerTimeout)
            mEditor.commit()
        }

    var ifAcquirerSSL: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.ACQUIRER_SSL, false)
        set(ifAcquirerSSL) {
            mEditor.putBoolean(ConstantUtils.ACQUIRER_SSL, ifAcquirerSSL)
            mEditor.commit()
        }

    var iCCData: String?
        get() = mPrefs.getString(ConstantUtils.ICC_DATA, "")
        set(iccData) {
            mEditor.putString(ConstantUtils.ICC_DATA, iccData)
            mEditor.commit()
        }

    var keyDownloadPeriodInMin: Int
        get() = mPrefs.getInt(ConstantUtils.KEY_DOWNLOAD_TIME_IN_MINS, 1440)
        set(timeInMin) {
            mEditor.putInt(ConstantUtils.KEY_DOWNLOAD_TIME_IN_MINS, timeInMin)
            mEditor.commit()
        }

    var checkKeyDownloadIntervalInMin: Int
        get() = mPrefs.getInt(ConstantUtils.CHECK_KEY_DOWNLOAD_INTERVAL_IN_MINS, 120)
        set(timeInMin) {
            mEditor.putInt(ConstantUtils.CHECK_KEY_DOWNLOAD_INTERVAL_IN_MINS, timeInMin)
            mEditor.commit()
        }

    var callHomePeriodInMin: Int
        get() = mPrefs.getInt(ConstantUtils.CALL_HOME_TIME_IN_MINS, 60)
        set(timeInMin) {
            mEditor.putInt(ConstantUtils.CALL_HOME_TIME_IN_MINS, timeInMin)
            mEditor.commit()
        }

    var reversalRetryTimeInMin: Int
        get() = mPrefs.getInt(ConstantUtils.RESEND_REVERSAL_TIME_IN_MINS, 30)
        set(resendReversal) {
            mEditor.putInt(ConstantUtils.RESEND_REVERSAL_TIME_IN_MINS, resendReversal)
            mEditor.commit()
        }

    var tranNotifyRetentionInDays: Int
        get() = mPrefs.getInt(ConstantUtils.TRAN_NOTIFY_RETENTION_IN_DAYS, 3)
        set(tranNotifyRetentionInDays) {
            mEditor.putInt(ConstantUtils.TRAN_NOTIFY_RETENTION_IN_DAYS, tranNotifyRetentionInDays)
            mEditor.commit()
        }

    var reversalRetentionInDays: Int
        get() = mPrefs.getInt(ConstantUtils.REVERSAL_RETENTION_IN_DAYS, 3)
        set(resendReversal) {
            mEditor.putInt(ConstantUtils.REVERSAL_RETENTION_IN_DAYS, resendReversal)
            mEditor.commit()
        }

    var transactionNotificationRetryTimeInMin: Int
        get() = mPrefs.getInt(ConstantUtils.RESEND_TRANSACTION_NOTIFICATION_TIME_IN_MINS, 60)
        set(resendTransactionNotification) {
            mEditor.putInt(
                ConstantUtils.RESEND_TRANSACTION_NOTIFICATION_TIME_IN_MINS,
                resendTransactionNotification
            )
            mEditor.commit()
        }

    var batchCutOffTime: String?
        get() = mPrefs.getString(ConstantUtils.BATCH_CUT_OFF_TIME, "23:59:59.999")
        set(batchCutOffTime) {
            mEditor.putString(ConstantUtils.BATCH_CUT_OFF_TIME, batchCutOffTime)
            mEditor.commit()
        }

    var pageTimerInSec: Int
        get() = mPrefs.getInt(ConstantUtils.PAGE_TIMER_IN_SEC, 60)
        set(pageTimerInSec) {
            mEditor.putInt(ConstantUtils.PAGE_TIMER_IN_SEC, pageTimerInSec)
            mEditor.commit()
        }

    var terminalSerialNo: String?
        get() = if (mPrefs == null) null else mPrefs.getString(
            ConstantUtils.SERIAL_NO,
            Build.SERIAL
        )
        set(serialNo) {
            mEditor.putString(ConstantUtils.SERIAL_NO, serialNo)
            mEditor.commit()
        }

    var termType: Int
        get() = if (mPrefs == null) 22 else mPrefs.getInt(ConstantUtils.TERM_TYPE, 22)
        set(termType) {
            mEditor.putInt(ConstantUtils.TERM_TYPE, termType)
            mEditor.commit()
        }

    var termCapabilities: String?
        get() = if (mPrefs == null) null else mPrefs.getString(
            ConstantUtils.TERM_CAPABILITIES,
            "E0F0C8"
        )
        set(termCapabilities) {
            mEditor.putString(ConstantUtils.TERM_CAPABILITIES, termCapabilities)
            mEditor.commit()
        }

    var extraTermCapabilities: String?
        get() = if (mPrefs == null) null else mPrefs.getString(
            ConstantUtils.EXTRA_TERM_CAPABILITIES,
            ""
        )
        set(extraTermCapabilities) {
            mEditor.putString(ConstantUtils.EXTRA_TERM_CAPABILITIES, extraTermCapabilities)
            mEditor.commit()
        }

    var isForcedOnline: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.FORCE_ONLINE, true)
        set(forcedOnline) {
            mEditor.putBoolean(ConstantUtils.FORCE_ONLINE, forcedOnline)
            mEditor.commit()
        }

    var referenceCurrencyCode: String?
        get() = if (mPrefs == null) null else mPrefs.getString(
            ConstantUtils.REFERENCE_CURRENCY_CODE,
            "0566"
        )
        set(referenceCurrencyCode) {
            mEditor.putString(ConstantUtils.REFERENCE_CURRENCY_CODE, referenceCurrencyCode)
            mEditor.commit()
        }

    var transCurrencyExponent: String?
        get() = if (mPrefs == null) null else mPrefs.getString(
            ConstantUtils.TRANS_CURRENCY_EXPONENT,
            "2"
        )
        set(transCurrencyExponent) {
            mEditor.putString(ConstantUtils.TRANS_CURRENCY_EXPONENT, transCurrencyExponent)
            mEditor.commit()
        }

    var referenceCurrencyExponent: String?
        get() = if (mPrefs == null) null else mPrefs.getString(
            ConstantUtils.REFENCE_CURRENCY_EXPONENT,
            "02"
        )
        set(refernceCurrencyExponent) {
            mEditor.putString(ConstantUtils.REFENCE_CURRENCY_EXPONENT, refernceCurrencyExponent)
            mEditor.commit()
        }

    var referenceCurrencyConversion: String?
        get() = if (mPrefs == null) null else mPrefs.getString(
            ConstantUtils.REFENCE_CURRENCY_CONVERSION,
            "00"
        )
        set(referenceCurrencyConversion) {
            mEditor.putString(
                ConstantUtils.REFENCE_CURRENCY_CONVERSION,
                referenceCurrencyConversion
            )
            mEditor.commit()
        }

    var defaultTDOL: String?
        get() = mPrefs.getString(ConstantUtils.DEFAULT_TDOL, "")
        set(defaultTDOL) {
            mEditor.putString(ConstantUtils.DEFAULT_TDOL, defaultTDOL)
            mEditor.commit()
        }

    var defaultDDOL: String?
        get() = mPrefs.getString(ConstantUtils.DEFAULT_DDOL, "")
        set(defaultDDOL) {
            mEditor.putString(ConstantUtils.DEFAULT_DDOL, defaultDDOL)
            mEditor.commit()
        }

    var supportPSESelection: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.SUPPORT_PSE_SELECTION, true)
        set(supportPSESelection) {
            mEditor.putBoolean(ConstantUtils.SUPPORT_PSE_SELECTION, supportPSESelection)
            mEditor.commit()
        }

    var tranType: Int
        get() = mPrefs.getInt(ConstantUtils.TRAN_TYPE, ConstantUtils.GOODS_TRAN_TYPE)
        set(tranType) {
            mEditor.putInt(ConstantUtils.TRAN_TYPE, tranType)
            mEditor.commit()
        }

    var getDataPin: Int
        get() = mPrefs.getInt(ConstantUtils.GET_DATA_PIN, ConstantUtils.GET_PIN_DATA)
        set(getDataPin) {
            mEditor.putInt(ConstantUtils.GET_DATA_PIN, getDataPin)
            mEditor.commit()
        }

    var termTransQuality: String?
        get() = if (mPrefs == null) null else mPrefs.getString(
            ConstantUtils.TERM_TRAN_QUALITY,
            ConstantUtils.TERM_TRANSACTION_QUALITY
        )
        set(termTransQuali) {
            mEditor.putString(ConstantUtils.TERM_TRAN_QUALITY, termTransQuali)
            mEditor.commit()
        }

    var zmk: String?
        get() = mPrefs.getString(ConstantUtils.ZMK, "")
        set(zmk) {
            mEditor.putString(ConstantUtils.ZMK, zmk)
            mEditor.commit()
        }

    var tmk: String?
        get() = mPrefs.getString(ConstantUtils.TMK, "")
        set(tmk) {
            mEditor.putString(ConstantUtils.TMK, tmk)
            mEditor.commit()
        }

    var tsk: String?
        get() = mPrefs.getString(ConstantUtils.TSK, "")
        set(tsk) {
            mEditor.putString(ConstantUtils.TSK, tsk)
            mEditor.commit()
        }

    var tpk: String?
        get() = mPrefs.getString(ConstantUtils.TPK, "")
        set(tpk) {
            mEditor.putString(ConstantUtils.TPK, tpk)
            mEditor.commit()
        }

    var cleartpk: String?
        get() = mPrefs.getString(ConstantUtils.CLEAR_TPK, "")
        set(tpk) {
            mEditor.putString(ConstantUtils.CLEAR_TPK, tpk)
            mEditor.commit()
        }

    var supervisorPIN: String?
        get() = mPrefs.getString(ConstantUtils.SUPERVISOR_PIN, "1234")
        set(supervisorPIN) {
            mEditor.putString(ConstantUtils.SUPERVISOR_PIN, supervisorPIN)
            mEditor.commit()
        }

    var adminPassword: String?
        get() = mPrefs.getString(ConstantUtils.ADMIN_PWD, "Password1$")
        set(adminPassword) {
            mEditor.putString(ConstantUtils.ADMIN_PWD, adminPassword)
            mEditor.commit()
        }

    var ifFirstLaunch: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.FIRST_LAUNCH, true)
        set(ifFirstLaunch) {
            mEditor.putBoolean(ConstantUtils.FIRST_LAUNCH, ifFirstLaunch)
            mEditor.commit()
        }

    /*
    public void setIfUseRemoteNetworkConfig(boolean ifUseRemoteNetworkConfig) {
        mEditor.putBoolean(ConstantUtils.USE_REMOTE_NETWORK_CONFIG, ifUseRemoteNetworkConfig);
        mEditor.commit();
    }
    public boolean getIfUseRemoteNetworkConfig() {
        return mPrefs.getBoolean(ConstantUtils.USE_REMOTE_NETWORK_CONFIG, false);
    }
    */
    var ifManualTerminalSetup: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.IS_TERMINAL_MANUAL_SETUP, false)
        set(ifManualTerminalSetup) {
            mEditor.putBoolean(ConstantUtils.IS_TERMINAL_MANUAL_SETUP, ifManualTerminalSetup)
            mEditor.commit()
        }

    var isDemoMode: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.DEMO_MODE, false)
        set(demoMode) {
            mEditor.putBoolean(ConstantUtils.DEMO_MODE, demoMode)
            mEditor.commit()
        }

    var demoResponseCode: String?
        get() = mPrefs.getString(ConstantUtils.DEMO_RESPONSE_CODE, "")
        set(responseCode) {
            mEditor.putString(ConstantUtils.DEMO_RESPONSE_CODE, responseCode)
            mEditor.commit()
        }

    var pINPadBluetoothAddress: String?
        get() = mPrefs.getString(ConstantUtils.PINPAD_BLUETOOTH_ADDRESS, "")
        set(pinpadBluetoothAddress) {
            mEditor.putString(ConstantUtils.PINPAD_BLUETOOTH_ADDRESS, pinpadBluetoothAddress)
            mEditor.commit()
        }

    var pINPadType: String?
        get() = mPrefs.getString(ConstantUtils.PINPAD_TYPE, "")
        set(pinpadType) {
            mEditor.putString(ConstantUtils.PINPAD_TYPE, pinpadType)
            mEditor.commit()
        }

    var tmkExported: String?
        get() = mPrefs.getString(ConstantUtils.TMK_EXPORTED, "")
        set(tmk) {
            mEditor.putString(ConstantUtils.TMK_EXPORTED, tmk)
            mEditor.commit()
        }

    var aPIKEY: String
        get() = mPrefs.getString(ConstantUtils.API_KEY, "BuildConfig.API") ?: ""
        set(apiKey) {
            mEditor.putString(ConstantUtils.API_KEY, apiKey)
            mEditor.commit()
        }

    var terminalAdUrl: String?
        get() = mPrefs.getString(ConstantUtils.TERMINAL_AD_URL, "")
        set(terminalAdUrl) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.TERMINAL_AD_URL, terminalAdUrl)
            mEditor.apply()
        }

    var env: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.DEFAULT_ENV, true)
        set(epms) {
            mEditor = mPrefs.edit()
            mEditor.putBoolean(ConstantUtils.DEFAULT_ENV, epms)
            mEditor.apply()
        }

    var notificationCount: Int
        get() = mPrefs.getInt(ConstantUtils.NOTIFICATION_COUNT, 0)
        set(notificationCount) {
            mEditor = mPrefs.edit()
            mEditor.putInt(ConstantUtils.NOTIFICATION_COUNT, notificationCount)
            mEditor.apply()
        }

    var individualTransactionMaxAmt: Int
        get() = mPrefs.getInt(ConstantUtils.GET_INDIVIDUAL_TRANSACTION_MAX_AMOUNT, 0)
        set(individualTransactionMaxAmt) {
            mEditor = mPrefs.edit()
            mEditor.putInt(
                ConstantUtils.GET_INDIVIDUAL_TRANSACTION_MAX_AMOUNT,
                individualTransactionMaxAmt
            )
            mEditor.apply()
        }

    var hasCallBack: Boolean
        get() = mPrefs.getBoolean(ConstantUtils.HAS_CALL_BACK, true)
        set(hasCallBack) {
            mEditor = mPrefs.edit()
            mEditor.putBoolean(ConstantUtils.HAS_CALL_BACK, hasCallBack)
            mEditor.apply()
        }

    var merchantCallBack: String?
        get() = mPrefs.getString(ConstantUtils.CALL_BACK, "")
        set(callBack) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.CALL_BACK, callBack)
            mEditor.apply()
        }

    var clearTsk: String?
        get() = mPrefs.getString(ConstantUtils.CLEAR_TSK, "")
        set(clearTsk) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.CLEAR_TSK, clearTsk)
            mEditor.apply()
        }

    var transTotal: String?
        get() = mPrefs.getString(ConstantUtils.TRANS_TOTAL, "")
        set(transTotal) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.TRANS_TOTAL, transTotal)
            mEditor.apply()
        }

    var merchantKey: String?
        get() = mPrefs.getString(ConstantUtils.MERCHANT_KEY, "")
        set(merchantKey) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.MERCHANT_KEY, merchantKey)
            mEditor.apply()
        }

    var merchantMID: String?
        get() = mPrefs.getString(ConstantUtils.MERCHANT_MID, "")
        set(merchantMID) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.MERCHANT_MID, merchantMID)
            mEditor.apply()
        }

    var aid: String?
        get() = mPrefs.getString(ConstantUtils.AID, "")
        set(aid) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.AID, aid)
            mEditor.apply()
        }

    var tvr: String?
        get() = mPrefs.getString(ConstantUtils.TVR, "")
        set(tvr) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.TVR, tvr)
            mEditor.apply()
        }

    var unpredictableNumber: String?
        get() = mPrefs.getString(ConstantUtils.UNPRENUMBER, "")
        set(unpredictableNumber) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.UNPRENUMBER, unpredictableNumber)
            mEditor.apply()
        }

    var cardOwner: String?
        get() = mPrefs.getString(ConstantUtils.CARDOWNER, "")
        set(cardOwner) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.CARDOWNER, cardOwner)
            mEditor.apply()
        }

    var cardType: String?
        get() = mPrefs.getString(ConstantUtils.CARDTYPE, "")
        set(cardType) {
            mEditor = mPrefs.edit()
            mEditor.putString(ConstantUtils.CARDTYPE, cardType)
            mEditor.apply()
        }

//    fun getTermType(): Int {
//        return if (mPrefs == null) 22 else mPrefs.getInt(ConstantUtils.TERM_TYPE, 22)
//    }
//
//    fun getTermCapabilities(): String? {
//        return if (mPrefs == null) null else mPrefs.getString(ConstantUtils.TERM_CAPABILITIES,
//            "E0F0C8")
//    }

//    fun getExtraTermCapabilities(): String? {
//        return if (mPrefs == null) null else mPrefs.getString(ConstantUtils.EXTRA_TERM_CAPABILITIES,
//            "")
//    }
//
//    fun isForcedOnline(): Boolean {
//        return mPrefs.getBoolean(ConstantUtils.FORCE_ONLINE, true)
//    }
//
//    fun getReferenceCurrencyCode(): String? {
//        return if (mPrefs == null) null else mPrefs.getString(ConstantUtils.REFERENCE_CURRENCY_CODE,
//            "0566")
//    }
//
//    fun getTransCurrencyExponent(): String? {
//        return if (mPrefs == null) null else mPrefs.getString(ConstantUtils.TRANS_CURRENCY_EXPONENT,
//            "2")
//    }
//
//    fun getReferenceCurrencyExponent(): String? {
//        return if (mPrefs == null) null else mPrefs.getString(ConstantUtils.REFENCE_CURRENCY_EXPONENT,
//            "02")
//    }
//
//    fun getReferenceCurrencyConversion(): String? {
//        return if (mPrefs == null) null else mPrefs.getString(ConstantUtils.REFENCE_CURRENCY_CONVERSION,
//            "00")
//    }

    companion object {
        private const val TAG = "LocalData"
        private const val PREF_NAME = "PREF_GLOBAL_DATA"
        private const val PREF_CONGIG_DATA = "PREF_CONGIG_DATA"
        private const val PREF_USER_DATA = "PREF_USER_DATA"

    }

}