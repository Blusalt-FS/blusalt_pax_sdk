package com.blusalt.blusaltpaxsdk;

import android.content.Context;

import androidx.annotation.Keep;

@Keep
public class Pos {
    private static final String TAG = "Pos";
    private static Pos INSTANCE;

    public static Context  mContext;

    public static Pos getINSTANCE(){
        return INSTANCE;
    }

    public void init(Context context) {
        INSTANCE = this;
        Pos.mContext = context;
//        BaseUtils.init(context);
        // AppLog.debug(true);
    }


}

