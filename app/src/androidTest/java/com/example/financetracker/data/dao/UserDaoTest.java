package com.example.financetracker.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.entity.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserDaoTest {
    private AppDatabase db;
    private UserDao userDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertUser_andGetByUsername() {
        User user = new User();
        user.username = "alice";
        user.email = "alice@example.com";
        user.password = "password";
        user.isAdmin = false;

        long id = userDao.insert(user);
        assertNotNull("Insert should return a row id", id);

        User loaded = userDao.getByUsername("alice");
        assertNotNull("User should be found by username", loaded);
        assertEquals("Username should match", "alice", loaded.username);
        assertEquals("Email should match", "alice@example.com", loaded.email);
    }

    @Test
    public void updateUser_changesPersisted() {
        User user = new User();
        user.username = "bob";
        user.email = "old@example.com";
        user.password = "password";
        user.isAdmin = false;

        long id = userDao.insert(user);
        User loaded = userDao.getById(id);
        assertNotNull(loaded);

        loaded.email = "new@example.com";
        loaded.isAdmin = true;
        userDao.update(loaded);

        User updated = userDao.getById(id);
        assertNotNull(updated);
        assertEquals("new@example.com", updated.email);
        assertEquals(true, updated.isAdmin);
    }

    @Test
    public void deleteUser_removesRow() {
        User user = new User();
        user.username = "charlie";
        user.email = "charlie@example.com";
        user.password = "password";
        user.isAdmin = false;

        long id = userDao.insert(user);
        User loaded = userDao.getById(id);
        assertNotNull(loaded);

        userDao.delete(loaded);

        User afterDelete = userDao.getById(id);
        assertNull("User should be null after delete", afterDelete);
    }
}
