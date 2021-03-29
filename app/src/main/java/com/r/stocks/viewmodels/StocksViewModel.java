package com.r.stocks.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.r.stocks.models.CompanyModel;
import com.r.stocks.repositories.StocksRepository;

import java.util.List;

public class StocksViewModel extends ViewModel {

    private StocksRepository stocksRepository;

    public StocksViewModel() {
        stocksRepository = StocksRepository.getInstance();
    }

    public LiveData<List<CompanyModel>> getCompanies(){
        return stocksRepository.getCompanies();
    }

    public LiveData<List<CompanyModel>> getFavorites(){
        return stocksRepository.getFavorites();
    }

    public void searchStocks(int countOfStocks){
        stocksRepository.searchStocks(countOfStocks);
    }

    public void searchFavorites(){
        stocksRepository.loadCacheFavorite();
        stocksRepository.searchFavorites();
    }

    public void saveFavorites(){
        stocksRepository.saveFavorites();
    }

    public void addToFavorite(CompanyModel company) {
        stocksRepository.addToFavorite(company);
    }

    public void removeFromFavorite(CompanyModel company) {
        stocksRepository.removeFromFavorite(company);
    }
}
