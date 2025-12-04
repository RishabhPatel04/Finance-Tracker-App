package com.example.financetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.financetracker.ui.LandingPageActivity;
import com.example.financetracker.ui.LoginActivity;
import com.example.financetracker.ui.SignUpActivity;

/**
 * MainActivity is the first screen users see if they are not logged in.
 * Displays the app name and provides options to Login or Create Account.
 * If user is already logged in (via SharedPreferences), redirects to LandingPage.
 */
public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";

    /**
     * Determines whether the user is already logged in and routes them either
     * directly to the landing page or to the initial screen with Login and
     * Create Account options.
     *
     * @param savedInstanceState previous state if the activity is re-created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            // User is logged in, go to LandingPage
            Intent intent = new Intent(this, LandingPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // User is not logged in, show initial screen
        setContentView(R.layout.activity_main_initial);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        btnCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}

