package com.example.financetracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.financetracker.DonutChartView;
import com.example.financetracker.MainActivity;
import com.example.financetracker.R;
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.entity.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LandingPageActivity is the authenticated landing screen (dashboard) of the app.
 * Displays username, admin status, financial summary, and quick action buttons.
 */
public class LandingPageActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_ADMIN = "isAdmin";

    private TextView tvMonthYear;
    private TextView tvIncome;
    private TextView tvExpenses;
    private TextView tvBudget;
    private TextView tvTotalSpent;
    private EditText etSearch;
    private DonutChartView donutChart;
    private View llSpendingChart;
    private TextView tvSpendingByCategory;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        initializeViews();
        setupMonthYear();
        setupClickListeners();
        loadUserInfo();
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
     * Loads and displays user information from SharedPreferences.
     */
    private void loadUserInfo() {
        // User info is now handled in the menu visibility logic
        // No UI elements need to be updated here anymore
    }

    /**
     * Sets up click listeners for menu, profile, admin, logout, and quick action buttons.
     */
    private void setupClickListeners() {
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        ImageButton btnProfile = findViewById(R.id.btnProfile);

        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                showMainMenu(v);
            });
        }

        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                showProfileMenu(v);
            });
        }

    }

    /**
     * Shows the main menu dropdown with Transactions, Budgets, Goals, and Settings options.
     */
    private void showMainMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

        // Show/hide admin-only menu items based on admin status
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false);
        MenuItem budgetsItem = popupMenu.getMenu().findItem(R.id.menu_budgets);
        MenuItem goalsItem = popupMenu.getMenu().findItem(R.id.menu_goals);
        
        if (budgetsItem != null) {
            budgetsItem.setVisible(isAdmin);
        }
        if (goalsItem != null) {
            goalsItem.setVisible(isAdmin);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_transactions) {
                Intent intent = new Intent(this, TransactionsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_budgets) {
                Toast.makeText(this, "Budgets", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to budgets screen (admin only)
                return true;
            } else if (itemId == R.id.menu_goals) {
                Toast.makeText(this, "Goals", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to goals screen (admin only)
                return true;
            } else if (itemId == R.id.menu_settings) {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to settings screen
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    /**
     * Shows the profile dropdown menu with Admin Area and Logout options.
     */
    private void showProfileMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        // Show/hide admin menu item based on admin status
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false);
        MenuItem adminItem = popupMenu.getMenu().findItem(R.id.menu_admin);
        if (adminItem != null) {
            adminItem.setVisible(isAdmin);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_admin) {
                Intent intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    /**
     * Logs out the user by clearing SharedPreferences and navigating to MainActivity.
     */
    private void logout() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_IS_ADMIN);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
    }

    /**
     * Loads and displays dashboard data.
     * Currently uses placeholder data. Will be replaced with actual data from database.
     * Since there are no transactions yet, expenses are $0 and chart shows empty state.
     */
    private void loadDashboardData() {
        // TODO: Load actual data from Room database (income and budget from account setup)
        // For now, using placeholder values
        double income = 3200.0;
        double budget = 2000.0;
        double expenses = 0.0; // No transactions yet

        tvIncome.setText(formatCurrency(income));
        tvBudget.setText(formatCurrency(budget));
        tvExpenses.setText(formatCurrency(expenses));

        // Set up donut chart - show empty state with $0
        // When transactions exist, this will show the actual spending breakdown
        // For now, chart will be empty (DonutChartView handles empty arrays gracefully)
        donutChart.setData(new float[]{}, new int[]{});

        // Hide category breakdown section since there are no transactions
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
