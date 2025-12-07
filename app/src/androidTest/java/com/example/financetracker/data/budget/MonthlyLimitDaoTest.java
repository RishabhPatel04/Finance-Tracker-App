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
public class MonthlyLimitDaoTest {
    private AppDatabase db;
    private MonthlyLimitDao monthlyLimitDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        monthlyLimitDao = db.monthlyLimitDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void upsertLimit_insertsRow() {
        MonthlyLimit limit = new MonthlyLimit("user1", 50_000L);
        monthlyLimitDao.upsert(limit);

        SupportSQLiteDatabase sqlDb = db.getOpenHelper().getReadableDatabase();
        Cursor c = sqlDb.query("SELECT limit_cents FROM monthly_limit WHERE username = 'user1'");
        try {
            assertTrue(c.moveToFirst());
            long stored = c.getLong(0);
            assertEquals(50_000L, stored);
        } finally {
            c.close();
        }
    }

    @Test
    public void upsertLimit_overwritesExistingRow() {
        MonthlyLimit first = new MonthlyLimit("user1", 10_000L);
        monthlyLimitDao.upsert(first);

        MonthlyLimit second = new MonthlyLimit("user1", 80_000L);
        monthlyLimitDao.upsert(second);

        SupportSQLiteDatabase sqlDb = db.getOpenHelper().getReadableDatabase();
        Cursor c = sqlDb.query("SELECT limit_cents FROM monthly_limit WHERE id = 1");
        try {
            assertTrue(c.moveToFirst());
            long stored = c.getLong(0);
            assertEquals(80_000L, stored);
        } finally {
            c.close();
        }
    }

    @Test
    public void clearLimit_deletesAllRows() {
        MonthlyLimit limit = new MonthlyLimit("user1", 25_000L);
        monthlyLimitDao.upsert(limit);

        monthlyLimitDao.clearAll();

        SupportSQLiteDatabase sqlDb = db.getOpenHelper().getReadableDatabase();
        Cursor c = sqlDb.query("SELECT COUNT(*) FROM monthly_limit");
        try {
            assertTrue(c.moveToFirst());
            long count = c.getLong(0);
            assertEquals(0L, count);
        } finally {
            c.close();
        }
    }
}
