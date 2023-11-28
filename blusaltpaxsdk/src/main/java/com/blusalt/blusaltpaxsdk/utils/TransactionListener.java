package com.blusalt.blusaltpaxsdk.utils;

import androidx.annotation.Keep;

import com.blusalt.blusaltpaxsdk.models.TerminalInfo;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.TerminalInfoProcessor;

/**
 * Created by AYODEJI on 05/19/2022.
 */
@Keep
public interface TransactionListener {
    public void onProcessingError(RuntimeException message, int errorcode);
    public void onCompleteTransaction(TerminalInfoProcessor response);
}
