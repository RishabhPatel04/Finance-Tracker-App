package com.example.financetracker.ui.budget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.financetracker.data.budget.BudgetRepository;
import com.example.financetracker.data.budget.MonthlyLimit;

public class MonthlyLimitViewModel extends ViewModel {
    private final BudgetRepository repo;

    public final LiveData<String> currentLabel;
    public final LiveData<Integer> progress;

    public MonthlyLimitViewModel(BudgetRepository repo) {
        this.repo = repo;

        LiveData<MonthlyLimit> limit = repo.observerLimit();
        LiveData<Long> spent = repo.observeMonthSpent();
        MediatorLiveData<String> label = new MediatorLiveData<>();
        MediatorLiveData<Integer> prog = new MediatorLiveData<>();

        final long[] L = {0};
        final long[] S = {0};

        label.setValue("$0.00/$0.00");
        prog.setValue(0);

        label.addSource(limit, m1 -> {
            L[0] = (m1 == null ? 0 : m1.limitCents);
            label.setValue(fmt(S[0]) + " / " + fmt(L[0]));
            prog.setValue(pct(S[0], L[0]));
        });
        label.addSource(spent, s->{
            S[0]=(s == null ? 0: s);
            label.setValue(fmt(S[0]) + " / " + fmt(L[0]));
            prog.setValue(pct(S[0], L[0]));

        });
        currentLabel = label;
        progress = prog;

    }
    public void saveLimit(String dollars){
        repo.saveLimitDollars(dollars);
    }
    private static String fmt(long cents){
        return String.format("$%,.2f", cents/100.0);
    }

    private static int pct(long spent, long limit){
        return limit <=0?0: (int)Math.min(100, Math.round(spent*100.0/limit));
    }
}
