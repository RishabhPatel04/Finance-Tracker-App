package com.example.financetracker.data.goals;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")

public class Goal {
    @PrimaryKey(autoGenerate = true) public long id;
    @NonNull public String title = "";

    public long targetCents;
    public long progressCents;
    public long savedCents;
    public long dueDateMillis;
    public String username;
    @ColumnInfo(name = "created_at")
    public long createdAt; // e.g., System.currentTimeMillis()
}
