package com.blusalt.blusaltpaxsdk.processor.util


import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.DownloadReasonCode
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.DownloadStatus

class KeyException : Exception {

    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}

    private var downloadStatus: DownloadStatus? = null
    private var downloadReasonCode: DownloadReasonCode? = null
    private var errorCode: String? = null
    private var errorDescription: String? = null

    constructor(
        downloadStatus: DownloadStatus,
        downloadReasonCode: DownloadReasonCode,
        errorCode: String,
        errorDescription: String,
    ) : super(errorDescription) {
        this.downloadStatus = downloadStatus
        this.downloadReasonCode = downloadReasonCode
        this.errorCode = errorCode
        this.errorDescription = errorDescription
    }

    constructor(
        downloadStatus: DownloadStatus,
        downloadReasonCode: DownloadReasonCode,
        errorCode: String,
        errorDescription: String,
        throwable: Throwable?,
    ) : super(errorDescription, throwable) {
        this.downloadStatus = downloadStatus
        this.downloadReasonCode = downloadReasonCode
        this.errorCode = errorCode
        this.errorDescription = errorDescription
    }

    fun getDownloadStatus(): DownloadStatus? {
        return downloadStatus
    }

    fun getDownloadReasonCode(): DownloadReasonCode? {
        return downloadReasonCode
    }

    fun getErrorCode(): String? {
        return errorCode
    }

    fun getErrorDescription(): String? {
        return errorDescription
    }


}