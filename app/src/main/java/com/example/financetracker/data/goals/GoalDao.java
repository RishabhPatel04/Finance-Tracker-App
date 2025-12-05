package com.example.financetracker.data.goals;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public class GoalDao {
    @Insert long insert(Goal g);
    @Update
    void update(Goal g);
    @Delete void delete(Goal g);

    @Query("SELECT * FROM goals WHERE username=:username ORDER BY dueDateMillis")
    List<Goal> listForUser(String username);

    @Query("SELECT * FROM goals WHERE id=:id LIMIT 1")
    Goal getById(long id);
}
