package com.example.financetracker.data.budget;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financetracker.R;
import com.example.financetracker.data.AppDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class BudgetHomeFragment extends Fragment {
    private MonthlyLimitViewModel limitVm;
    private CategoryBudgetsViewModel catsVm;
    private CategoryBudgetAdapter adapter;

    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle b){
    return inf.inflate(R.layout.fragment_budget_home, c, false);
    }
    @Override
    public void onViewCreated(@NonNull View v, @NonNullable Bundle b){
        super.onViewCreated(v,b);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        ExecutorService io = Executor.newSingleThreadExecutor();
        BudgetRepository repo = new BudgetRepository(
                db.monthlyLimitDao(), db.categoryBudgetDao(), db.transactionDao(),io);

        limitVm = new MonthlyLimitViewModel(repo);
        catsVm = new CategoryBudgetsViewModel(repo);

        ProgressBar monthPb = v.findViewById(R.id.monthProgress);
        TextView monthLabel = v.findViewById(R.id.monthLabel);
        EditText limitInput = v.findViewById(R.id.limitInput);
        v.findViewById(R.id.saveLimitBtn).setOnClickListener(x->
                limitVm.saveLimit(limitInput.getText().toString()));

        limitVm.currentLabel.observe(getViewLifecycleOwner(), monthLabel::setText);
        limitVm.progress.observe(getViewLifecycleOwner(), monthPb :: setProgress);

        adapter = new CategoryBudgetAdapter(budget -> catsVm.remove(budget));
        RecyclerView rv = v.findViewById(R.id.budgetList);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        catsVm.rows.observe(getViewLifecycleOwner(), adapter ::submit);
        v.findViewById(R.id.addBudgetBtn).setOnClickListener(x-> showAddDialog());
    }
    private void showAddDialog(){
        var ctx = requireContext();
        LinearLayout box = new LinearLayout(ctx);
        box.setOrientation(LinearLayout.VERTICAL);
        int p = (int)(16 * ctx.getResources(). getDisplayMetrics().density);
        box.setPadding(p,p,p,0);
        EditText category = new EditText(ctx);
        category.setHint("Category(e.g., Food");
        EditText amount = new EditText(ctx);
        amount.setHint("Limit( e.g., 150.00");
        amount.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_FLAG_DECIMAL);
        box.addView(category);
        box.addView(amount);

        new androidx.appcompat.app.AlertDialog.Builder(ctx)
                .setTitle("Add / Update Budget")
                .setView(box)
                .setPositiveButton("Save", (d,w)-> catsVm.addOrUpdate(category.getText().toString(), amount.getText().toString()))
                .setNegativeButton("Cancel", null)
                .show();
    }

}
