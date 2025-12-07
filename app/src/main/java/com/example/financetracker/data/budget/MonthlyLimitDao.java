package com.example.financetracker.data.budget;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MonthlyLimitDao {
    @Query("SELECT * FROM monthly_limit WHERE id = 1 LIMIT 1")
    LiveData<MonthlyLimit> observe();

    @Query("SELECT limit_cents FROM monthly_limit WHERE id = 1 LIMIT 1")
    Long currentLimitCents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(MonthlyLimit m);

    @Query("DELETE FROM monthly_limit")
    void clear();
}
