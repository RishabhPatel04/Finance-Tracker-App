package com.example.financetracker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.financetracker.R;
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.dao.UserDao;
import com.example.financetracker.data.entity.AccountSettings;
import com.example.financetracker.data.entity.User;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private static final String KEY_USERNAME = "username";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private Spinner spCurrency;
    private MultiAutoCompleteTextView tvCategories;

    private String originalUsername;
    private String[] categories;
    private boolean[] selectedCategories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spCurrency = findViewById(R.id.spCurrencySettings);
        tvCategories = findViewById(R.id.multiCategoriesSettings);
        Button btnSave = findViewById(R.id.btnSaveSettings);
        android.widget.ImageButton btnBack = findViewById(R.id.btnBackSettings);

        // Currency spinner
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(
                this, R.array.currencies, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurrency.setAdapter(currencyAdapter);

        // Categories multi-select using the same dropdown options as account setup.
        categories = getResources().getStringArray(R.array.categories);
        selectedCategories = new boolean[categories.length];

        tvCategories.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Select Categories");
            builder.setMultiChoiceItems(categories, selectedCategories, (dialog, which, isChecked) -> {
                selectedCategories[which] = isChecked;
            });
            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder selected = new StringBuilder();
                for (int i = 0; i < categories.length; i++) {
                    if (selectedCategories[i]) {
                        if (selected.length() > 0) selected.append(", ");
                        selected.append(categories[i]);
                    }
                }
                tvCategories.setText(selected.toString());
            });
            builder.setNeutralButton("Select All / None", (dialog, which) -> {
                android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialog;
                boolean allSelected = true;
                for (boolean sel : selectedCategories) {
                    if (!sel) {
                        allSelected = false;
                        break;
                    }
                }
                boolean newState = !allSelected;
                android.widget.ListView listView = alertDialog.getListView();
                for (int i = 0; i < selectedCategories.length; i++) {
                    selectedCategories[i] = newState;
                    listView.setItemChecked(i, newState);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        originalUsername = prefs.getString(KEY_USERNAME, "");

        loadCurrentData();

        btnSave.setOnClickListener(v -> saveSettings());

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void loadCurrentData() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            UserDao userDao = db.userDao();

            User user = userDao.getByUsername(originalUsername);
            AccountSettings settings = db.accountSettingsDao().getForUser(originalUsername);

            runOnUiThread(() -> {
                if (user != null) {
                    etUsername.setText(user.username);
                    etEmail.setText(user.email);
                    etPassword.setText(user.password);
                }

                if (settings != null) {
                    if (!TextUtils.isEmpty(settings.currency)) {
                        String[] currencies = getResources().getStringArray(R.array.currencies);
                        for (int i = 0; i < currencies.length; i++) {
                            if (currencies[i].equalsIgnoreCase(settings.currency)) {
                                spCurrency.setSelection(i);
                                break;
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(settings.categories)) {
                        tvCategories.setText(settings.categories);

                        if (categories != null && selectedCategories != null) {
                            // Pre-select any categories that were previously chosen
                            String[] chosen = settings.categories.split(",");
                            for (int i = 0; i < categories.length; i++) {
                                selectedCategories[i] = false;
                                String cat = categories[i];
                                for (String c : chosen) {
                                    if (cat.equalsIgnoreCase(c.trim())) {
                                        selectedCategories[i] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });
        });
    }

    private void saveSettings() {
        final String newUsername = etUsername.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String currency = (String) spCurrency.getSelectedItem();
        final String categoriesText = tvCategories.getText().toString().trim();

        if (TextUtils.isEmpty(newUsername) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Username, email, and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            UserDao userDao = db.userDao();

            User user = userDao.getByUsername(originalUsername);
            if (user == null) {
                runOnUiThread(() -> Toast.makeText(this, "User record not found", Toast.LENGTH_SHORT).show());
                return;
            }

            // If username changed, ensure it's unique
            if (!newUsername.equals(originalUsername)) {
                int count = userDao.countByUsername(newUsername);
                if (count > 0) {
                    runOnUiThread(() -> Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show());
                    return;
                }
            }

            // Update User fields
            user.username = newUsername;
            user.email = email;
            user.password = password;
            userDao.update(user);

            // Reassign username across related tables if it changed
            if (!newUsername.equals(originalUsername)) {
                db.transactionDao().reassignUsername(originalUsername, newUsername);
                db.goalDao().reassignUsername(originalUsername, newUsername);
                db.monthlyLimitDao().reassignUsername(originalUsername, newUsername);
                db.categoryBudgetDao().reassignUsername(originalUsername, newUsername);
                db.accountSettingsDao().reassignUsername(originalUsername, newUsername);
            }

            // Upsert AccountSettings for this user
            AccountSettings existing = db.accountSettingsDao().getForUser(newUsername);
            if (existing == null) {
                existing = new AccountSettings();
                existing.username = newUsername;
            }
            existing.currency = currency;
            existing.categories = categoriesText;
            db.accountSettingsDao().insert(existing);

            // Update shared preferences
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_USERNAME, newUsername);
            editor.putString("preferred_categories_" + newUsername, categoriesText);
            if (!newUsername.equals(originalUsername)) {
                editor.remove("preferred_categories_" + originalUsername);
            }
            editor.apply();

            runOnUiThread(() -> {
                Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
                originalUsername = newUsername;
                finish();
            });
        });
    }
}
