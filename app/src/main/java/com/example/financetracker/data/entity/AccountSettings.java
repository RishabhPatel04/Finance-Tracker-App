package com.example.financetracker.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account_settings")
public class AccountSettings {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String currency;

    public String categories;

    public double monthlyIncome;

    public double monthlyBudget;

    public String goal;
}
