package com.example.financetracker.data.budget;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "monthly_limit")
public class MonthlyLimit {

    @PrimaryKey
    @NonNull
    public String username = "";

    @ColumnInfo(name = "limit_cents")
    public long limitCents;

    public MonthlyLimit() { }

    public MonthlyLimit(@NonNull String username, long limitCents) {
        this.username = username;
        this.limitCents = limitCents;
    }
}
