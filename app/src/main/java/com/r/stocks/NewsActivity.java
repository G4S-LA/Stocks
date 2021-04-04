package com.r.stocks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.r.stocks.adapters.NewsAdapter;
import com.r.stocks.request.MyService;
import com.r.stocks.response.NewsResponse;
import com.r.stocks.utils.NewsApi;
import com.r.stocks.viewmodels.NewsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private TextView companyName;
    private String ticker;
    private NewsViewModel newsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        registerComponents();

        newsViewModel = new ViewModelProvider(NewsActivity.this).get(NewsViewModel.class);

        configureRecycleView();

        observeAnyChange();

        newsViewModel.searchNews(ticker);
    }

    private void registerComponents() {
        ticker = getIntent().getStringExtra("ticker");
        companyName = findViewById(R.id.companyName);
        companyName.setText(getIntent().getStringExtra("name"));
    }

    private void configureRecycleView() {
        recyclerView = findViewById(R.id.recycler_view_news);
        newsAdapter = new NewsAdapter();
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
    }

    private void observeAnyChange() {
        newsViewModel.getNews().observe(this, new Observer<List<NewsResponse>>() {
            @Override
            public void onChanged(List<NewsResponse> newsResponses) {
                if (newsResponses == null) {
                    showToast("Probably you haven't internet connection");
                }
                newsAdapter.setList(newsResponses);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}