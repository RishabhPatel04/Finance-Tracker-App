package com.example.financetracker.data.budget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryBudgetsViewModel  extends ViewModel {
    private final BudgetRepository repo;
    public static class Row{
        public final CategoryBudget budget;
        public final String label;
        public final int progress;
        public Row(CategoryBudget b, long spent ){
            this.budget = b;
            this.label = String.format("$%,.2f / $%,.2f", spent/100.0, b.limitCents/100.0);
            this.progress = b.limitCents<=0?0:(int)Math.min(100, Math.round(spent*100.0/b.limitCents));
        }
    }
    public final LiveData<List<Row>> rows;

    public CategoryBudgetsViewModel(BudgetRepository repo){
        this.repo = repo;
        MediatorLiveData<List<Row>> m = new MediatorLiveData<>();
        m.setValue(Collections.emptyList());

        m.addSource(repo.observeCat(), budgets->{
            if (budgets == null){
                m.setValue(Collections.emptyList());
                return;
            }
            List<Row> tmp = new ArrayList<>();
            for (CategoryBudget b : budgets){
                LiveData<Long> s = repo.observeMonthSpentForCategory(b.category);
                m.addSource(s, spent ->{
                    List<Row> rebuilt = new ArrayList<>();
                    for (CategoryBudget bb : budgets){
                        long sp = bb.category.equals(b.category) && spent != null ? spent : 0;
                        rebuilt.add(new Row(bb, bb.category.equals(b.category)? sp:0));
                    }
                    m.setValue(rebuilt);
                });
            }
        });
        rows = m;
    }
    public void addOrUpdate(String category, String dollars) { repo.upsertCategory(category, dollars); }
    public void remove(CategoryBudget b) { repo.deleteCategory(b); }
}

