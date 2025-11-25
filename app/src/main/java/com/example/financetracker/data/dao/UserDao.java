package com.example.financetracker.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.financetracker.data.entity.User;

import java.util.List;

/**
 * Data-access object for {@link User} records.
 */
@Dao
public interface UserDao {
    /**
     * Inserts a new user. This will abort if the primary key conflicts.
     *
     * @param user entity to insert
     * @return row ID of the newly inserted user
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(User user);

    /**
     * Retrieves a user by unique username.
     *
     * @param username unique username
     * @return {@link User} or null if not found
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getByUsername(String username);

    /**
     * Returns the number of users with the given username.
     * Useful for existence checks during sign-up.
     *
     * @param username username to check
     * @return count of matching users (0 or 1)
     */
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countByUsername(String username);

    /**
     * Retrieves all users from the database.
     * Used by admin for user management.
     *
     * @return list of all users
     */
    @Query("SELECT * FROM users ORDER BY username")
    List<User> getAllUsers();

    /**
     * Updates an existing user.
     *
     * @param user user entity to update
     */
    @Update
    void update(User user);

    /**
     * Deletes a user from the database.
     *
     * @param user user entity to delete
     */
    @Delete
    void delete(User user);

    /**
     * Retrieves a user by user ID.
     *
     * @param userId user ID
     * @return User or null if not found
     */
    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    User getById(long userId);
}
