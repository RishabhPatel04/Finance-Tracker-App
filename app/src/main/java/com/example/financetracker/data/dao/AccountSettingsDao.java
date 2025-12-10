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

    @Query("SELECT * FROM account_settings WHERE username = :username LIMIT 1")
    AccountSettings getForUser(String username);

    @Query("DELETE FROM account_settings WHERE username = :username")
    void deleteForUser(String username);

    @Query("DELETE FROM account_settings")
    void deleteAll();

    @Query("UPDATE account_settings SET username = :newUsername WHERE username = :oldUsername")
    void reassignUsername(String oldUsername, String newUsername);
}
