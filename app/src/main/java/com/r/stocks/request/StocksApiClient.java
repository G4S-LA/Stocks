package com.r.stocks.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.r.stocks.MyApplication;
import com.r.stocks.models.AllCompanies;
import com.r.stocks.models.CompanyModel;
import com.r.stocks.response.CompanyResponse;
import com.r.stocks.response.QuoteResponse;
import com.r.stocks.utils.Status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StocksApiClient {

    private static final String TAG = "API";

    private MutableLiveData<List<CompanyModel>> mCompanies;
    private MutableLiveData<List<CompanyModel>> mFavorites;
    private List<CompanyModel> cachedFavorites;
    private static StocksApiClient instance;


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
        mFavorites.postValue(favorites);
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

        MyService.getStocksApi().getAllQuotes(MyService.API_KEY).enqueue(new Callback<List<QuoteResponse>>() {
            @Override
            public void onResponse(Call<List<QuoteResponse>> call, Response<List<QuoteResponse>> response) {
                if (response.body() != null) {
                    Log.v(TAG, "Get stocks:\n" + response.body().get(0).getAllStocks(countOfStocks));
                    searchCompanies(response.body().get(0).getAllStocks(countOfStocks), favorites);
                } else {
                    mCompanies.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<QuoteResponse>> call, Throwable t) {
                mCompanies.postValue(null);
            }
        });
    }

    private void searchImages(final List<CompanyModel> companies, final Status status) {
        for (final CompanyModel company : companies) {
            final String file = MyApplication.getInstance().getExternalFilesDir(null) +
                    File.separator + company.getTicker() + ".jpg";
            if (!haveImage(file)) {
                Call<ResponseBody> call = MyService.getImageApi().getImage(getNameOfImage(company.getName()));

                call.enqueue((new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            Log.v(TAG, "Response of image - OK");
                            boolean FileDownloaded = DownloadImage(response.body(), company);
                            Log.v(TAG, "Image is downloaded? - " + FileDownloaded + " company - " + company.getName());
                            if (status == Status.ALL) {
                                mCompanies.postValue(companies);
                            } else {
                                mFavorites.postValue(companies);
                            }
                        } catch (Exception e) {
                            Log.v(TAG, "Image exception");
                            setDefaultImage(company);
                            if (status == Status.ALL) {
                                mCompanies.postValue(companies);
                            } else {
                                mFavorites.postValue(companies);
                            }
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.v(TAG, "Response of image - FAIL");
                        setDefaultImage(company);
                        if (status == Status.ALL) {
                            mCompanies.postValue(companies);
                        } else {
                            mFavorites.postValue(companies);
                        }
                    }
                }));
            } else {
                Bitmap image = BitmapFactory.decodeFile(file);
                company.setImage(image);
                if (status == Status.ALL) {
                    mCompanies.postValue(companies);
                } else {
                    mFavorites.postValue(companies);
                }
                Log.v(TAG, "Image from cache, company - " + company.getName());
            }
        }
    }

    private void setDefaultImage(CompanyModel company) {
        int drawableID = MyApplication.getInstance().getResources().getIdentifier("not_stonks", "drawable", MyApplication.getInstance().getPackageName());
        Bitmap image = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), drawableID);
        company.setImage(image);
    }

    private boolean haveImage(String ticker) {
        File file = new File(ticker);
        return file.exists();
    }

    private String getNameOfImage(String name) {
        String result = name.toLowerCase();

        if (result.indexOf(',') != -1) {
            result = result.substring(0, result.indexOf(','));
        }
        if (result.indexOf('.') != -1) {
            result = result.substring(0, result.indexOf('.'));
        }
        if (result.indexOf(' ') != -1) {
            result = result.substring(0, result.indexOf(' '));
        }

        return result;
    }

    private boolean DownloadImage(ResponseBody body, CompanyModel company) {
        Log.v(TAG, "Downloading Image");

        try (InputStream in = body.byteStream();
             FileOutputStream out = new FileOutputStream(MyApplication.getInstance().getExternalFilesDir(null) +
                     File.separator + company.getTicker() + ".jpg")) {
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } catch (IOException e) {
            Log.v(TAG, "Downloading Image - FAIL");
            return false;
        }
        Bitmap bMap = BitmapFactory.decodeFile(MyApplication.getInstance().getExternalFilesDir(null) +
                File.separator + company.getTicker() + ".jpg");
        company.setImage(bMap);
        return true;
    }

    private void searchCompanies(String tickers, final Set<String> favorites) {
        MyService.getStocksApi().getCompany(tickers, MyService.API_KEY).enqueue(new Callback<List<CompanyResponse>>() {
            @Override
            public void onResponse(Call<List<CompanyResponse>> call, Response<List<CompanyResponse>> response) {
                try {
                    Log.v(TAG, "Response of companies - OK");
                    List<CompanyModel> companies = new AllCompanies(response.body(), favorites).getAllCompanies();

                    mCompanies.postValue(companies);
                    searchImages(companies, Status.ALL);
                } catch (Exception e) {
                    mCompanies.postValue(null);
                    Log.v(TAG, "Conversion of response(companies) - FAIL");
                }
            }

            @Override
            public void onFailure(Call<List<CompanyResponse>> call, Throwable t) {
                Log.v(TAG, "Response of companies(companies) - FAIL");
                mCompanies.postValue(null);
            }
        });
    }

    public void searchFavorites(String tickers) {
        MyService.getStocksApi().getCompany(tickers, MyService.API_KEY).enqueue(new Callback<List<CompanyResponse>>() {
            @Override
            public void onResponse(Call<List<CompanyResponse>> call, Response<List<CompanyResponse>> response) {
                try {
                    Log.v(TAG, "Response of favorites - OK");
                    List<CompanyModel> companies = new AllCompanies(response.body(), true).getAllCompanies();
                    mFavorites.postValue(companies);
                    searchImages(companies, Status.FAVORITE);
                } catch (Exception e) {
                    Log.v(TAG, "Conversion of response(favorites) - FAIL");
                    mFavorites.postValue(cachedFavorites);
                }
            }

            @Override
            public void onFailure(Call<List<CompanyResponse>> call, Throwable t) {
                Log.v(TAG, "Response of companies(favorites) - FAIL");
                mFavorites.postValue(cachedFavorites);
            }
        });
    }
}
