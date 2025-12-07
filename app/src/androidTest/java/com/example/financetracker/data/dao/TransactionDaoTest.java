package com.example.financetracker.data.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.entity.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TransactionDaoTest {
    private AppDatabase db;
    private TransactionDao transactionDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        transactionDao = db.transactionDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    private Transaction createSampleTransaction() {
        Transaction tx = new Transaction();
        tx.username = "user1";
        tx.category = "Food";
        tx.amount = 25.0;
        tx.description = "Groceries";
        tx.date = System.currentTimeMillis();
        tx.type = "expense";
        return tx;
    }

    @Test
    public void insertTransaction_andGetById() {
        Transaction tx = createSampleTransaction();
        long id = transactionDao.insert(tx);

        Transaction loaded = transactionDao.getById(id);
        assertNotNull("Transaction should be loaded by id", loaded);
        assertEquals("user1", loaded.username);
        assertEquals("Food", loaded.category);
        assertEquals(25.0, loaded.amount, 0.001);
    }

    @Test
    public void updateTransaction_changesPersisted() {
        Transaction tx = createSampleTransaction();
        long id = transactionDao.insert(tx);

        Transaction loaded = transactionDao.getById(id);
        assertNotNull(loaded);

        loaded.amount = 40.0;
        loaded.description = "Groceries + snacks";
        transactionDao.update(loaded);

        Transaction updated = transactionDao.getById(id);
        assertNotNull(updated);
        assertEquals(40.0, updated.amount, 0.001);
        assertEquals("Groceries + snacks", updated.description);
    }

    @Test
    public void deleteTransaction_removesRow() {
        Transaction tx = createSampleTransaction();
        long id = transactionDao.insert(tx);

        Transaction loaded = transactionDao.getById(id);
        assertNotNull(loaded);

        transactionDao.delete(loaded);

        Transaction afterDelete = transactionDao.getById(id);
        assertNull("Transaction should be null after delete", afterDelete);
    }
}
