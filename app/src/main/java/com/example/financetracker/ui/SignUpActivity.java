package com.example.financetracker.ui;

import android.os.Bundle;
import android.text.TextUtils;
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
 * SignUpActivity allows users to register a new account in the local Room DB.
 * It checks for empty input and prevents duplicate usernames.
 */
public class SignUpActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Initializes the UI and handles the create-account action.
     * On success the activity finishes and returns to Login.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText email = findViewById(R.id.etEmail);
        EditText username = findViewById(R.id.etUsername);
        EditText password = findViewById(R.id.etPassword);
        Button create = findViewById(R.id.btnCreateAccount);

        create.setOnClickListener(v -> {
            String e = email.getText().toString().trim();
            String u = username.getText().toString().trim();
            String p = password.getText().toString();
            if (TextUtils.isEmpty(e) || TextUtils.isEmpty(u) || TextUtils.isEmpty(p)) {
                Toast.makeText(this, "Enter email, username and password", Toast.LENGTH_SHORT).show();
                return;
            }
            executor.execute(() -> {
                if (AppDatabase.getInstance(getApplicationContext()).userDao().countByUsername(u) > 0) {
                    runOnUiThread(() -> Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show());
                } else {
                    User user = new User();
                    user.email = e;
                    user.username = u;
                    user.password = p;
                    user.isAdmin = false;
                    AppDatabase.getInstance(getApplicationContext()).userDao().insert(user);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Account created. Complete setup.", Toast.LENGTH_LONG).show();
                        startActivity(new android.content.Intent(this, AccountSetupActivity.class));
                        finish();
                    });
                }
            });
        });
    }
}

