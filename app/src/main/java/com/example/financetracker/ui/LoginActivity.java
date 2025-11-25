package com.example.financetracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.financetracker.R;
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LoginActivity presents the landing screen where users authenticate.
 * It validates input locally, then queries Room for the stored user.
 * If the account is missing, it prompts the user to create one.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_ADMIN = "isAdmin";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    /**
     * Initializes the UI and wires up click listeners for login and navigation
     * to the sign-up screen.
     *
     * @param savedInstanceState previous state if re-created
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.etUsername);
        EditText password = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGotoSignUp = findViewById(R.id.btnGotoSignUp);

        btnGotoSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = username.getText().toString().trim();
                String p = password.getText().toString();
                if (TextUtils.isEmpty(u) || TextUtils.isEmpty(p)) {
                    Toast.makeText(LoginActivity.this, "Enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                executor.execute(() -> {
                    User user = AppDatabase.getInstance(getApplicationContext()).userDao().getByUsername(u);
                    runOnUiThread(() -> {
                        if (user == null) {
                            Toast.makeText(LoginActivity.this, "Account not found. Please create an account.", Toast.LENGTH_LONG).show();
                        } else if (!p.equals(user.password)) {
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Save login state to SharedPreferences
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(KEY_IS_LOGGED_IN, true);
                            editor.putString(KEY_USERNAME, user.username);
                            editor.putBoolean(KEY_IS_ADMIN, user.isAdmin);
                            editor.apply();

                            // Navigate to LandingPage
                            Intent i = new Intent(LoginActivity.this, LandingPageActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }
                    });
                });
            }
        });
    }
}

