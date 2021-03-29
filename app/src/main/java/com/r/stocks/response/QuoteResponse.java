package com.r.stocks.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuoteResponse {
    private int count;

    @SerializedName("quotes")
    private List<String> allQuotes;

    public String getAllStocks(int size) {
        if (size < 0){
            size = count;
        }
        if (allQuotes != null) {
            StringBuilder result = new StringBuilder(allQuotes.get(0));

            for (int i = 1; i < size && i < count; i++) {
                result.append(',').append(allQuotes.get(i));
            }
            return result.toString();
        }
        return "";
    }
}
