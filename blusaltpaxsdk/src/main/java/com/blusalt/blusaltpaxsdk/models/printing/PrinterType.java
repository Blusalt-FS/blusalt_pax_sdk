package com.blusalt.blusaltpaxsdk.models.printing;

import androidx.annotation.Keep;

@Keep
public enum PrinterType {
    PosTransaction(10) , BankTransfer(20), CashRecord(30), TransDetail(40),Ussd(50), ResAccount(60),;

    private  int printerType;
    PrinterType(int printerType) {
        this.printerType = printerType;
    }

    public int getPrinterType() {
        if (printerType == 20) {
            return 20;
        }
        return 10;
    }

    @Override
    public String toString() {
        switch (getPrinterType()){
            case 20:
                return "Bank Transfer";
            case 30:
                return "Cash Record";
            case 40:
                return "Trans Detail";
            case 50:
                return "Ussd";
            case 60:
                return "ResAccount";
            default:
                return "Pos Transaction";
        }
    }
}
