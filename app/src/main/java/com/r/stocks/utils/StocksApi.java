package com.r.stocks.utils;

import com.r.stocks.response.CompanyResponse;
import com.r.stocks.response.QuoteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StocksApi {
    @GET("tr/trending") //https://mboum.com/api/v1/tr/trending?apikey=demo
    Call<List<QuoteResponse>> getAllQuotes(
            @Query("apikey") String key
    );

    @GET("qu/quote/") //https://mboum.com/api/v1/qu/quote/?symbol=AAPL&apikey=demo
    Call<List<CompanyResponse>> getCompany(
            @Query("symbol") String tickers,
            @Query("apikey") String key
    );
}
