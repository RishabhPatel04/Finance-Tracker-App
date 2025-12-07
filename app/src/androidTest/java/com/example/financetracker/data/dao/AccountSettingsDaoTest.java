package com.example.financetracker.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

    private AccountSettings createSampleSettings(String username) {
        AccountSettings s = new AccountSettings();
        s.username = username;
        s.currency = "USD";
        s.categories = "Food,Travel";
        s.monthlyIncome = 5000.0;
        s.monthlyBudget = 3000.0;
        s.goal = "Save for vacation";
        return s;
    }

    @Test
    public void insertSettings_andGetForUser() {
        AccountSettings settings = createSampleSettings("user1");
        long id = accountSettingsDao.insert(settings);

        AccountSettings loaded = accountSettingsDao.getForUser("user1");
        assertNotNull("Settings should be loaded for user1", loaded);
        assertEquals("USD", loaded.currency);
        assertEquals("Save for vacation", loaded.goal);
    }

    @Test
    public void insertWithSameUsername_replacesExistingRow() {
        AccountSettings first = createSampleSettings("user1");
        accountSettingsDao.insert(first);

        AccountSettings updated = createSampleSettings("user1");
        updated.currency = "EUR";
        updated.monthlyBudget = 3500.0;
        accountSettingsDao.insert(updated);

        AccountSettings reloaded = accountSettingsDao.getForUser("user1");
        assertNotNull(reloaded);
        assertEquals("EUR", reloaded.currency);
        assertEquals(3500.0, reloaded.monthlyBudget, 0.001);
    }

    @Test
    public void deleteForUser_removesOnlyThatUser() {
        AccountSettings s1 = createSampleSettings("user1");
        AccountSettings s2 = createSampleSettings("user2");
        s2.currency = "CAD";
        accountSettingsDao.insert(s1);
        accountSettingsDao.insert(s2);

        accountSettingsDao.deleteForUser("user1");

        AccountSettings forUser1 = accountSettingsDao.getForUser("user1");
        AccountSettings forUser2 = accountSettingsDao.getForUser("user2");
        assertNull(forUser1);
        assertNotNull(forUser2);
        assertEquals("CAD", forUser2.currency);
    }
}
