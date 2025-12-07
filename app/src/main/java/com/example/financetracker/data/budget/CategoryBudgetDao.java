package com.example.financetracker.data.budget;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryBudgetDao {
    @Query("SELECT * FROM category_budgets WHERE username = :username ORDER BY category")
    LiveData<List<CategoryBudget>> observeAll(String username);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsert(CategoryBudget b);

    @Delete
    void delete(CategoryBudget b);

    @Query("UPDATE category_budgets SET username = :newUsername WHERE username = :oldUsername")
    void reassignUsername(String oldUsername, String newUsername);
}
