package com.r.stocks.db;

import android.util.Log;

import com.r.stocks.MyApplication;
import com.r.stocks.models.AllCompanies;
import com.r.stocks.models.CompanyModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CacheFavorite {

    private static final String FILE_TICKERS = MyApplication.getInstance().getExternalCacheDir() + File.separator + "favorites.txt";
    private static final String FILE_STOCKS = MyApplication.getInstance().getExternalCacheDir() + File.separator + "stocks.txt";
    private static final String TAG = "Favorites";

    private static CacheFavorite instance;
    private Set<String> favorites;


    public static CacheFavorite getInstance() {
        if (instance == null) {
            instance = new CacheFavorite();
        }
        return instance;
    }

    private CacheFavorite() {
        favorites = getFavorites();
    }

    private Set<String> getFavorites() {
        File file = new File(FILE_TICKERS);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fileInputStream, "UTF-8");
            int c;
            while ((c = isr.read()) != -1) {
                stringBuilder.append((char) c);
            }
            String[] a = stringBuilder.toString().split("\n");
            Log.v(TAG, "Favorites cache loaded successfully :" + stringBuilder.toString());

            return new HashSet<>(Arrays.asList(a));
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "Failed to load cache");
        }

        return new HashSet<>();
    }

    public Set<String> getFavoriteStocks() {
        return favorites;
    }

    public void addToFavorite(CompanyModel company) {
        favorites.add(company.getTicker());
    }

    public void saveFavorites(List<CompanyModel> allFavoriteCompanies) {
        saveTickers();
        AllCompanies stocks = new AllCompanies(allFavoriteCompanies);
        saveStocks(stocks);
    }

    private void saveTickers() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String ticker : favorites) {
            stringBuilder.append(ticker).append("\n");
        }
        try {
            String result = new String(stringBuilder);
            FileOutputStream fileOutputStream = new FileOutputStream(FILE_TICKERS);
            fileOutputStream.write(result.getBytes());
            Log.v(TAG, "Tickers of favorites saved successfully:\n" + result);
        } catch (Exception e) {
            Log.v(TAG, "Failed to save tickers of favorites");
            e.printStackTrace();
        }
    }

    private void saveStocks(AllCompanies allFavorites) {
        File file = new File(FILE_STOCKS);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutput objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(allFavorites);
            objectOutputStream.close();
            Log.v(TAG, "Favorites saved successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "Failed to save favorites");
        }
    }

    public List<CompanyModel> loadCacheFavorite() {
        AllCompanies stocks;
        File file_stocks = new File(FILE_STOCKS);
        if (file_stocks.exists()) {
            try {
                FileInputStream fos = new FileInputStream(file_stocks);
                ObjectInputStream objectInputStream = new ObjectInputStream(fos);
                stocks = (AllCompanies) objectInputStream.readObject();
                Log.v(TAG, "File with favorites was read successfully");
                objectInputStream.close();
                fos.close();
                return stocks.getAllCompanies();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Log.v(TAG, "File with favorites is empty");
            }
        } else {
            Log.v(TAG, "File with favorites not found");
        }
        return null;
    }

    public void removeFromFavorite(CompanyModel company) {
        favorites.remove(company.getTicker());
    }
}
