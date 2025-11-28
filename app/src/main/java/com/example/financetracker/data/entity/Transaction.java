package com.example.financetracker.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Room entity representing a financial transaction.
 */
@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public long transactionId;
    
    /**
     * Username of the user who created this transaction
     */
    public String username;
    
    /**
     * Transaction category (e.g., "Mortgage/rent", "Food", "Utilities")
     */
    public String category;
    
    /**
     * Transaction amount (positive for income, negative for expenses)
     */
    public double amount;
    
    /**
     * Transaction description/note
     */
    public String description;
    
    /**
     * Date of the transaction
     */
    public long date; // Stored as timestamp
    
    /**
     * Transaction type: "income" or "expense"
     */
    public String type;
}

