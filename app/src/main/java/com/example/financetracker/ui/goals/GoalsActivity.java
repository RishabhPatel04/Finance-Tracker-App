package com.example.financetracker.ui.goals;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.financetracker.R;

public class GoalsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_home);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Goals");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v->
                getOnBackPressedDispatcher().onBackPressed());

        findViewById(R.id.btnCreateGoal).setOnClickListener(v-> GoalsDialogs.showCreate(this));
        findViewById(R.id.btnUpdateGoal).setOnClickListener(v-> GoalsDialogs.showUpdate(this));
        findViewById(R.id.btnRemoveGoal).setOnClickListener(v-> GoalsDialogs.showRemove(this));


    }
    //@Override
    public boolean onSupportNegativeUp(){
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
