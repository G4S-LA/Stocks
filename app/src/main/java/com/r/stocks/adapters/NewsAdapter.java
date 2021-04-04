package com.r.stocks.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.r.stocks.R;
import com.r.stocks.WebActivity;
import com.r.stocks.response.NewsResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsResponse> newsDataList;

    public NewsAdapter() {
        newsDataList = null;
    }

    public void setList(List<NewsResponse> newsDataList) {
        this.newsDataList = newsDataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsResponse newsData = newsDataList.get(position);
        holder.title.setText(newsData.getTitle());
        holder.description.setText(newsData.getDescription());
        holder.url = newsData.getUrl();
        Picasso.get().load(newsData.getUrlToImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (newsDataList == null) {
            return 0;
        }
        return newsDataList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView imageView;
        LinearLayout parent;
        String url;

        public NewsViewHolder(@NonNull final View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.parent);
            title = itemView.findViewById(R.id.newsTitle);
            description = itemView.findViewById(R.id.description);
            imageView = itemView.findViewById(R.id.imageView_news);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), WebActivity.class);
                    intent.putExtra("url", url);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}

