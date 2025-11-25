package com.example.financetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * MainActivity is the authenticated landing screen (dashboard) of the app.
 * Displays financial summary, spending by category, and quick action buttons.
 */
public class MainActivity extends AppCompatActivity {
    private TextView tvMonthYear;
    private TextView tvIncome;
    private TextView tvExpenses;
    private TextView tvBudget;
    private TextView tvTotalSpent;
    private EditText etSearch;
    private DonutChartView donutChart;
    private View llSpendingChart;
    private TextView tvSpendingByCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupMonthYear();
        setupClickListeners();
        loadDashboardData();
    }

    /**
     * Initializes all view references from the layout.
     */
    private void initializeViews() {
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpenses = findViewById(R.id.tvExpenses);
        tvBudget = findViewById(R.id.tvBudget);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        etSearch = findViewById(R.id.etSearch);
        donutChart = findViewById(R.id.donutChart);
        llSpendingChart = findViewById(R.id.llSpendingChart);
        tvSpendingByCategory = findViewById(R.id.tvSpendingByCategory);
    }

    /**
     * Sets up the month and year display to show current month.
     */
    private void setupMonthYear() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String monthYear = dateFormat.format(calendar.getTime()).toUpperCase();
        tvMonthYear.setText(monthYear);
    }

    /**
     * Sets up click listeners for menu, profile, search, and quick action buttons.
     */
    private void setupClickListeners() {
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        ImageButton btnProfile = findViewById(R.id.btnProfile);

        btnMenu.setOnClickListener(v -> {
            // TODO: Open navigation drawer or menu
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show();
        });

        btnProfile.setOnClickListener(v -> {
            // TODO: Open profile screen
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
        });

        // Quick Action Buttons
        Button btnTransactions = findViewById(R.id.btnTransactions);
        Button btnRecentTransactions = findViewById(R.id.btnRecentTransactions);
        Button btnBudgets = findViewById(R.id.btnBudgets);
        Button btnGoals = findViewById(R.id.btnGoals);
        Button btnGoalsSnapshot = findViewById(R.id.btnGoalsSnapshot);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnTransactions.setOnClickListener(v -> {
            // TODO: Navigate to transactions screen
            Toast.makeText(this, "Transactions", Toast.LENGTH_SHORT).show();
        });

        btnRecentTransactions.setOnClickListener(v -> {
            // TODO: Navigate to recent transactions screen
            Toast.makeText(this, "Recent Transactions", Toast.LENGTH_SHORT).show();
        });

        btnBudgets.setOnClickListener(v -> {
            // TODO: Navigate to budgets screen
            Toast.makeText(this, "Budgets", Toast.LENGTH_SHORT).show();
        });

        btnGoals.setOnClickListener(v -> {
            // TODO: Navigate to goals screen
            Toast.makeText(this, "Goals", Toast.LENGTH_SHORT).show();
        });

        btnGoalsSnapshot.setOnClickListener(v -> {
            // TODO: Navigate to goals snapshot screen
            Toast.makeText(this, "Goals Snapshot", Toast.LENGTH_SHORT).show();
        });

        btnSettings.setOnClickListener(v -> {
            // TODO: Navigate to settings screen
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Loads and displays dashboard data.
     * Currently uses placeholder data. Will be replaced with actual data from database.
     * Since there are no transactions yet, expenses are $0 and chart is hidden.
     */
    private void loadDashboardData() {
        // TODO: Load actual data from Room database (income and budget from account setup)
        // For now, using placeholder values
        double income = 3200.0;
        double budget = 2000.0;
        double expenses = 0.0; // No transactions yet
        double totalSpent = 0.0; // No transactions yet

        tvIncome.setText(formatCurrency(income));
        tvBudget.setText(formatCurrency(budget));
        tvExpenses.setText(formatCurrency(expenses));
        tvTotalSpent.setText(formatCurrency(totalSpent));

        // Hide donut chart section since there are no transactions
        llSpendingChart.setVisibility(View.GONE);
        tvSpendingByCategory.setVisibility(View.GONE);
    }

    /**
     * Formats a double value as currency string.
     *
     * @param amount the amount to format
     * @return formatted currency string (e.g., "$3,200")
     */
    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "$%,.0f", amount);
    }
}
