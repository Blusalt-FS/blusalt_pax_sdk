package com.blusalt.blusaltpaxsdk.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.blusalt.blusaltpaxsdk.MyApplication;

/**
 * Created by AYODEJI on 10/10/2020.
 *
 */
public class MemoryManager {

    private static MemoryManager sInstance;
    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String PREF_NAME = "terminal_sdk_app";
    private static final int PREF_MODE = 0;
    private static final String KEY_USER1 = "user1";

    private static final String KEY_IS_LIVE_TOKEN = "is_live";

    private static final String KEY_KEEP_STATE = "keep_state";





    private MemoryManager() {
        mSharedPreferences = MyApplication.getINSTANCE().getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized MemoryManager getInstance() {
        if (sInstance == null) sInstance = new MemoryManager();
        return sInstance;
    }


    public String getSecretKey() {
        if (mSharedPreferences.getString(KEY_USER1, null) != null) {
            String secretKey = mSharedPreferences.getString(KEY_USER1, null);
            return secretKey;
        }
        return null;
    }

    public void putUserSecretKey(String secretKey) {
        editor.putString(KEY_USER1, secretKey);
        editor.commit();
    }


    public boolean isSecretActivated(){ return mSharedPreferences.contains(KEY_USER1);}


    public void putIsLive(boolean value) {
        editor.putBoolean(KEY_IS_LIVE_TOKEN, value);
        editor.commit();
    }

    public boolean getIsLive(){ return mSharedPreferences.getBoolean(KEY_IS_LIVE_TOKEN,false); }

    public boolean isActivated(){ return mSharedPreferences.contains(KEY_IS_LIVE_TOKEN);}


    public void keepPickState(String value){
        editor.putString(KEY_KEEP_STATE,value);
        editor.commit();
    }

    public String getKeepedState(){
        return  mSharedPreferences.getString(KEY_KEEP_STATE, null);
    }


}
