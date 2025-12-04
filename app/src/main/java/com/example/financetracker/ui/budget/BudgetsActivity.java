package com.example.financetracker.ui.budget;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;


import androidx.appcompat.app.AppCompatActivity;

import com.example.financetracker.R;

public class BudgetsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //where i add the return button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Budgets");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//
        }
        getSupportFragmentManager()
                .beginTransaction()
                        .replace(R.id.container, new BudgetHomeFragment())
                                .commit();
       // toolbar.setNavigationOnClickListener(v->
                //getOnBackPressedDispatcher().onBackPressed());


    }

    @Override
    public boolean onSupportNavigateUp(){
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

}
