package com.r.stocks.utils;

import com.r.stocks.response.NewsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("v1/company-news") //https://finnhub.io/api/v1/company-news?symbol=AAPL&from=2021-03-01&to=2021-03-09&token=c1bgbhf48v6rcdqa0gag
    Call<List<NewsResponse>> searchNews(
            @Query("symbol") String ticker,
            @Query("from") String from,
            @Query("to") String to,
            @Query("token") String key
    );
}
