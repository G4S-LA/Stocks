package com.r.stocks.repositories;

import androidx.lifecycle.LiveData;

import com.r.stocks.db.CacheFavorite;
import com.r.stocks.models.CompanyModel;
import com.r.stocks.request.StocksApiClient;

import java.util.List;

public class StocksRepository {

    private static StocksRepository instance;

    private StocksApiClient stocksApiClient;

    private CacheFavorite cacheFavorite;

    public static StocksRepository getInstance() {
        if (instance == null) {
            instance = new StocksRepository();
        }
        return instance;
    }

    private StocksRepository() {
        stocksApiClient = StocksApiClient.getInstance();
        cacheFavorite = CacheFavorite.getInstance();
    }

    public void removeFromFavorite(CompanyModel company) {
        cacheFavorite.removeFromFavorite(company);
        stocksApiClient.removeFavorite(company);
    }

    public void saveFavorites() {
        cacheFavorite.saveFavorites(stocksApiClient.getFavorites().getValue());
    }

    public void loadCacheFavorite() {
        stocksApiClient.setFavorites(cacheFavorite.loadCacheFavorite());
    }

    public LiveData<List<CompanyModel>> getCompanies() {
        return stocksApiClient.getCompanies();
    }

    public LiveData<List<CompanyModel>> getFavorites() {
        return stocksApiClient.getFavorites();
    }

    public void searchStocks(int countOfStocks) {
        stocksApiClient.searchStocks(countOfStocks, cacheFavorite.getFavoriteStocks());
    }

    public void searchFavorites() {
        stocksApiClient.searchFavorites(cacheFavorite.favoriteTickers());
    }

    public void addToFavorite(CompanyModel company) {
        cacheFavorite.addToFavorite(company);
        stocksApiClient.addToFavorite(company);
    }
}
