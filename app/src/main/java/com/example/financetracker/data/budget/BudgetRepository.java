package com.example.financetracker.data.budget;

import androidx.lifecycle.LiveData;

import com.example.financetracker.data.dao.TransactionDao;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class BudgetRepository {
    private final MonthlyLimitDao limitDao;
    private final CategoryBudgetDao catDao;
    private final TransactionDao txDao;
    private final ExecutorService io;
    private final String username;
    public BudgetRepository(MonthlyLimitDao l, CategoryBudgetDao c, TransactionDao t, ExecutorService io, String username) {
        this.limitDao = l;
        this.catDao = c;
        this.txDao = t;
        this.io = io;
        this.username = username;
    }

    public LiveData<MonthlyLimit> observeLimit() {
        return limitDao.observe(username);
    }

    public LiveData<List<CategoryBudget>> observeCats() {
        return catDao.observeAll(username);
    }

    public void saveLimitDollars(String dollars) {
        long cents = toCents(dollars);
        io.execute(() -> {
            MonthlyLimit m = new MonthlyLimit(username, cents);
            limitDao.upsert(m);
        });
    }

    public void upsertCategory(String category, String dollars) {
        long cents = toCents(dollars);
        io.execute(() -> {
            CategoryBudget b = new CategoryBudget(username, category.trim(), cents);
            catDao.upsert(b);
        });
    }

    public void deleteCategory(CategoryBudget b) {
        io.execute(() -> catDao.delete(b));
    }

    public LiveData<Long> observeMonthSpent() {
        long[] r = monthRange();
        return txDao.observeMonthSpentForUser(r[0], r[1], username);
    }

    public LiveData<Long> observeMonthSpentForCategory(String category) {
        long[] r = monthRange();
        return txDao.observeMonthSpentForUserAndCategory(r[0], r[1], username, category);
    }

    private static long toCents(String d) {
        try {
            return Math.round(Double.parseDouble(d.trim()) * 100.0);
        } catch (Exception e) {
            return 0L;
        }

    }

    private static long[] monthRange() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long start = c.getTimeInMillis();
        c.add(Calendar.MONTH, 1);
        long end = c.getTimeInMillis();
        return new long[]{start, end};
    }
}

