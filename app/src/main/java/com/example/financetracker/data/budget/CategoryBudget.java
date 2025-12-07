package com.example.financetracker.data.budget;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "category_budgets", primaryKeys = {"username", "category"})
public class CategoryBudget {
    @NonNull
    @ColumnInfo(name = "username")
    public String username = "";

    @NonNull
    @ColumnInfo(name = "category")
    public String category = "";

    @ColumnInfo(name = "limit_cents", defaultValue = "0")
    public long limitCents;

    public CategoryBudget(@NonNull String username, @NonNull String category, long limitCents){
        this.username = username.trim();
        this.category = category.trim();
        this.limitCents = limitCents;
    }
    public CategoryBudget(){}
}
