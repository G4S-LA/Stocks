package com.r.stocks.repositories;

import androidx.lifecycle.LiveData;

import com.r.stocks.request.NewsApiClient;
import com.r.stocks.response.NewsResponse;

import java.util.List;

public class NewsRepository {
    private static NewsRepository instance;

    private NewsApiClient newsApiClient;

    public static NewsRepository getInstance() {
        if (instance == null) {
            instance = new NewsRepository();
        }
        return instance;
    }

    private NewsRepository() {
        newsApiClient = NewsApiClient.getInstance();
    }

    public LiveData<List<NewsResponse>> getNews() {
        return newsApiClient.getNews();
    }

    public void searchNews(String ticker) {
        newsApiClient.searchNews(ticker);
    }
}
