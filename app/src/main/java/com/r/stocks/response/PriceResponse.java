package com.r.stocks.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PriceResponse {
    @SerializedName("c")
    @Expose
    private double currentPrice;

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    @SerializedName("pc")
    @Expose
    private double previousPrice;
}
