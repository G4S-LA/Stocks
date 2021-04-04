package com.r.stocks.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ImageApi {
    @GET("logo") //https://finnhub.io/api/logo?symbol=FPI
    Call<ResponseBody> getImage(
            @Query("symbol") String ticker
    );
}
