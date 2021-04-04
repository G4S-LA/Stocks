package com.r.stocks.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.r.stocks.MyApplication;
import com.r.stocks.models.CompanyModel;
import com.r.stocks.response.CompanyResponse;
import com.r.stocks.response.PriceResponse;
import com.r.stocks.response.TickerResponse;
import com.r.stocks.utils.ButtonStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StocksApiClient {

    private static final String TAG = "StocksAPI";
    volatile private MutableLiveData<List<CompanyModel>> mCompanies;
    private MutableLiveData<List<CompanyModel>> mFavorites;
    private List<CompanyModel> cachedFavorites;
    private static StocksApiClient instance;
    private ImagesApiClient imagesApiClient;

    public static StocksApiClient getInstance() {
        if (instance == null) {
            instance = new StocksApiClient();
        }
        return instance;
    }

    public void setFavorites(List<CompanyModel> companies) {
        if (cachedFavorites == null && companies != null) {
            cachedFavorites = new ArrayList<>(companies);
        }
    }

    private StocksApiClient() {
        mCompanies = new MutableLiveData<>();
        mFavorites = new MutableLiveData<>();
        imagesApiClient = ImagesApiClient.getInstance();
    }

    public LiveData<List<CompanyModel>> getCompanies() {
        return mCompanies;
    }

    public LiveData<List<CompanyModel>> getFavorites() {
        return mFavorites;
    }

    public void addToFavorite(CompanyModel company) {
        List<CompanyModel> favorites = mFavorites.getValue();
        if (favorites == null) {
            favorites = new ArrayList<>(Collections.singletonList(company));
        } else {
            favorites.add(company);
        }
        mFavorites.setValue(favorites);
    }

    public void removeFavorite(CompanyModel company) {
        List<CompanyModel> favorites = mFavorites.getValue();
        if (favorites != null && favorites.contains(company)) {
            favorites.remove(company);
            mFavorites.postValue(favorites);
        }
        List<CompanyModel> allCompanies = mCompanies.getValue();
        if (allCompanies != null) {
            for (CompanyModel stock : allCompanies) {
                if (stock.getTicker().equals(company.getTicker())) {
                    stock.setFavorite(false);
                    mCompanies.postValue(allCompanies);
                    break;
                }
            }
        }
    }

    public void searchStocks(final int countOfStocks, final Set<String> favorites) {
        MyService.getStocksApi().getAllTickers(MyService.COUNTRY, MyService.INDEX, MyService.API_KEY).enqueue(new Callback<List<TickerResponse>>() {
            @Override
            public void onResponse(Call<List<TickerResponse>> call, Response<List<TickerResponse>> response) {
                if (response.body() != null) {
                    List<TickerResponse> tmp = new ArrayList<>(response.body());
                    Log.v(TAG, "Array = " + tmp.toString());
                    searchCompanies(tmp, countOfStocks, favorites);
                } else {
                    Log.v(TAG, "Response of all tickers is null");
                    mCompanies.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<TickerResponse>> call, Throwable t) {
                mCompanies.postValue(null);
                Log.v(TAG, "Response of all tickers - FAIL");
            }
        });
    }

    private void searchCompanies(final List<TickerResponse> tickers, final int countOfStocks, final Set<String> favorites) {
        for (int i = 0; i < countOfStocks && i < tickers.size(); i++) {
            final int finalI = i;
            MyService.getStocksApi().getCompany(tickers.get(i).getTicker(), MyService.API_KEY).enqueue(new Callback<CompanyResponse>() {
                @Override
                public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                    try {
                        if (!isResponseOfCompanyEmpty(response.body())) {
                            CompanyModel company = new CompanyModel(response.body());
                            if (favorites.contains(company.getTicker())) {
                                company.setFavorite(true);
                            }
                            searchPriceForCompany(company, ButtonStatus.ALL);
                            imagesApiClient.searchImages(company);
                        }
                    } catch (Exception e) {
                        Log.v(TAG, "Conversion of response(companies) - FAIL");
                    }
                }

                @Override
                public void onFailure(Call<CompanyResponse> call, Throwable t) {
                    Log.v(TAG, "Response of companies(companies) - FAIL");
                    if (finalI == countOfStocks - 1 || finalI == tickers.size() - 1) {
                        checkLastResponse();
                    }
                }
            });
        }
    }

    public void searchFavorites(Set<String> tickers) {
        if (!MyApplication.hasNetwork()) {
            mFavorites.setValue(cachedFavorites);
            return;
        }
        Log.v(TAG, "After loading Favorites: " + tickers.toString());
        for (String ticker : tickers) {
            MyService.getStocksApi().getCompany(ticker, MyService.API_KEY).enqueue(new Callback<CompanyResponse>() {
                @Override
                public void onResponse(Call<CompanyResponse> call, Response<CompanyResponse> response) {
                    try {
                        Log.v(TAG, "Response of favorites - OK");
                        if (!isResponseOfCompanyEmpty(response.body())) {
                            CompanyModel company = new CompanyModel(response.body());
                            company.setFavorite(true);
                            searchPriceForCompany(company, ButtonStatus.FAVORITE);
                        }
                    } catch (Exception e) {
                        Log.v(TAG, "Conversion of response(favorites) - FAIL");
                    }
                }

                @Override
                public void onFailure(Call<CompanyResponse> call, Throwable t) {
                    Log.v(TAG, "Response of companies(favorites) - FAIL");
                }
            });
        }
    }

    private void searchPriceForCompany(final CompanyModel company, final ButtonStatus buttonStatus) {
        if (company != null) {
            MyService.getStocksApi().getPrice(company.getTicker(), MyService.API_KEY).enqueue(new Callback<PriceResponse>() {
                @Override
                public void onResponse(Call<PriceResponse> call, Response<PriceResponse> response) {
                    try {
                        Log.v(TAG, "Company = " + company.getTicker() + " have price " + response.body().getCurrentPrice() + company.getCur());
                        company.setPrice(response.body());
                        publishResults(company, buttonStatus);
                    } catch (Exception e) {
                        Log.v(TAG, "Conversion of response(price) - FAIL for " + company.getTicker());
                    }
                }

                @Override
                public void onFailure(Call<PriceResponse> call, Throwable t) {
                    Log.v(TAG, "Response of price - FAIL");
                }
            });
        }
    }

    private boolean isResponseOfCompanyEmpty(CompanyResponse company) {
        return company == null || company.getName() == null;
    }

    private void checkLastResponse() {
        List<CompanyModel> currentCompanies = mCompanies.getValue();
        if (currentCompanies == null) {
            Log.v(TAG, "All requests were empty");
            mCompanies.setValue(null);
        }
    }

    public void publishResults(CompanyModel company, ButtonStatus buttonStatus) {
        if (company.getPrice() != 0.0) {
            List<CompanyModel> currentCompanies;
            if (buttonStatus == ButtonStatus.ALL) {
                currentCompanies = mCompanies.getValue();
            } else {
                currentCompanies = mFavorites.getValue();
            }
            if (currentCompanies == null) {
                currentCompanies = new ArrayList<>();
            }
            currentCompanies.add(company);
            if (buttonStatus == ButtonStatus.ALL) {
                mCompanies.setValue(currentCompanies);
            } else {
                mFavorites.setValue(currentCompanies);
            }
            Log.v(TAG, company.getTicker() + " was added");
        }
    }
}
