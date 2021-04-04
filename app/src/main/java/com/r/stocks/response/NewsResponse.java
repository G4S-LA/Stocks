package com.r.stocks.response;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;

public class NewsResponse {
    @SerializedName("headline")
    String title;

    @SerializedName("summary")
    String description;

    @SerializedName("image")
    String urlToImage;

    String url;

    public NewsResponse(NewsResponse data) {
        this.title = data.title;
        this.description = data.description;
        this.url = data.url;
        this.urlToImage = data.urlToImage;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getUrl() {
        return url;
    }
}
