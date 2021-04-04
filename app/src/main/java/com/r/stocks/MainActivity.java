package com.r.stocks;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.r.stocks.adapters.StocksAdapter;
import com.r.stocks.utils.ButtonStatus;
import com.r.stocks.models.CompanyModel;
import com.r.stocks.utils.LoadingStatus;
import com.r.stocks.viewmodels.StocksViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements StocksAdapter.OnStocksListener {

    private static final int countOfSearchingStocks = 25; // В случае невалидных ответов итоговое кол-во акций будет меньше. Также я не до конца разобрался с многопоточностью, поэтому часть запросов акций тоже уходит в никуда(
    private RecyclerView recyclerView;
    private TextView stocks, favorite;
    private Button refresh;
    private StocksAdapter stocksAdapter;
    private StocksViewModel stocksViewModel;
    private SearchView searchView;
    private ProgressBar progressBar;
    private ButtonStatus buttonStatus;
    private LoadingStatus loadingStatus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerComponents();

        stocksViewModel = new ViewModelProvider(this).get(StocksViewModel.class);

        observeAnyChange();

        setAllListeners();

        configureRecycleView();

        searchStocksApi();
    }

    private void registerComponents() {
        buttonStatus = ButtonStatus.ALL;
        recyclerView = findViewById(R.id.rv_stocks);
        searchView = findViewById(R.id.search);
        progressBar = findViewById(R.id.progress_bar);
        stocks = findViewById(R.id.tv_stocks);
        favorite = findViewById(R.id.tv_favorites);
        refresh = findViewById(R.id.refresh);
        loadingStatus = LoadingStatus.START;
        buttonStatus = ButtonStatus.ALL;
    }

    private void setAllListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                stocksAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                stocksAdapter.getFilter().filter(newText);
                return false;
            }
        });

        stocks.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                stocks.setTextAppearance(R.style.Selected);
                favorite.setTextAppearance(R.style.Default);
                setButtonStatus(ButtonStatus.ALL);
                if (loadingStatus == LoadingStatus.LOADING) {
                    buttonRefreshOFF();
                }
                if (loadingStatus == LoadingStatus.START) {
                    buttonRefreshON();
                }
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                stocks.setTextAppearance(R.style.Default);
                favorite.setTextAppearance(R.style.Selected);
                setButtonStatus(ButtonStatus.FAVORITE);
                allOFF();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonStatus == ButtonStatus.ALL) {
                    buttonRefreshOFF();
                }
                searchStocksApi();
            }
        });
    }

    private void setButtonStatus(ButtonStatus buttonStatus) {
        this.buttonStatus = buttonStatus;
        stocksAdapter.setButtonStatus(buttonStatus);
    }

    private void observeAnyChange() {

        stocksViewModel.getCompanies().observe(this, new Observer<List<CompanyModel>>() {
            @Override
            public void onChanged(List<CompanyModel> companies) {
                if (companies != null) {
                    allOFF();
                    stocksAdapter.setListStocks(companies);
                    loadingStatus = LoadingStatus.FINISH;
                    Log.v("Stocks", "STOCKS has been updated");
                } else {
                    loadingStatus = LoadingStatus.START;
                    if (buttonStatus == ButtonStatus.ALL) {
                        buttonRefreshON();
                    }
                    showToast("Something went wrong");
                    Log.v("Stocks", "Stocks is null");
                }
            }
        });

        stocksViewModel.getFavorites().observe(this, new Observer<List<CompanyModel>>() {
            @Override
            public void onChanged(List<CompanyModel> favorites) {
                if (favorites != null) {
                    stocksAdapter.setListFavorites(favorites);
                    Log.v("Favorites", "FAVORITE has been updated");
                } else {
                    if (buttonStatus == ButtonStatus.FAVORITE) {
                        showToast("You haven't favorites yet");
                    }
                    Log.v("Favorites", "Favorite is null");
                }
            }
        });
    }

    private void searchStocksApi() {

        stocksViewModel.searchStocks(countOfSearchingStocks);
        stocksViewModel.searchFavorites();

        loadingStatus = LoadingStatus.LOADING;
    }

    private void buttonRefreshON() {
        refresh.setVisibility(Button.VISIBLE);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void buttonRefreshOFF() {
        refresh.setVisibility(Button.INVISIBLE);
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    private void allOFF() {
        refresh.setVisibility(Button.INVISIBLE);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void configureRecycleView() {
        stocksAdapter = new StocksAdapter(this);

        recyclerView.setAdapter(stocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStockClick(CompanyModel company) {
        Intent intent = new Intent(this, NewsActivity.class);
        intent.putExtra("ticker", company.getTicker());
        intent.putExtra("name", company.getName());
        this.startActivity(intent);
    }

    @Override
    public void onStarClick(CompanyModel company) {
        if (!company.isFavorite() && buttonStatus == ButtonStatus.ALL) {
            showToast(company.getTicker() + " added to Favorite");
            company.setFavorite(true);
            stocksViewModel.addToFavorite(company);
            stocksAdapter.notifyDataSetChanged();
        } else if (company.isFavorite() && buttonStatus == ButtonStatus.FAVORITE) {
            showToast(company.getTicker() + " removed from Favorite");
            company.setFavorite(false);
            stocksViewModel.removeFromFavorite(company);
            stocksAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView.clearFocus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stocksViewModel.saveFavorites();
    }
}