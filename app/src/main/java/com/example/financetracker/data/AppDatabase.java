package com.example.financetracker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.financetracker.data.budget.CategoryBudget;
import com.example.financetracker.data.budget.CategoryBudgetDao;
import com.example.financetracker.data.dao.TransactionDao;
import com.example.financetracker.data.dao.UserDao;
import com.example.financetracker.data.entity.Transaction;
import com.example.financetracker.data.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, CategoryBudget.class,Transaction.class}, version = 3, exportSchema = false)

/**
 * AppDatabase is the Room database entry point for the application.
 * It currently exposes only the {@link UserDao} and stores the DB as
 * a single-instance using the application context.
 */
public abstract class AppDatabase extends RoomDatabase {
    public abstract CategoryBudgetDao categoryBudgetDao();
    //public abstract  com.example.financetracker.data.budget.CategoryBudgetDao categoryBudgetDao();
    public abstract  com.example.financetracker.data.budget.MonthlyLimitDao monthlyLimitDao();

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract TransactionDao transactionDao();

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
                     .addCallback(new RoomDatabase.Callback() {
                         @Override
                         public void onCreate(SupportSQLiteDatabase db) {
                             super.onCreate(db);
                             // Seed predefined users will be done on first access
                         }
                     })
                     .build();
                    // Seed predefined users after database is created
                    seedPredefinedUsers(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Seeds the database with predefined users if they don't already exist.
     */
    private static void seedPredefinedUsers(Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            UserDao userDao = INSTANCE.userDao();
            
            // Check and create testuser1 (not admin)
            if (userDao.countByUsername("testuser1") == 0) {
                User testUser = new User();
                testUser.username = "testuser1";
                testUser.password = "testuser1";
                testUser.email = "testuser1@example.com";
                testUser.isAdmin = false;
                userDao.insert(testUser);
            }
            
            // Check and create admin2 (admin)
            if (userDao.countByUsername("admin2") == 0) {
                User adminUser = new User();
                adminUser.username = "admin2";
                adminUser.password = "admin2";
                adminUser.email = "admin2@example.com";
                adminUser.isAdmin = true;
                userDao.insert(adminUser);
            }
        });
    }
}

