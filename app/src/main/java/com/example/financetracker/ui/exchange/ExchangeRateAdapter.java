package com.example.financetracker.ui.exchange;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExchangeRateAdapter extends RecyclerView.Adapter<ExchangeRateAdapter.RateViewHolder> {

    public static class RateItem {
        public final String code;
        public final double rate;

        public RateItem(String code, double rate) {
            this.code = code;
            this.rate = rate;
        }
    }

    private final List<RateItem> items = new ArrayList<>();

    public void setItems(List<RateItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new RateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        RateItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RateViewHolder extends RecyclerView.ViewHolder {
        private final TextView text1;
        private final TextView text2;

        RateViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }

        void bind(RateItem item) {
            text1.setText(item.code);
            String line = String.format(Locale.getDefault(), "1 USD = %.4f %s", item.rate, item.code);
            text2.setText(line);
        }
    }
}
