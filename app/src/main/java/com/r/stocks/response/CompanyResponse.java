package com.r.stocks.response;

/*
 * "shortName": "Apple Inc." // название компании
 * "currency": "USD", // валюта
 * "regularMarketPrice": 123.39 // цена на сегодня
 * "regularMarketChange": 3.4000015 // изменение в валюте
 * "regularMarketChangePercent": 2.8335707 //изменения в процентах
 *
 * */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.r.stocks.models.CompanyModel;

public class CompanyResponse {

    @SerializedName("symbol")
    @Expose
    private String ticker;

    @SerializedName("shortName")
    @Expose
    private String name;

    @SerializedName("currency")
    @Expose
    private String cur;

    @SerializedName("regularMarketPrice")
    @Expose
    private double price;

    @SerializedName("regularMarketChange")
    @Expose
    private double change;

    @SerializedName("regularMarketChangePercent")
    @Expose
    private double changePercent;

    public String getTicker() { return ticker; }

    public String getName() {
        return name;
    }

    public String getCur() {
        return cur;
    }

    public double getPrice() {
        return price;
    }

    public double getChange() {
        return change;
    }

    public double getChangePercent() {
        return changePercent;
    }

}
