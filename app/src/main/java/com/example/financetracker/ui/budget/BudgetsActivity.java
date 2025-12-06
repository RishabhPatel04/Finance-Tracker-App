package com.example.financetracker.ui.budget;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.financetracker.R;

public class BudgetsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_home);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new BudgetHomeFragment())
                .commit();

    }
}
