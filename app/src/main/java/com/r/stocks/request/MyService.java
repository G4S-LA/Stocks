package com.r.stocks.request;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.r.stocks.MyApplication;
import com.r.stocks.utils.ImageApi;
import com.r.stocks.utils.StocksApi;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyService {
    public static final String BASE_URL = "https://mboum.com/api/v1/";
    public static final String BASE_IMAGE_URL = "https://logo.clearbit.com/"; // Так как в API акций нет картинок, некоторые позиции могут быть без логотипов
    public static final String API_KEY = "SOW87wEeiv6gCHJnL9yPdQ3yFxEk2st2QQxJvzSkocJE32yOFaUfljmJz7Dh";


    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";
    private static final String TAG = "Network";

    private static final long cacheSize = 5 * 1024 * 1024; // 5 MB

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit.Builder retrofitImageBuilder =
            new Retrofit.Builder()
                    .baseUrl(BASE_IMAGE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static Retrofit retrofitImage = retrofitImageBuilder.build();

    private static StocksApi stocksApi = retrofit.create(StocksApi.class);

    private static ImageApi imageApi = retrofitImage.create(ImageApi.class);

    public static StocksApi getStocksApi() {
        return stocksApi;
    }

    public static ImageApi getImageApi() {
        return imageApi;
    }

    private static OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .cache(cache())
                .addNetworkInterceptor(networkInterceptor())
                .addInterceptor(offlineInterceptor())
                .build();
    }

    private static Cache cache() {
        return new Cache(new File(MyApplication.getInstance().getCacheDir(), "Stocks"), cacheSize);
    }

    private static Interceptor networkInterceptor()     {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.v(TAG,"Network interceptor: called");

                Response response = chain.proceed(chain.request());

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(5, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    private static Interceptor offlineInterceptor() {
        return new Interceptor() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.v(TAG, "Offline interceptor called.");
                Request request = chain.request();

                if(!MyApplication.hasNetwork()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(6,TimeUnit.HOURS)
                            .build();
                    request = request.newBuilder()
                            .removeHeader(HEADER_PRAGMA)
                            .removeHeader(HEADER_CACHE_CONTROL)
                            .cacheControl(cacheControl)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }

}
