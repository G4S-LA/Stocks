package com.r.stocks.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanyResponse {

    private String ticker;

    private String name;

    @SerializedName("currency")
    @Expose
    private String cur;

    private double price;

    private double change;

    @SerializedName("logo")
    @Expose
    private String image;

    public CompanyResponse(CompanyResponse company) {
        this.ticker = company.ticker;
        this.name = company.name;
        this.cur = company.cur;
        this.price = company.price;
        this.change = company.change;
        this.changePercent = company.changePercent;
        this.image = company.image;
    }

    private double changePercent;

    public String getTicker() {
        return ticker;
    }

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
