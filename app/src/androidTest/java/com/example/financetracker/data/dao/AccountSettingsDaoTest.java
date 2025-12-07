package com.example.financetracker.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.entity.AccountSettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AccountSettingsDaoTest {
    private AppDatabase db;
    private AccountSettingsDao accountSettingsDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        accountSettingsDao = db.accountSettingsDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    private AccountSettings createSampleSettings() {
        AccountSettings s = new AccountSettings();
        s.currency = "USD";
        s.categories = "Food,Travel";
        s.monthlyIncome = 5000.0;
        s.monthlyBudget = 3000.0;
        s.goal = "Save for vacation";
        return s;
    }

    @Test
    public void insertSettings_andGetAll() {
        AccountSettings settings = createSampleSettings();
        long id = accountSettingsDao.insert(settings);

        List<AccountSettings> all = accountSettingsDao.getAll();
        assertEquals(1, all.size());
        AccountSettings loaded = all.get(0);
        assertNotNull(loaded);
        assertEquals("USD", loaded.currency);
        assertEquals("Save for vacation", loaded.goal);
    }

    @Test
    public void insertWithSameId_replacesExistingRow() {
        AccountSettings first = createSampleSettings();
        long id = accountSettingsDao.insert(first);

        AccountSettings updated = createSampleSettings();
        updated.id = id;
        updated.currency = "EUR";
        updated.monthlyBudget = 3500.0;
        accountSettingsDao.insert(updated);

        List<AccountSettings> all = accountSettingsDao.getAll();
        assertEquals(1, all.size());
        AccountSettings reloaded = all.get(0);
        assertEquals("EUR", reloaded.currency);
        assertEquals(3500.0, reloaded.monthlyBudget, 0.001);
    }

    @Test
    public void deleteAll_clearsTable() {
        AccountSettings settings = createSampleSettings();
        accountSettingsDao.insert(settings);

        accountSettingsDao.deleteAll();

        List<AccountSettings> all = accountSettingsDao.getAll();
        assertEquals(0, all.size());
    }
}
