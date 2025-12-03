package com.example.financetracker.data.budget;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "monthly_limit")
public class MonthlyLimit {

    @PrimaryKey
    public int id = 1;
    public long limitCents;
}
