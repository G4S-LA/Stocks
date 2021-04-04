package com.r.stocks.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.r.stocks.repositories.NewsRepository;
import com.r.stocks.response.NewsResponse;

import java.util.List;

public class NewsViewModel extends ViewModel {

    private NewsRepository newsRepository;

    public NewsViewModel() {
        newsRepository = NewsRepository.getInstance();
    }

    public LiveData<List<NewsResponse>> getNews(){
        return newsRepository.getNews();
    }

    public void searchNews(String ticker){
        newsRepository.searchNews(ticker);
    }
}
