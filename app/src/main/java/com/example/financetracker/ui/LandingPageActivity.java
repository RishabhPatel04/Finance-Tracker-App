package com.example.financetracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.financetracker.MainActivity;
import com.example.financetracker.R;
import com.example.financetracker.views.DonutChartView;
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.entity.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LandingPageActivity displays the main dashboard after a successful login.
 * It shows income, budget, and spending by category using a donut chart and
 * provides navigation to transactions, admin tools, and profile actions.
 */
public class LandingPageActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_ADMIN = "isAdmin";

    private TextView tvMonthYear;
    private TextView tvIncome;
    private TextView tvBudget;
    private TextView tvTotalSpent;
    private EditText etSearch;
    private DonutChartView donutChart;
    private View llSpendingChart;
    private TextView tvSpendingByCategory;
    private LinearLayout llCategoryLegend;

    private String currentUsername;
    private boolean isAdmin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final Map<String, Integer> CATEGORY_COLORS = new HashMap<String, Integer>() {{
        put("Mortgage/rent", R.color.category_mortgage);
        put("Insuarance", R.color.category_insurance);
        put("Debt", R.color.category_debt);
        put("Utilities", R.color.category_utilities);
        put("Savings", R.color.category_savings);
        put("Investments", R.color.category_investments);
        put("Food", R.color.category_food);
        put("Personal Care", R.color.category_personal_care);
        put("Subscriptions", R.color.category_subscriptions);
        put("Travel", R.color.category_travel);
    }};

    /**
     * Sets up the landing page layout, initializes all views, configures the
     * current month label and click listeners, and loads the initial dashboard
     * data for the logged-in user.
     *
     * @param savedInstanceState previous state if the activity is re-created
     */
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
     * Refreshes user information and dashboard data whenever the landing page
     * becomes visible again.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
        loadDashboardData();
    }

    /**
     * Finds and caches references to all views used in the landing page UI.
     */
    private void initializeViews() {
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvIncome = findViewById(R.id.tvIncome);
        tvBudget = findViewById(R.id.tvBudget);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        etSearch = findViewById(R.id.etSearch);
        donutChart = findViewById(R.id.donutChart);
        llSpendingChart = findViewById(R.id.llSpendingChart);
        tvSpendingByCategory = findViewById(R.id.tvSpendingByCategory);
        llCategoryLegend = findViewById(R.id.llCategoryLegend);
    }

    private void setupMonthYear() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String monthYear = dateFormat.format(calendar.getTime()).toUpperCase();
        tvMonthYear.setText(monthYear);
    }

    private void loadUserInfo() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUsername = prefs.getString(KEY_USERNAME, "");
        isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false);
    }

    /**
     * Wires up click listeners for the main menu and profile buttons shown on
     * the landing page toolbar.
     */
    private void setupClickListeners() {
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        ImageButton btnProfile = findViewById(R.id.btnProfile);

        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> showMainMenu(v));
        }

        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> showProfileMenu(v));
        }
    }

    /**
     * Shows the main popup menu for navigating to transactions, budgets,
     * goals, and settings. Some options are visible only for admin users.
     *
     * @param anchor the view that anchors the popup menu
     */
    private void showMainMenu(View anchor) {
        try {
            if (anchor == null) {
                showToast("Menu error: Invalid anchor view");
                return;
            }

            PopupMenu popupMenu = new PopupMenu(this, anchor);
            popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

            // Show/hide admin-only menu items
            try {
                boolean isAdmin = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .getBoolean(KEY_IS_ADMIN, false);
                
                MenuItem budgetsItem = popupMenu.getMenu().findItem(R.id.menu_budgets);
                MenuItem goalsItem = popupMenu.getMenu().findItem(R.id.menu_goals);
                
                if (budgetsItem != null) budgetsItem.setVisible(isAdmin);
                if (goalsItem != null) goalsItem.setVisible(isAdmin);
            } catch (Exception e) {
                Log.e("LandingPageActivity", "Error checking admin status", e);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_transactions) {
                    startActivity(new Intent(this, TransactionsActivity.class));
                    return true;
                } else if (itemId == R.id.menu_budgets) {
                    startActivity(new Intent(
                            LandingPageActivity.this,
                            com.example.financetracker.ui.budget.BudgetsActivity.class
                    ));
                    //showToast("Budgets feature coming soon");
                    return true;
                } else if (itemId == R.id.menu_goals) {
                    startActivity(new Intent(LandingPageActivity.this,
                            com.example.financetracker.ui.goals.GoalsActivity.class));
                    //showToast("Goals feature coming soon");
                    return true;
                } else if (itemId == R.id.menu_settings) {
                    showToast("Settings feature coming soon");
                    return true;
                }
                return false;
            });

            popupMenu.show();
        } catch (Exception e) {
            Log.e("LandingPageActivity", "Error showing menu", e);
            showToast("Failed to show menu");
        }
    }

    /**
     * Shows the profile popup menu for admin tools and logout actions.
     *
     * @param anchor the view that anchors the popup menu
     */
    private void showProfileMenu(View anchor) {
        try {
            PopupMenu popupMenu = new PopupMenu(this, anchor);
            popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

            // Show/hide admin menu item
            boolean isAdmin = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .getBoolean(KEY_IS_ADMIN, false);
            MenuItem adminItem = popupMenu.getMenu().findItem(R.id.menu_admin);
            if (adminItem != null) {
                adminItem.setVisible(isAdmin);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_admin) {
                    startActivity(new Intent(this, AdminActivity.class));
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    logout();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        } catch (Exception e) {
            Log.e("LandingPageActivity", "Error showing profile menu", e);
            showToast("Failed to show profile menu");
        }
    }

    /**
     * Clears the stored login state and navigates back to MainActivity.
     */
    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
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
     * Loads all transactions from the database, aggregates income and
     * expenses, and updates the donut chart, totals, and legend on the
     * landing page.
     */
    private void loadDashboardData() {
        // Sample data - replace with actual data from your database
        executor.execute(() -> {
            // Dashboard now shows global totals for all users so that regular users
            // can also see admin-managed transactions.
            List<Transaction> transactions = AppDatabase.getInstance(getApplicationContext())
                    .transactionDao()
                    .getAllTransactions();

            double totalIncome = 0.0;
            double totalExpenses = 0.0;
            Map<String, Double> categoryTotals = new HashMap<>();

            for (Transaction transaction : transactions) {
                if ("income".equals(transaction.type)) {
                    totalIncome += transaction.amount;
                } else if ("expense".equals(transaction.type)) {
                    double amount = Math.abs(transaction.amount);
                    totalExpenses += amount;

                    Double current = categoryTotals.get(transaction.category);
                    if (current == null) {
                        current = 0.0;
                    }
                    categoryTotals.put(transaction.category, current + amount);
                } else if ("investment".equals(transaction.type)) {
                    // Backward compatibility for legacy 'investment' rows
                    if (transaction.amount >= 0) {
                        totalIncome += transaction.amount;
                    } else {
                        double amount = Math.abs(transaction.amount);
                        totalExpenses += amount;

                        Double current = categoryTotals.get(transaction.category);
                        if (current == null) {
                            current = 0.0;
                        }
                        categoryTotals.put(transaction.category, current + amount);
                    }
                }
            }

            List<Float> values = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();
            List<String> legendCategories = new ArrayList<>();

            if (!categoryTotals.isEmpty()) {
                for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                    Double value = entry.getValue();
                    if (value != null && value > 0) {
                        values.add(value.floatValue());
                        colors.add(getCategoryColor(entry.getKey()));
                        legendCategories.add(entry.getKey());
                    }
                }
            }

            double remainingBudget = Math.max(0, totalIncome - totalExpenses);

            final double finalTotalIncome = totalIncome;
            final double finalTotalExpenses = totalExpenses;
            final double finalRemainingBudget = remainingBudget;
            final List<Float> chartValues = new ArrayList<>(values);
            final List<Integer> chartColors = new ArrayList<>(colors);
            final List<String> legendLabels = new ArrayList<>(legendCategories);
            final Map<String, Double> finalCategoryTotals = new HashMap<>(categoryTotals);

            runOnUiThread(() -> {
                tvIncome.setText(formatCurrency(finalTotalIncome));
                tvBudget.setText(formatCurrency(finalRemainingBudget));

                donutChart.setCenterLabel("Total Spent");

                if (chartValues.isEmpty()) {
                    donutChart.setData(new ArrayList<>(), null);
                    tvTotalSpent.setText(formatCurrency(0));
                    llSpendingChart.setVisibility(View.GONE);
                    tvSpendingByCategory.setVisibility(View.GONE);
                    if (llCategoryLegend != null) {
                        llCategoryLegend.removeAllViews();
                        llCategoryLegend.setVisibility(View.GONE);
                    }
                } else {
                    donutChart.setData(chartValues, chartColors);
                    tvTotalSpent.setText(formatCurrency(finalTotalExpenses));
                    llSpendingChart.setVisibility(View.VISIBLE);
                    tvSpendingByCategory.setVisibility(View.VISIBLE);
                    tvSpendingByCategory.setText("Spending by Category (MTD)");

                    if (llCategoryLegend != null) {
                        llCategoryLegend.setVisibility(View.VISIBLE);
                        llCategoryLegend.removeAllViews();

                        for (int i = 0; i < legendLabels.size(); i++) {
                            String category = legendLabels.get(i);
                            Double amountObj = finalCategoryTotals.get(category);
                            double categoryAmount = amountObj != null ? amountObj : 0.0;

                            LinearLayout row = new LinearLayout(LandingPageActivity.this);
                            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            rowParams.bottomMargin = dpToPx(12);
                            row.setLayoutParams(rowParams);
                            row.setOrientation(LinearLayout.HORIZONTAL);
                            row.setGravity(Gravity.CENTER_VERTICAL);

                            View dot = new View(LandingPageActivity.this);
                            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(
                                    dpToPx(12),
                                    dpToPx(12)
                            );
                            dotParams.setMarginEnd(dpToPx(8));
                            dot.setLayoutParams(dotParams);

                            GradientDrawable dotBackground = new GradientDrawable();
                            dotBackground.setShape(GradientDrawable.OVAL);
                            int color = chartColors.isEmpty() ?
                                    ContextCompat.getColor(LandingPageActivity.this, R.color.primary) :
                                    chartColors.get(i % chartColors.size());
                            dotBackground.setColor(color);
                            dot.setBackground(dotBackground);

                            TextView categoryText = new TextView(LandingPageActivity.this);
                            LinearLayout.LayoutParams categoryParams = new LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1f
                            );
                            categoryText.setLayoutParams(categoryParams);
                            categoryText.setText(category);
                            categoryText.setTextColor(ContextCompat.getColor(LandingPageActivity.this, R.color.text_primary));
                            categoryText.setTextSize(14f);

                            TextView amountText = new TextView(LandingPageActivity.this);
                            LinearLayout.LayoutParams amountParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            amountText.setLayoutParams(amountParams);
                            amountText.setText(formatCurrency(categoryAmount));
                            amountText.setTextColor(ContextCompat.getColor(LandingPageActivity.this, R.color.text_primary));
                            amountText.setTextSize(14f);
                            amountText.setTypeface(amountText.getTypeface(), android.graphics.Typeface.BOLD);

                            row.addView(dot);
                            row.addView(categoryText);
                            row.addView(amountText);

                            llCategoryLegend.addView(row);
                        }
                    }
                }
            });
        });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private int getCategoryColor(String category) {
        Integer colorRes = CATEGORY_COLORS.get(category);
        return colorRes != null ? ContextCompat.getColor(this, colorRes) : ContextCompat.getColor(this, R.color.category_travel);
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "$%,.0f", amount);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}