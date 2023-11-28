package com.blusalt.blusaltpaxsdk.utils;

import android.content.Context;

/**
 * Created by zq on 2016/6/7.
 */
public class Shower {
    private static Context context ;
    public Shower(Context c){
        this.context = c ;
    }

    public String getString(int rid){
        return  Shower.this.context.getResources().getString(rid);
    }
}
