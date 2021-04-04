package com.r.stocks.models;

import com.r.stocks.response.CompanyResponse;
import com.r.stocks.response.PriceResponse;

import java.io.Serializable;

public class CompanyModel implements Serializable {
    private String ticker;
    private String name;
    private String cur;
    private double price;
    private double change;
    private double changePercent;
    private boolean isFavorite;

    public CompanyModel(CompanyResponse companyResponse) {
        ticker = companyResponse.getTicker();
        name = companyResponse.getName();
        cur = companyResponse.getCur();
        price = companyResponse.getPrice();
        change = companyResponse.getChange();
        changePercent = companyResponse.getChangePercent();
        isFavorite = false;
    }

    public CompanyModel(CompanyModel companyModel) {
        ticker = companyModel.getTicker();
        name = companyModel.getName();
        cur = companyModel.getCur();
        price = companyModel.getPrice();
        change = companyModel.getChange();
        changePercent = companyModel.getChangePercent();
        isFavorite = companyModel.isFavorite();
    }

    public String getTicker() { return ticker; }

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

    public void setPrice(PriceResponse price) {
        this.price = price.getCurrentPrice();
        setChange(price.getCurrentPrice(), price.getPreviousPrice());
    }

    private void setChange(double currentPrice, double previousPrice) {
        change = currentPrice - previousPrice;
        changePercent = (currentPrice / previousPrice - 1) * 100;
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
                '}';
    }
}
