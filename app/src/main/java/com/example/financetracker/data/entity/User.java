package com.example.financetracker.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity representing an application user. This minimal model is used for
 * authentication of the demo app and can be extended later with profile fields.
 */
@Entity(tableName = "users")
public class User {
    /**
     * surrogate key
     */
    @PrimaryKey(autoGenerate = true)
    public long userId;
    /**
     * unique username
     */
    public String username;
    /**
     * email address (optional for login, required at sign-up per UI)
     */
    public String email;
    /**
     * plain text for demo; replace with a hashed value in production
     */
    public String password;
    /**
     * simple role flag for future use
     */
    public boolean isAdmin;
}
