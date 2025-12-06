package com.example.financetracker.data.budget;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "monthly_limit")
public class MonthlyLimit {

    @PrimaryKey
    public int id = 1;
    @ColumnInfo(name = "limit_cents")
    public long limitCents;
    public MonthlyLimit(){ }

    public MonthlyLimit(long limitCents){
        this.id = 1;
        this.limitCents = limitCents;
    }
}
