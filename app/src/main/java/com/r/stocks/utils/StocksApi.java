package com.r.stocks.utils;

import com.r.stocks.response.CompanyResponse;
import com.r.stocks.response.PriceResponse;
import com.r.stocks.response.TickerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StocksApi {
    @GET("v1/stock/symbol") //https://finnhub.io/api/v1/stock/symbol?exchange=US&mic=XNYS&token=c1bgbhf48v6rcdqa0gag
    Call<List<TickerResponse>> getAllTickers(
            @Query("exchange") String country,
            @Query("mic") String index,
            @Query("token") String key
    );

    @GET("v1/stock/profile2") //https://finnhub.io/api/v1/stock/profile2?symbol=AAPL&token=c1bgbhf48v6rcdqa0gag
    Call<CompanyResponse> getCompany(
            @Query("symbol") String ticker,
            @Query("token") String key
    );

    @GET("v1/quote") //https://finnhub.io/api/v1/quote?symbol=AAPL&token=c1bgbhf48v6rcdqa0gag
    Call<PriceResponse> getPrice(
            @Query("symbol") String ticker,
            @Query("token") String key
    );
}
