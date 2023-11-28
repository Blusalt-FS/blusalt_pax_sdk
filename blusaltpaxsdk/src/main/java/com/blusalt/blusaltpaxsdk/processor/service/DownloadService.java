package com.blusalt.blusaltpaxsdk.processor.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.blusalt.blusaltpaxsdk.network.BaseData;
import com.blusalt.blusaltpaxsdk.network.RetrofitClientInstanceParam;
import com.blusalt.blusaltpaxsdk.network.RetrofitClientInstanceProcessor;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.KeyDownloadRequest;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.KeyDownloadResponse;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.param.ModelError;
import com.blusalt.blusaltpaxsdk.processor.processor_blusalt.param.ParamDownloadResponse;
import com.blusalt.blusaltpaxsdk.processor.util.TripleDES;
import com.blusalt.blusaltpaxsdk.utils.AppPreferenceHelper;
import com.blusalt.blusaltpaxsdk.utils.Constants;
import com.blusalt.blusaltpaxsdk.utils.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.blusalt.blusaltpaxsdk.processor.LocalData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadService {

    public SecurityKeyManager securityKeyManager;
    public LocalData localData;

    public AppPreferenceHelper appPreferenceHelper;


    public DownloadService(SecurityKeyManager securityKeyManager, LocalData localData, AppPreferenceHelper appPreferenceHelper) {
        this.appPreferenceHelper = appPreferenceHelper;
        this.securityKeyManager = securityKeyManager;
        this.localData =  localData;
    }

    public void ProcessKeyDownload(KeyDownloadRequest keyDownloadRequest, TerminalKeyParamDownloadListener listener) {
        Log.e("ProcessKeyDownload", "ProcessKeyDownload");
        RetrofitClientInstanceProcessor.getInstance().getDataService().downloadKeyExchangeFromProcessor(keyDownloadRequest).enqueue(new Callback<BaseData<KeyDownloadResponse>>() {
            @Override
            public void onResponse(@NonNull Call<BaseData<KeyDownloadResponse>> call, @NonNull Response<BaseData<KeyDownloadResponse>> response) {
                Log.e("ProcessKeyDownload", "response: " + response);
                KeyDownloadResponse keyDownloadResponse = new KeyDownloadResponse();

                if (response.isSuccessful()) {

                    if (response.body().getMessage().contains("Access denied! invalid apiKey passed")) {

                    } else {
                        keyDownloadResponse = Objects.requireNonNull(response.body()).getData();

                        Log.e("ProcessKeyDownload", new Gson().toJson(response.body().getData()));
                        Log.e("ProcessKeyDownload", "isSuccessful" + new Gson().toJson(keyDownloadResponse));

                        String tmk = TripleDES.threeDesEncrypt("10101010101010101010101010101010", "01010101010101010101010101010101", keyDownloadResponse.masterKey);
                        Log.e("tmk", tmk.toString());
                        Log.e("ClearTmk", keyDownloadResponse.masterKey);
//                        int retTMK = securityKeyManager.saveTMK(keyDownloadResponse.masterKey, clearTMK);
                        int retTMK = securityKeyManager.saveTMK(tmk, keyDownloadResponse.masterKey);

//                        String clearTPK = TripleDES.threeDesDecrypt(keyDownloadResponse.pinKey, clearTMK);
                        String clearTPK = TripleDES.threeDesDecrypt(keyDownloadResponse.pinKey, keyDownloadResponse.masterKey);
                        Log.e("clearTPK", clearTPK.toString());
                        int retTPK = securityKeyManager.saveTPK(keyDownloadResponse.pinKey);

                        String clearTSK = TripleDES.threeDesDecrypt(keyDownloadResponse.sessionKey, keyDownloadResponse.masterKey);
                        Log.e("clearTSK", clearTSK.toString());
//                        int retTSK = securityKeyManager.saveTSK(clearTSK);
                        int retTSK = securityKeyManager.saveTSK(keyDownloadResponse.sessionKey);

//                        String clearTMK = TripleDES.threeDesDecrypt(keyDownloadResponse.masterKey, "11111111111111111111111111111111");
//                        Log.e("ClearTmk", clearTMK.toString());
//                        int retTMK = securityKeyManager.saveTMK("75EEF0E4ECD345089A9E22CA41EFC735", "5451BC0B64F146435BBF320ED579C4AE");
//
//                        String clearTPK = TripleDES.threeDesDecrypt(keyDownloadResponse.pinKey, clearTMK);
//                        Log.e("clearTPK", clearTPK.toString());
//                        int retTPK = securityKeyManager.saveTPK("3773E02C70A7B6C20BCDC7F1FDCECB57");
//
//                        String clearTSK = TripleDES.threeDesDecrypt(keyDownloadResponse.sessionKey, clearTMK);
//                        Log.e("clearTSK", clearTSK.toString());
//                        int retTSK = securityKeyManager.saveTSK("6754260E20D9A28AF2C14983988389AB");

                        if (retTMK == 0){
                            Log.e("Injection", "TMK injected Successful");
                        }else {
                            Log.e("Injection", "TMK injected failed" + retTMK);
                        }

                        if (retTPK == 0){
                            Log.e("Injection", "TPK injected Successful");
                        }else {
                            Log.e("Injection", "TPK injected failed" + retTPK);
                        }

                        if (retTSK == 0){
                            Log.e("Injection", "TSK injected Successful");
                        }else {
                            Log.e("Injection", "TSK injected failed" + retTSK);
                        }

                        if (retTMK == 0 && retTSK == 0 && retTPK == 0){
                            Log.e("Injection", "All keys injected Successful");
                            listener.onSuccess("Terminal Configuration Successful");
                            SharedPreferencesUtils.getInstance().setValue(Constants.INTENT_KEY_CONFIG, true);
                        }else {
                            listener.onFailed("Please restart the terminal");
                        }

                        localData.setTerminalId(keyDownloadResponse.terminalId);
                        localData.setMerchantId(keyDownloadResponse.downloadParameter.merchantId);
                        localData.setTsk(keyDownloadResponse.sessionKey);
                        localData.setMerchantLoc(keyDownloadResponse.downloadParameter.merchantNameAndLocation);
                        localData.setMerchantCategoryCode(keyDownloadResponse.downloadParameter.merchantCategoryCode);
                    }

                } else {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<KeyDownloadResponse>() {
                        }.getType();

                        listener.onFailed("Terminal Configuration Failed");
                        SharedPreferencesUtils.getInstance().setValue(Constants.INTENT_KEY_CONFIG, false);

                        keyDownloadResponse = gson.fromJson(response.errorBody().charStream(), type);
                        Log.e("ProcessTransaction", "ProcessTransaction failed" + new Gson().toJson(keyDownloadResponse));


                    } catch (Exception e) {
                        Log.e("ProcessKeyDownload", "error" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseData<KeyDownloadResponse>> call, @NonNull Throwable t) {
                SharedPreferencesUtils.getInstance().setValue(Constants.INTENT_KEY_CONFIG, false);
                listener.onFailed("Terminal Configuration Failed");

                KeyDownloadResponse keyDownloadResponse = new KeyDownloadResponse();
                Log.e("ProcessKeyDownload", "onFailure" + t.getMessage());
                Log.e("ProcessKeyDownload", "onFailure" + keyDownloadResponse);
            }
        });
    }

    public void downloadTerminalParam(String serialNumber, TerminalKeyParamDownloadListener listener) {
        Log.e("ProcessKeyDownload", "ProcessKeyDownload");
        RetrofitClientInstanceParam.getInstance().getDataService().downloadTerminalParam(serialNumber).enqueue(new Callback<BaseData<ParamDownloadResponse>>() {
            @Override
            public void onResponse(@NonNull Call<BaseData<ParamDownloadResponse>> call, @NonNull Response<BaseData<ParamDownloadResponse>> response) {
                Log.e("ProcessKeyDownload", "response: " + response);
                ParamDownloadResponse paramDownloadResponse = new ParamDownloadResponse();

                if (response.isSuccessful()) {

                    try {
                        paramDownloadResponse = Objects.requireNonNull(response.body()).getData();

                        Log.e("ProcessKeyDownload", new Gson().toJson(response.body().getData()));
                        Log.e("ProcessKeyDownload", "isSuccessful" + response.code() + response.message());

                        KeyDownloadRequest keyDownloadRequest = new KeyDownloadRequest();
                        keyDownloadRequest.terminalId = paramDownloadResponse.terminalId.toString();
                        ProcessKeyDownload(keyDownloadRequest, listener);
                        SharedPreferencesUtils.getInstance().setValue(Constants.INTENT_TERMINAL_CONFIG, true);

                        listener.onSuccess("Terminal Parameter Downloaded");
//                            localData.setMerchantId(keyDownloadResponse.downloadParameter.merchantId);
//                            localData.setTsk(keyDownloadResponse.sessionKey);
//                            localData.setMerchantLoc(keyDownloadResponse.downloadParameter.merchantNameAndLocation);
//                            localData.setMerchantCategoryCode(keyDownloadResponse.downloadParameter.merchantCategoryCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("TAG", "TerminalParameterFailed " + response.code());
                    BufferedReader reader = null;
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(response.errorBody().byteStream()));
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String finallyError = sb.toString();
                    Log.e("TAG", "TerminalParameterFailed " + finallyError);

                    ModelError modelError = new Gson().fromJson(finallyError, ModelError.class);
                    Log.e("TAG", "TerminalParameterFailed: " + new Gson().toJson(modelError));
                    Log.e("TAG", "TerminalParameterFailed: " + modelError.getMessage());

                    SharedPreferencesUtils.getInstance().setValue(Constants.INTENT_TERMINAL_CONFIG, false);
                    listener.onFailed("Terminal Parameter Failed: " + modelError.getMessage());

//                    try {
//                        Gson gson = new Gson();
//
//                        ModelError modelError = new ModelError();
//                        Type type = new TypeToken<ModelError>() {
//                        }.getType();
//
//                        modelError = gson.fromJson(finallyError, type);
//                        Log.e("ProcessKeyDownload", "ProcessKeyDownload failed" + modelError.getMessage());
//
//                    } catch (Exception e) {
//                        Log.e("ProcessKeyDownload", "error" + e.getMessage());
//                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseData<ParamDownloadResponse>> call, @NonNull Throwable t) {
                SharedPreferencesUtils.getInstance().setValue(Constants.INTENT_TERMINAL_CONFIG, false);
                listener.onFailed("Terminal Parameter Failed: " + t.getMessage());

                KeyDownloadResponse keyDownloadResponse = new KeyDownloadResponse();
                Log.e("ProcessKeyDownload", "onFailure" + t.getMessage());
                Log.e("ProcessKeyDownload", "onFailure" + keyDownloadResponse);
            }
        });
    }


}
