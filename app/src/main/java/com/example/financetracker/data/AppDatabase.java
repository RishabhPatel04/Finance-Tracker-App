package com.example.financetracker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.financetracker.data.dao.UserDao;
import com.example.financetracker.data.entity.User;

@Database(entities = {User.class}, version = 2, exportSchema = false)
/**
 * AppDatabase is the Room database entry point for the application.
 * It currently exposes only the {@link UserDao} and stores the DB as
 * a single-instance using the application context.
 */
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    /**
     * Returns a singleton instance of the database. The instance is created lazily
     * and synchronized to be thread-safe.
     *
     * @param context application context used by Room
     * @return singleton {@link AppDatabase}
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "finance_tracker.db"
                    ).fallbackToDestructiveMigration()
                     .build();
                }
            }
        }
        return INSTANCE;
    }
}

