package com.blusalt.blusaltpaxsdk.models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class TerminalResponse implements Serializable {
    public  boolean status;

    public  String message;
    public  String responseCode;
    public  String responseDescription;
    public  String supportPhoneNumber;
    public  boolean isMerchantCopy;
    public CardResponse data;
    public TerminalResponse(){}

    public TerminalResponse(String message, String code, String des){
        this.message = message; this.responseCode = code; this.responseDescription = des;
    }

    public static TerminalResponse getTerminalResponse(){
        TerminalResponse response = new TerminalResponse();
        response.responseDescription = "APPROVED";
        response.responseCode = "00";
        response.status = true;
        response.message = "Card Payment Successful";
        CardResponse cardResponse = new CardResponse();
        cardResponse.cardReference = "1234356789";
        cardResponse.cardScheme = "Master Card";
        CardReceiptInfo printerModel = new CardReceiptInfo();
        printerModel.merchantName = "CHICKEN REPUBLIC SPG JAKANDE LEKKI";
        printerModel.merchantAddress = "Sea Petroleum & Gas Filling Station SPG Jakande Lekki";
        printerModel.unpredictableNumber ="50CDB7DC";
        printerModel.merchantTID = "201182IY";
        printerModel.transactionDate = "22/06/29";
        printerModel.transactionTime = "15:23:24";
        printerModel.transactionAID = "A0000000031010";
        printerModel.customerCardPan = "62179380*****7654";
        printerModel.customerCardExpiry = "2210";
        printerModel.customerCardName = "Suru Avoseh";
        printerModel.transactionSTAN = "12454";
        printerModel.transactionAuthID  = "558594";
        printerModel.transactionAccountType = "Savings";
        printerModel.transactionAmount = "1.800.00";
        printerModel.transactionTVR = "0080008000";
        printerModel.appVersionNameNumber = "Tamslite 9.9.13";
        printerModel.appPoweredBy = "POWERED BY Blusalt";
        printerModel.appOrgUrl = "www.blusalt.com";
        printerModel.appOrgPhoneContact = "0700-2255-4839";
        cardResponse.receiptInfo = printerModel;
        response.data = cardResponse;
        return  response;
    }
}

