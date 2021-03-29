package com.r.stocks.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImageApi {
    @GET("{company}.com")
    Call<ResponseBody> getImage(@Path("company") String company);
}
