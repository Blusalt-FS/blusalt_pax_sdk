package com.blusalt.blusaltpaxsdk.models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class CardReceiptInfo implements Serializable {
    public  String merchantName;
    public  String merchantAddress;
    public  String unpredictableNumber;
    public  String merchantTID;
    public  String transactionDate;
    public  String transactionTime;
    public  String transactionAID;
    public  String customerCardPan;
    public  String customerCardExpiry;
    public  String customerCardName;
    public  String transactionSTAN;
    public  String transactionAuthID;
    public  String transactionAccountType;
    public  String transactionAmount;
    public  String transactionTVR;
    public  String appVersionNameNumber;
    public  String appPoweredBy;
    public  String appOrgUrl;
    public  String appOrgPhoneContact;

    public String rrn;
    public  String reference;
    public CardReceiptInfo(){}
}
