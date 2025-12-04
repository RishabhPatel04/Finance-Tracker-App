package com.example.financetracker.ui.budget;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financetracker.R;
import com.example.financetracker.data.budget.CategoryBudget;

import java.util.ArrayList;
import java.util.List;

public class CategoryBudgetAdapter extends RecyclerView.Adapter<CategoryBudgetAdapter.VH> {
    public interface Callbacks{
        void onDelete(CategoryBudget b);
    }
    private final Callbacks cb;
    private List<CategoryBudgetsViewModel.Row> data = new ArrayList<>();

    public CategoryBudgetAdapter(Callbacks cb){
        this.cb = cb;
    }
    public void submit(List<CategoryBudgetsViewModel.Row>rows){
        data = rows == null? new ArrayList<>() : rows;
        notifyDataSetChanged();
    }
    public static class VH extends RecyclerView.ViewHolder{
        TextView category;
        TextView  amounts;
        ProgressBar progress;
        Button deleteBtn;
        VH(View v){
            super(v);
            category = v.findViewById(R.id.categoryText);
            amounts = v.findViewById(R.id.amountsText);
            progress = v.findViewById(R.id.progress);
            deleteBtn = v.findViewById(R.id.deleteBtn);

        }
    }
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_category_budget, p, false);
        return new VH(v);
    }
    @Override
    public void onBindViewHolder(@NonNull VH h, int i){
        CategoryBudgetsViewModel.Row r = data.get(i);
        //var r = data.get(i);
        h.category.setText(r.budget.category);
        h.progress.setProgress(r.progress);
        h.amounts.setText(r.label);
        h.amounts.setTextColor(r.progress >= 100 ? Color.RED: 0xFF888888);
        h.deleteBtn.setOnClickListener(v-> cb.onDelete(r.budget));
    }
    @Override
    public int getItemCount(){
        return data.size();
    }

}
