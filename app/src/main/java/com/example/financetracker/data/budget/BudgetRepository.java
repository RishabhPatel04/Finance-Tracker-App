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


    public BudgetRepository(MonthlyLimitDao 1, MonthlyLimitDao monthlyLimitDao, CategoryBudgetDao c, TransactionDao t, ExecutorService io) {
        this.limitDao = 1;
        this.catDao = c;
        this.txDao = t;
        this.io = io;
    }

    public LiveData<MonthlyLimit>observerLimit(){
        return limitDao.observe();
    }
    public LiveData<List<CategoryBudget>> observeCat(){
        return catDao.observeAll();
    }

    public void saveLimitDollars(String dollars){
        long cents = toCents(dollars);
        io.execute(()->{
            MonthlyLimit m = new MonthlyLimit();
            m.limitCents = cents;
            limitDao.upsert(m);
        });
    }
    public void upsertCategory(String category, String dollars){
        long cents = toCents(dollars);
        io.execute(()->{
            CategoryBudget b = new CategoryBudget();
            b.category = category.trim();
            b.limitCents = cents;
            catDao.upsert(b);
        });
    }
    public void deleteCategory(CategoryBudget b){
        io.execute(()-> catDao.delete(b));
    }
    public LiveData<Long> observeMonthSpent(){
        long[] r = monthRange();
        return txDao.observeMonthSpent(r[0], r[1]);
    }
    public LiveData<Long> observeMonthSpentForCategory(String category){
        long[] r = monthRange();
        return txDao.observeMonthSpentForCategory(r[0], r[1], category);
    }
    private static long toCents(String d){
        try{return Math.round(Double.parseDouble(d.trim())*100);
    }catch (Exception e){
            return 0;
        }
        private static long[] monthRange() {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1); c.set(Calendar.HOUR_OF_DAY,0);
            c.set(Calendar.MINUTE,0); c.set(Calendar.SECOND,0); c.set(Calendar.MILLISECOND,0);
            long start = c.getTimeInMillis(); c.add(Calendar.MONTH,1); long end = c.getTimeInMillis();
            return new long[]{start,end};
        }

}

