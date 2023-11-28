package com.blusalt.blusaltpaxsdk.utils;

import androidx.annotation.Keep;

import com.blusalt.blusaltpaxsdk.models.TerminalInfo;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.TerminalInfoProcessor;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by AYODEJI on 05/19/2022.
 */
@Keep
public class StringUtils {
    public static String getNairaUnitFormat(String amount) {
        try {
            String amounts = amount + "D";
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CANADA);
            String currency = format.format(Double.parseDouble(amounts));
            return currency.replace("$", "");
        }catch (Exception e){
            return amount;
        }
    }

    public static TerminalInfoProcessor getTransactionTesponse(String message, int code) {
        TerminalInfoProcessor response = new TerminalInfoProcessor();
        response.responseCode = String.valueOf(code);
        response.responseDescription = message;
        return  response;
    }

    public static String maskValue(String pan) {
        StringBuilder builder = new StringBuilder();
        builder.append("**********").append(pan.substring(pan.length() - 2));
      //  builder.append(pan.substring(0, 8)).append("**********");
        return builder.toString();
    }

}
