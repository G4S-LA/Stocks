package com.r.stocks.request;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.r.stocks.response.NewsResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsApiClient {

    private MutableLiveData<List<NewsResponse>> mNews;
    private static NewsApiClient instance;
    private String dateFrom;
    private String dateTo;

    public static NewsApiClient getInstance() {
        if (instance == null) {
            instance = new NewsApiClient();
        }
        return instance;
    }

    private NewsApiClient() {
        mNews = new MutableLiveData<>();
    }

    public LiveData<List<NewsResponse>> getNews() {
        return mNews;
    }

    public void searchNews(final String ticker) {
        setDates();
        MyService.getNewsApi().searchNews(ticker, dateFrom, dateTo, MyService.API_KEY).enqueue(new Callback<List<NewsResponse>>() {
            @Override
            public void onResponse(Call<List<NewsResponse>> call, Response<List<NewsResponse>> response) {
                List<NewsResponse> news = response.body();
                Log.v("News","Search for " + ticker);
                List<NewsResponse> tmp = new ArrayList<>();
                for (int i = 0, size = news.size(); i < size; i++) {
                    tmp.add(new NewsResponse(news.get(i)));
                }
                mNews.setValue(tmp);
            }

            @Override
            public void onFailure(Call<List<NewsResponse>> call, Throwable t) {
                mNews.setValue(null);
            }
        });
    }

    private void setDates() {
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateFrom = sdf.format(cal.getTime());
        dateTo = sdf.format(currentDate);
    }
}
