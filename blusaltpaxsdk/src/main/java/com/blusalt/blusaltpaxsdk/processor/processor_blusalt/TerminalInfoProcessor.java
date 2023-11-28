package com.blusalt.blusaltpaxsdk.processor.processor_blusalt;
import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class TerminalInfoProcessor implements Serializable {

    public String AmountAuthorized;
    public String currencyCode;
    public String currency;
    public String processingCode;
    public String rrn;
    public String sessionKey;
    public String stan;
    public String TransactionDate;
    public String transactionDateTime;
    public String transactionTime;
    public String AmountOther;
    public String de62;
    public String de63;
    public String cardOwner;
    public String cardSequenceNumber;
    public String expiryDate;
    public String pan;
    public String serviceCode;
    public String track2;
    public String iccData;
    public String pinData;
    public String merchantCategoryCode;
    public String terminalMerchantID;
    public String merchantNameAndLocation;
    public String posConditionCode;
    public String posEntryMode;
    public String terminalId;

    public String responseCode;
    public String responseDescription;

}
