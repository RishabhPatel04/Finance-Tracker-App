package com.example.financetracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.financetracker.data.entity.Transaction;

import java.util.List;

/**
 * Data-access object for {@link Transaction} records.
 */
@Dao
public interface TransactionDao {
    /**
     * Inserts a new transaction.
     *
     * @param transaction entity to insert
     * @return row ID of the newly inserted transaction
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Transaction transaction);

    /**
     * Updates an existing transaction.
     *
     * @param transaction entity to update
     */
    @Update
    void update(Transaction transaction);

    /**
     * Deletes a transaction.
     *
     * @param transaction entity to delete
     */
    @Delete
    void delete(Transaction transaction);

    /**
     * Retrieves all transactions for a specific user.
     *
     * @param username username to filter by
     * @return list of transactions for the user, ordered by date (newest first)
     */
    @Query("SELECT * FROM transactions WHERE username = :username ORDER BY date DESC")
    List<Transaction> getTransactionsByUser(String username);

    LiveData<Long>ObserveMonthSpent(long start, long end);
    /**
     * Retrieves all transactions (for admin users).
     *
     * @return list of all transactions, ordered by date (newest first)
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<Transaction> getAllTransactions();

    /**
     * Searches transactions by description or category for a specific user.
     *
     * @param username username to filter by
     * @param searchQuery search term
     * @return list of matching transactions
     */
    @Query("SELECT * FROM transactions WHERE username = :username AND (description LIKE '%' || :searchQuery || '%' OR category LIKE '%' || :searchQuery || '%') ORDER BY date DESC")
    List<Transaction> searchTransactionsByUser(String username, String searchQuery);

    /**
     * Searches all transactions by description or category (for admin users).
     *
     * @param searchQuery search term
     * @return list of matching transactions
     */
    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :searchQuery || '%' OR category LIKE '%' || :searchQuery || '%' ORDER BY date DESC")
    List<Transaction> searchAllTransactions(String searchQuery);

    /**
     * Retrieves a transaction by ID.
     *
     * @param transactionId transaction ID
     * @return Transaction or null if not found
     */
    @Query("SELECT * FROM transactions WHERE transactionId = :transactionId LIMIT 1")
    Transaction getById(long transactionId);

    LiveData<Long> observeMonthSpentForCategory(long stat, long end, String category);
}

