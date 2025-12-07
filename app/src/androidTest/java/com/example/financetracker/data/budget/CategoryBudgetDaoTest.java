package com.example.financetracker.data.budget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;

import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.financetracker.data.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CategoryBudgetDaoTest {
    private AppDatabase db;
    private CategoryBudgetDao categoryBudgetDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        categoryBudgetDao = db.categoryBudgetDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertCategoryBudget_persistsRow() {
        CategoryBudget b = new CategoryBudget("Food", 10_000L);
        categoryBudgetDao.upsert(b);

        SupportSQLiteDatabase sqlDb = db.getOpenHelper().getReadableDatabase();
        Cursor c = sqlDb.query("SELECT COUNT(*) FROM category_budgets WHERE category = 'Food'");
        try {
            assertTrue(c.moveToFirst());
            long count = c.getLong(0);
            assertEquals(1L, count);
        } finally {
            c.close();
        }
    }

    @Test
    public void upsertCategoryBudget_updatesExistingRow() {
        CategoryBudget b1 = new CategoryBudget("Travel", 5_000L);
        categoryBudgetDao.upsert(b1);

        CategoryBudget b2 = new CategoryBudget("Travel", 20_000L);
        categoryBudgetDao.upsert(b2);

        SupportSQLiteDatabase sqlDb = db.getOpenHelper().getReadableDatabase();
        Cursor c = sqlDb.query("SELECT limit_cents FROM category_budgets WHERE category = 'Travel'");
        try {
            assertTrue(c.moveToFirst());
            long limit = c.getLong(0);
            assertEquals(20_000L, limit);
        } finally {
            c.close();
        }
    }

    @Test
    public void deleteCategoryBudget_removesRow() {
        CategoryBudget b = new CategoryBudget("Utilities", 15_000L);
        categoryBudgetDao.upsert(b);

        categoryBudgetDao.delete(b);

        SupportSQLiteDatabase sqlDb = db.getOpenHelper().getReadableDatabase();
        Cursor c = sqlDb.query("SELECT COUNT(*) FROM category_budgets WHERE category = 'Utilities'");
        try {
            assertTrue(c.moveToFirst());
            long count = c.getLong(0);
            assertEquals(0L, count);
        } finally {
            c.close();
        }
    }
}
