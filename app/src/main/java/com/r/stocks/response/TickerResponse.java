package com.r.stocks.response;

import com.google.gson.annotations.SerializedName;

public class TickerResponse {
    @SerializedName("symbol")
    private String ticker;

    public String getTicker() {
        return ticker;
    }

    @Override
    public String toString() {
        return "TickerResponse{" +
                "ticker = '" + ticker + '\'' +
                '}';
    }
}
