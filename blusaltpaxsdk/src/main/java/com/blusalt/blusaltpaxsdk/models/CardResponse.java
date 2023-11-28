package com.blusalt.blusaltpaxsdk.models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class CardResponse implements Serializable {
    public  String  cardReference;
    public String cardScheme;

    public String message;

    public  String posResponseCode;

    public CardReceiptInfo receiptInfo;
    public CardResponse(){}

}
