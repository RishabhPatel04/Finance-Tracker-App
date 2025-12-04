package com.example.financetracker.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financetracker.R;

import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.entity.AccountSettings;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.MultiAutoCompleteTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AccountSetupActivity shown after sign-up (or first login).
 * Presents the four onboarding actions. No logic yet.
 */
public class AccountSetupActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Initializes the account setup UI, including currency, category, income,
     * budget, and goal inputs, and wires up the save behavior to persist
     * selections into Room.
     *
     * @param savedInstanceState previous state if the activity is re-created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        Spinner spCurrency = findViewById(R.id.spCurrency);
        MultiAutoCompleteTextView tvCategories = findViewById(R.id.multiCategories);
        EditText etIncome = findViewById(R.id.etMonthlyIncome);
        EditText etBudget = findViewById(R.id.etMonthlyBudget);
        EditText etGoal = findViewById(R.id.etGoal);
        Button btnSave = findViewById(R.id.btnSaveSetup);

        // Currency spinner
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(
                this, R.array.currencies, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurrency.setAdapter(currencyAdapter);

        // Categories multi-select dialog
        String[] categories = getResources().getStringArray(R.array.categories);
        boolean[] selectedCategories = new boolean[categories.length];
        tvCategories.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        btnSave.setOnClickListener(v -> {
            String incomeStr = etIncome.getText().toString().trim();
            String budgetStr = etBudget.getText().toString().trim();
            String currency = (String) spCurrency.getSelectedItem();
            String categoriesSelected = tvCategories.getText().toString().trim();
            String goal = etGoal.getText().toString().trim();

            // Minimal validation: numeric-only for income and budget
            if (!incomeStr.matches("\\d+") || !budgetStr.matches("\\d+")) {
                Toast.makeText(this, "Income and Budget must be numbers", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Persist these selections in Room (future step)
            executor.execute(() -> {
                AccountSettings settings = new AccountSettings();
                settings.currency = currency;
                settings.categories = categoriesSelected;
                settings.monthlyIncome = Double.parseDouble(incomeStr);
                settings.monthlyBudget = Double.parseDouble(budgetStr);
                settings.goal = goal;

                AppDatabase.getInstance(getApplicationContext())
                        .accountSettingsDao()
                        .insert(settings);
            });
            Toast.makeText(this, "Saved: " + currency + ", categories: " + categoriesSelected, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
