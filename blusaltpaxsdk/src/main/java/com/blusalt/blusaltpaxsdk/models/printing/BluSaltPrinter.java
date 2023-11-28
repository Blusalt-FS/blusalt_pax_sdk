package com.blusalt.blusaltpaxsdk.models.printing;

import androidx.annotation.Keep;

import com.blusalt.blusaltpaxsdk.models.TerminalResponse;
import com.blusalt.blusaltpaxsdk.models.DesirailizeGeneric;

import java.io.Serializable;

@Keep
public class BluSaltPrinter implements Serializable {
   public DesirailizeGeneric transDetail; // Transaction Detail
   public MerchantDetails merchantDetails;
   public  PrinterType printerType;
   public  BankTransfer bankTransfer; // Bank transfer & Ussd
   public TerminalResponse posResponse; // Pos transaction

   public ResAccTransactionData resAccTransactionData; // Reserve Account
   public  CashRecord cashRecord; // Card Record
   public  String transactionDate; // Card Record
   public  String transactionSource; // Card Record
   public  String supportPhoneNumber;
   public  boolean isMerchantCopy;

   public  BluSaltPrinter(){}

}
