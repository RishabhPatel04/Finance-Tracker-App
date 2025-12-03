package com.example.financetracker.data.budget;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_budgets")
public class CategoryBudget {
    @PrimaryKey(autoGenerate = true) public long id;
    public String category;
    public long limitCents;
}
