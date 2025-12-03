package com.example.financetracker.data.budget;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryBudgetDao {
    @Query("SELECT * FROM category_budgets ORDER BY category")
    LiveData<List<CategoryBudget>> observeAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsert(CategoryBudget b);

    @Delete
    void delete(CategoryBudget b);
}
