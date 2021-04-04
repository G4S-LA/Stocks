package com.r.stocks.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.r.stocks.R;
import com.r.stocks.models.CompanyModel;
import com.r.stocks.utils.ButtonStatus;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.r.stocks.request.MyService.IMAGE_FOLDER;
import static com.r.stocks.request.MyService.IMAGE_RESOLUTION;

public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.StocksViewHolder> implements Filterable {

    private ButtonStatus buttonStatus;
    private List<CompanyModel> listStocks;
    private List<CompanyModel> listStocksFull;
    private List<CompanyModel> listStocksFullFavorites;
    private OnStocksListener onStocksListener;

    public StocksAdapter(OnStocksListener onStocksListener) {
        this.onStocksListener = onStocksListener;
        buttonStatus = ButtonStatus.ALL;
    }

    public void setButtonStatus(ButtonStatus buttonStatus) {
        this.buttonStatus = buttonStatus;
        Log.v("Status", "Status was changed");
        changeListStocks();
        notifyDataSetChanged();
    }

    public void setListStocks(List<CompanyModel> listStocksFull) {
        this.listStocksFull = listStocksFull;
        if (buttonStatus == ButtonStatus.ALL) {
            if (listStocks != null) {
                listStocks.clear();
            }
            listStocks = new ArrayList<>(listStocksFull);
            notifyDataSetChanged();
        }
    }

    public void setListFavorites(List<CompanyModel> listStocksFullFavorites) {
        this.listStocksFullFavorites = listStocksFullFavorites;
        if (buttonStatus == ButtonStatus.FAVORITE) {
            if (listStocks != null) {
                listStocks.clear();
            }
            listStocks = new ArrayList<>(listStocksFullFavorites);
            notifyDataSetChanged();
        }
    }

    private void changeListStocks() {
        if (listStocks != null) {
            listStocks.clear();
        }
        if (buttonStatus == ButtonStatus.FAVORITE && listStocksFullFavorites != null) {
            listStocks = new ArrayList<>(listStocksFullFavorites);
        } else if (buttonStatus == ButtonStatus.ALL && listStocksFull != null) {
            listStocks = new ArrayList<>(listStocksFull);
        }
    }

    @NonNull
    @Override
    public StocksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.stocks_list_item, parent, false);

        if (viewType == 0) {
            view.setBackgroundResource(R.drawable.background_stocks_menu);
        }

        return new StocksViewHolder(view, onStocksListener);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull StocksViewHolder holder, int position) {

        holder.ticker.setText(listStocks.get(position).getTicker());
        holder.company.setText(listStocks.get(position).getName());
        holder.price.setText(String.format("%.2f", listStocks.get(position).getPrice()) +
                getCurrency(listStocks.get(position).getCur()));
        holder.change.setText(getChangePrice(
                listStocks.get(position).getChange(),
                listStocks.get(position).getChangePercent(),
                listStocks.get(position).getCur()));
        holder.change.setTextColor(getColorOfChange(listStocks.get(position).getChange()));
        if (listStocks.get(position).isFavorite()) {
            holder.star.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.star.setImageResource(android.R.drawable.btn_star_big_off);
        }
        holder.companyModel = listStocks.get(position);
        Picasso.get()
                .load(new File(IMAGE_FOLDER + holder.companyModel.getTicker() + IMAGE_RESOLUTION))
                .into(holder.logo);
    }

    private int getColorOfChange(double value) {
        if (value > 0) {
            return 0xFF00A94A; // GREEN
        }
        if (value < 0) {
            return 0xFFBA0000; // RED
        }
        return 0xFF646464; // GREY
    }

    @SuppressLint("DefaultLocale")
    private String getChangePrice(double value, double valuePercent, String cur) {
        StringBuilder result = new StringBuilder();
        if (value > 0) {
            result.append('+');
        }
        valuePercent = Math.abs(valuePercent);

        result.append(String.format("%.2f", value))
                .append(getCurrency(cur))
                .append(" (")
                .append(String.format("%.2f", valuePercent))
                .append('%')
                .append(')');

        return result.toString();
    }

    private String getCurrency(String cur) {
        if (cur != null) {
            if (cur.equals("USD")) {
                return "$";
            }
            if (cur.equals("RUB")) {
                return "₽";
            }
            if (cur.equals("EUR")) {
                return "€";
            }
            if (cur.equals("GBP")) {
                return "£";
            }
            if (cur.equals("BTC")) {
                return "B";
            }
            if (cur.equals("CAD")) {
                return "C$";
            }
            if (cur.equals("CNY")) {
                return "¥";
            }
            if (cur.equals("THB")) {
                return "฿";
            }
        }
        return "?";
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public int getItemCount() {
        if (listStocks != null) {
            return listStocks.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return stocksFilter;
    }

    private Filter stocksFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CompanyModel> filteredList = new ArrayList<>();
            List<CompanyModel> allStocks;
            if (buttonStatus == ButtonStatus.ALL) {
                allStocks = listStocksFull;
            } else {
                allStocks = listStocksFullFavorites;
            }

            if (constraint == null || constraint.length() == 0) {
                if (buttonStatus == ButtonStatus.FAVORITE) {
                    for (CompanyModel company : allStocks) {
                        if (company.isFavorite()) {
                            filteredList.add(company);
                        }
                    }
                } else {
                    filteredList.addAll(allStocks);
                }
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CompanyModel item : allStocks) {
                    if (item.getName().toLowerCase().contains(filterPattern) || item.getTicker().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (listStocks != null) {
                listStocks.clear();
                listStocks.addAll((List) results.values);
                notifyDataSetChanged();
            }
        }
    };

    static class StocksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ticker, company, price, change;
        ImageView star, logo;
        OnStocksListener onStocksListener;
        CompanyModel companyModel;

        public StocksViewHolder(@NonNull View itemView, final OnStocksListener onStocksListener) {
            super(itemView);

            price = itemView.findViewById(R.id.tv_price);
            ticker = itemView.findViewById(R.id.tv_ticker);
            company = itemView.findViewById(R.id.tv_company);
            change = itemView.findViewById(R.id.tv_change);
            star = itemView.findViewById(R.id.star);
            logo = itemView.findViewById(R.id.logo);
            this.onStocksListener = onStocksListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStocksListener.onStockClick(companyModel);
                }
            });
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStocksListener.onStarClick(companyModel);
                }
            });
        }

        @Override
        public void onClick(View v) {
            onStocksListener.onStockClick(companyModel);
            onStocksListener.onStarClick(companyModel);
        }
    }

    public interface OnStocksListener {
        void onStockClick(CompanyModel companyModel);

        void onStarClick(CompanyModel companyModel);
    }
}

