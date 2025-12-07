package com.example.financetracker.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account_settings")
public class AccountSettings {
    @PrimaryKey
    @NonNull
    public String username = "";

    public String currency;

    public String categories;

    public double monthlyIncome;

    public double monthlyBudget;

    public String goal;
}
