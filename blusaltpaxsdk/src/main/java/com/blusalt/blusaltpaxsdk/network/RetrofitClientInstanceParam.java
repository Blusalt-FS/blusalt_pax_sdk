package com.blusalt.blusaltpaxsdk.network;

//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.blusalt.blusaltpaxsdk.utils.APIConstant.BASE_LIVE_URL_OMNI_TMS;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by OLUWASEYI on 10/10/2020.
 *
 */
public class RetrofitClientInstanceParam {
    private static RetrofitClientInstanceParam mInstance;
    private Retrofit mRetrofit;


    private RetrofitClientInstanceParam() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        String authToken = Credentials.basic("admin", "$$admin$$");

        authorization = MemoryManager.getInstance().getSecretKey() != null ? MemoryManager.getInstance().getSecretKey(): "";
        Log.d("AAA",authorization);
        String authValue = authorization;
//        String newAuthValue = "642f857717144ccfe4a3a5f4md76xz4ntl652pmnn2g529hwf4xhbv279cx6xk2x6bhwqd7k5dqgwg";
        Interceptor authInterceptor = chain -> {
            Request newRequest = chain.request().newBuilder()
//                    .addHeader("x-api-key", newAuthValue)
                    .addHeader("Authorization", authToken)
                    .addHeader("content-type", "application/json")
                    .build();
            return chain.proceed(newRequest);
        };

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.readTimeout(3, TimeUnit.MINUTES);
        okHttpBuilder.connectTimeout(3, TimeUnit.MINUTES);
        okHttpBuilder.addInterceptor(authInterceptor);
        okHttpBuilder.addInterceptor(interceptor);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_LIVE_URL_OMNI_TMS)
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClientInstanceParam getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitClientInstanceParam();
        }
        return mInstance;
    }

    public GetDataService getDataService() {
        return mRetrofit.create(GetDataService.class);
    }

    private String authorization;

    public void reset() {
        mInstance = null;
        getInstance();
    }
}
