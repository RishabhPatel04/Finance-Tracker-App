package com.example.financetracker.data.budget;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_budgets")
public class CategoryBudget {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "category")
    public String category="";
    @ColumnInfo(name = "limit_cents", defaultValue = "0")
    public long limitCents;

    public CategoryBudget(@NonNull String category, long limitCents){
        this.category = category.trim();
        this.limitCents = limitCents;
    }
    public CategoryBudget(){}
}
