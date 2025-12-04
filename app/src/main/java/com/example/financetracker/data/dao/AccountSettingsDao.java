package com.example.financetracker.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.financetracker.data.entity.AccountSettings;

import java.util.List;

@Dao
public interface AccountSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AccountSettings settings);

    @Query("SELECT * FROM account_settings ORDER BY id DESC")
    List<AccountSettings> getAll();
}
