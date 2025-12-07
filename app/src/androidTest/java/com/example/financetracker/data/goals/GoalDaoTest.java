package com.example.financetracker.data.goals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.financetracker.data.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GoalDaoTest {
    private AppDatabase db;
    private GoalDao goalDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        goalDao = db.goalDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    private Goal createSampleGoal() {
        Goal g = new Goal();
        g.title = "Buy a car";
        g.targetCents = 500_000L;
        g.progressCents = 100_000L;
        g.savedCents = 100_000L;
        g.dueDateMillis = System.currentTimeMillis() + 30L * 24L * 60L * 60L * 1000L;
        g.username = "user1";
        g.createdAt = System.currentTimeMillis();
        return g;
    }

    @Test
    public void insertGoal_andGetById() {
        Goal goal = createSampleGoal();
        long id = goalDao.insert(goal);

        Goal loaded = goalDao.getById(id);
        assertNotNull("Goal should be loaded by id", loaded);
        assertEquals("Buy a car", loaded.title);
        assertEquals(500_000L, loaded.targetCents);
    }

    @Test
    public void updateGoal_changesPersisted() {
        Goal goal = createSampleGoal();
        long id = goalDao.insert(goal);

        Goal loaded = goalDao.getById(id);
        assertNotNull(loaded);

        loaded.progressCents = 300_000L;
        loaded.savedCents = 300_000L;
        goalDao.update(loaded);

        Goal updated = goalDao.getById(id);
        assertNotNull(updated);
        assertEquals(300_000L, updated.progressCents);
        assertEquals(300_000L, updated.savedCents);
    }

    @Test
    public void deleteGoal_removesRow() {
        Goal goal = createSampleGoal();
        long id = goalDao.insert(goal);

        Goal loaded = goalDao.getById(id);
        assertNotNull(loaded);

        goalDao.delete(loaded);

        Goal afterDelete = goalDao.getById(id);
        assertNull("Goal should be null after delete", afterDelete);
    }
}
