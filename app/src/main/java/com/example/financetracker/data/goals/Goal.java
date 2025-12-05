package com.example.financetracker.data.goals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")

public class Goal {
    @PrimaryKey(autoGenerate = true) public long id;
    public String title;
    public long targetCents;
    public long progressCents;
    public long dueDateMillis;
    public String username;
}
