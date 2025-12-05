// com.example.financetracker.data.goals.GoalDao.java
package com.example.financetracker.data.goals;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Goal goal);

    @Update
    void update(Goal goal);

    @Delete
    void delete(Goal goal);

    @Query("SELECT * FROM goals ORDER BY created_at DESC")
    LiveData<List<Goal>> observeAll();

    @Query("SELECT * FROM goals ORDER BY created_at DESC")
    List<Goal> listNow();

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    Goal getById(long id);
}
