package com.r.stocks.models;

import android.graphics.Bitmap;

import com.r.stocks.response.CompanyResponse;

import java.io.Serializable;

public class CompanyModel implements Serializable {
    private String ticker;
    private String name;
    private String cur;
    private double price;
    private double change;
    private double changePercent;
    private boolean isFavorite;
    private Bitmap image;

    public CompanyModel(String ticker, String name, String cur, double price, double change, double changePercent, boolean isFavorite, Bitmap image) {
        this.ticker = ticker;
        this.name = name;
        this.cur = cur;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
        this.isFavorite = isFavorite;
        this.image = image;
    }

    public CompanyModel(CompanyResponse companyResponse, boolean isFavorite) {
        ticker = companyResponse.getTicker();
        name = companyResponse.getName();
        cur = companyResponse.getCur();
        price = companyResponse.getPrice();
        change = companyResponse.getChange();
        changePercent = companyResponse.getChangePercent();
        this.isFavorite = isFavorite;
        image = null;
    }

    public CompanyModel(CompanyResponse companyResponse) {
        ticker = companyResponse.getTicker();
        name = companyResponse.getName();
        cur = companyResponse.getCur();
        price = companyResponse.getPrice();
        change = companyResponse.getChange();
        changePercent = companyResponse.getChangePercent();
        isFavorite = false;
        image = null;
    }

    public CompanyModel(CompanyModel companyModel) {
        ticker = companyModel.getTicker();
        name = companyModel.getName();
        cur = companyModel.getCur();
        price = companyModel.getPrice();
        change = companyModel.getChange();
        changePercent = companyModel.getChangePercent();
        isFavorite = companyModel.isFavorite();
        image = companyModel.image;
    }

    public String getTicker() { return ticker; }

    public Bitmap getImage() { return image; }

    public void setImage(Bitmap image) { this.image = image; }

    public void setFavorite(boolean favorite){
        isFavorite = favorite;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    @Override
    public String toString() {
        return "CompanyModel{" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", cur='" + cur + '\'' +
                ", price=" + price +
                ", change=" + change +
                ", changePercent=" + changePercent +
                ", isFavorite=" + isFavorite +
                ", image=" + image +
                '}';
    }
}
