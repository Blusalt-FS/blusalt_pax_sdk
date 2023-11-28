package com.blusalt.blusaltpaxsdk.models.printing;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class CashRecord implements Serializable {
    public  int id;
    public  int agentId;
    public  String customerName;
    public  String customerEmail;
    public  String reference;
    public int amount;
    public  String currency;
    public  String narration;
    public  String description;
    public  String created_at;
    public CashRecord(){}
}
