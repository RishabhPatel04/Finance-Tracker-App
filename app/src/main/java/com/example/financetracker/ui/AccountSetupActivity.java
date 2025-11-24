package com.example.financetracker.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financetracker.R;

/**
 * AccountSetupActivity shown after sign-up (or first login).
 * Presents the four onboarding actions. No logic yet.
 */
public class AccountSetupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
    }
}
