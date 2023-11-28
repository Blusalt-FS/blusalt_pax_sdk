package com.blusalt.blusaltpaxsdk.device;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blusalt.blusaltpaxsdk.MyApplication;


@Keep
public class BaseActivity extends Activity {
    public MyApplication mainApplication;
    private LocalBroadcastManager mLocalBroadcastManager;
    private ServiceEventBroadcastReceiver mBroadcastReceiver;

    private final String LOG_TAG = BaseActivity.class.getSimpleName();

    protected void showResult(final TextView textView, final String text) {
        Log.d(LOG_TAG,text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainApplication = (MyApplication) getApplication();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mBroadcastReceiver = new ServiceEventBroadcastReceiver();
    }

    public void onServiceConnected() {

    }

    public void onServiceDisconnected() {

    }

    private class ServiceEventBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MyApplication.ACTION_SERVICE_CONNECTED)) {
                BaseActivity.this.onServiceConnected();
            } else if (action
                    .equals(MyApplication.ACTION_SERVICE_DISCONNECTED)) {
                BaseActivity.this.onServiceDisconnected();
            }
        }
    }

    @Override
    protected void onResume() {
        IntentFilter ifIntentFilter = new IntentFilter();
        ifIntentFilter.addAction(MyApplication.ACTION_SERVICE_CONNECTED);
        ifIntentFilter.addAction(MyApplication.ACTION_SERVICE_DISCONNECTED);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,
                ifIntentFilter);
        super.onResume();
    }

    @Override
    protected void onStop() {
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }
}
