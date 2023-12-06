package com.blusalt.blusaltpaxsdk;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Keep;

import com.blusalt.blusaltpaxsdk.pax.AppActLifecycleCallback;
import com.blusalt.blusaltpaxsdk.pax.manager.ParamManager;
import com.blusalt.blusaltpaxsdk.pax.util.ThreadPoolManager;
import com.pax.dal.IDAL;
import com.paxsz.module.emv.process.EmvBase;
import com.paxsz.module.pos.Sdk;

import java.util.concurrent.ExecutorService;


@Keep
public class MyApplication extends Application {

    //public class EmvDemoApp  {
    private static ParamManager mParamManager;

    private Handler handler;
    private ExecutorService backgroundExecutor;
    private IDAL dal;

    public static String ACTION_SERVICE_CONNECTED = "DEVICEMANAGER_SERVICE_CONNECTED";
    public static String ACTION_SERVICE_DISCONNECTED = "DEVICEMANAGER_SERVICE_DISCONNECTED";

    private boolean isConnect = false;

    public static int printIndex = 0;
    public Context context;

    private static final String TAG = "MyApplication";
    private static MyApplication INSTANCE;

    public static MyApplication getINSTANCE(){
        return INSTANCE;
    }

    public static ParamManager getParamManager() {
        return mParamManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO Auto-generated method stub
        INSTANCE = this;
        context = this;


        handler = new Handler();
        registerActivityLifecycleCallbacks(new AppActLifecycleCallback());
        backgroundExecutor = ThreadPoolManager.getInstance().getExecutor();
        initSdkModule();
        initEmvModule();

    }

    public void initSdkModule() {
        runInBackground(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, " initSdkModule start");
                long startT = System.currentTimeMillis();
                getDalInstance();
                long endT = System.currentTimeMillis();
                Log.d(TAG, "initSdkModule  end:" + (endT - startT));
            }
        });

    }

    private void getDalInstance() {
        dal = Sdk.getInstance(INSTANCE).getDal();
    }


    public void initEmvModule() {
        runInBackground(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, " initEmvModule start");
                long startT = System.currentTimeMillis();
                mParamManager = ParamManager.getInstance(INSTANCE);
                long endT = System.currentTimeMillis();
                Log.d(TAG, "initEmvModule  end:" + (endT - startT));
                EmvBase.loadLibrary();
            }
        });

    }

    public void runInBackground(final Runnable runnable) {
        backgroundExecutor.execute(runnable);
    }

    public void runOnUiThread(final Runnable runnable) {
        handler.post(runnable);
    }


    public IDAL getDal() {
        if (dal == null) {
            getDalInstance();
        }
        return dal;
    }

    public boolean isDeviceManagerConnetcted() {
        return isConnect;
    }

}
