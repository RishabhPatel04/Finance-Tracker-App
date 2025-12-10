package com.example.financetracker.data.budget;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MonthlyLimitDao {
    @Query("SELECT * FROM monthly_limit WHERE username = :username LIMIT 1")
    LiveData<MonthlyLimit> observe(String username);

    @Query("SELECT limit_cents FROM monthly_limit WHERE username = :username LIMIT 1")
    Long currentLimitCents(String username);

    @Query("SELECT limit_cents FROM monthly_limit WHERE id = 1 LIMIT 1")
    Long currentLimitCents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(MonthlyLimit m);

    @Query("DELETE FROM monthly_limit WHERE username = :username")
    void clearForUser(String username);

    @Query("DELETE FROM monthly_limit")
    void clearAll();

    @Query("UPDATE monthly_limit SET username = :newUsername WHERE username = :oldUsername")
    void reassignUsername(String oldUsername, String newUsername);
}
