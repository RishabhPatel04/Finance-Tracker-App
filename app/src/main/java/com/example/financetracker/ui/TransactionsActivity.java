package com.example.financetracker.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financetracker.R;
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
 * TransactionsActivity displays and manages financial transactions.
 * Regular users can add and search transactions.
 * Admin users can add, edit, delete, and search all transactions.
 */
public class TransactionsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_ADMIN = "isAdmin";

    private RecyclerView rvTransactions;
    private TextView tvTotalIncome;
    private EditText etSearch;
    private ImageButton btnAddTransaction;
    private ImageButton btnBack; // Declare the back button
    private View cardExchangeRates;
    private View llEmptyState;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private List<Transaction> filteredList;
    private String currentUsername;
    private boolean isAdmin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Category to color mapping
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        // Get user info
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUsername = prefs.getString(KEY_USERNAME, "");
        isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadTransactions();
    }

    private void initializeViews() {
        rvTransactions = findViewById(R.id.rvTransactions);
        etSearch = findViewById(R.id.etSearch);
        btnAddTransaction = findViewById(R.id.btnAddTransaction);
        llEmptyState = findViewById(R.id.llEmptyState);
        btnBack = findViewById(R.id.btnBack); // Initialize the back button
        tvTotalIncome = findViewById(R.id.tvTotalIncome); // Initialize total income TextView
        cardExchangeRates = findViewById(R.id.cardExchangeRates);
    }

    private void setupRecyclerView() {
        transactionList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new TransactionAdapter(filteredList);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnAddTransaction.setOnClickListener(v -> showAddEditTransactionDialog(null));

        btnBack.setOnClickListener(v -> finish()); // Set up back button functionality

        if (cardExchangeRates != null) {
            cardExchangeRates.setOnClickListener(v -> {
                Intent intent = new Intent(TransactionsActivity.this,
                        com.example.financetracker.ui.exchange.ExchangeRatesActivity.class);
                startActivity(intent);
            });
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadTransactions() {
        executor.execute(() -> {
            List<Transaction> transactions;
            if (isAdmin) {
                transactions = AppDatabase.getInstance(getApplicationContext()).transactionDao().getAllTransactions();
            } else {
                transactions = AppDatabase.getInstance(getApplicationContext()).transactionDao().getTransactionsByUser(currentUsername);
            }

            runOnUiThread(() -> {
                transactionList.clear();
                transactionList.addAll(transactions);
                filterTransactions(etSearch.getText().toString());
                updateIncomeAndChart(); // Update income and chart after loading transactions
            });
        });
    }

    private void filterTransactions(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(transactionList);
        } else {
            executor.execute(() -> {
                List<Transaction> results;
                if (isAdmin) {
                    results = AppDatabase.getInstance(getApplicationContext()).transactionDao().searchAllTransactions(query);
                } else {
                    results = AppDatabase.getInstance(getApplicationContext()).transactionDao().searchTransactionsByUser(currentUsername, query);
                }

                runOnUiThread(() -> {
                    filteredList.clear();
                    filteredList.addAll(results);
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
            });
            return;
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            rvTransactions.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvTransactions.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
        }
    }

    private void updateIncomeAndChart() {
        double totalIncome = 0;
        for (Transaction transaction : transactionList) {
            if ("income".equals(transaction.type)) {
                totalIncome += transaction.amount;
            } else if ("investment".equals(transaction.type) && transaction.amount > 0) {
                // Backward compatibility: treat positive 'investment' rows as income
                totalIncome += transaction.amount;
            }
        }

        // Update total income TextView
        tvTotalIncome.setText(String.format(Locale.getDefault(), "$%,.2f", totalIncome));
    }

    private void showAddEditTransactionDialog(Transaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(transaction == null ? "Add Transaction" : "Edit Transaction");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_transaction, null);
        Spinner spType = dialogView.findViewById(R.id.spType);
        Spinner spCategory = dialogView.findViewById(R.id.spCategory);
        Spinner spInvestmentMode = dialogView.findViewById(R.id.spInvestmentMode);
        TextView tvInvestmentModeLabel = dialogView.findViewById(R.id.tvInvestmentModeLabel);
        TextView tvCategoryLabel = dialogView.findViewById(R.id.tvCategoryLabel);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDate = dialogView.findViewById(R.id.etDate);

        // Setup type spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Expense", "Income", "Investment"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        // Setup investment mode spinner (ROI vs Contribution)
        ArrayAdapter<String> investmentModeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"ROI", "Contribution"});
        investmentModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInvestmentMode.setAdapter(investmentModeAdapter);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                boolean isInvestment = "Investment".equalsIgnoreCase(selected);
                boolean isIncome = "Income".equalsIgnoreCase(selected);

                int investmentVisibility = isInvestment ? View.VISIBLE : View.GONE;
                tvInvestmentModeLabel.setVisibility(investmentVisibility);
                spInvestmentMode.setVisibility(investmentVisibility);

                // Hide category selection for pure Income; show for Expense/Investment
                int categoryVisibility = isIncome ? View.GONE : View.VISIBLE;
                tvCategoryLabel.setVisibility(categoryVisibility);
                spCategory.setVisibility(categoryVisibility);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Setup category spinner
        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // Setup date picker
        Calendar calendar = Calendar.getInstance();
        if (transaction != null) {
            calendar.setTimeInMillis(transaction.date);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        etDate.setText(dateFormat.format(calendar.getTime()));
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        etDate.setText(dateFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Pre-fill if editing
        if (transaction != null) {
            // Determine UI type and investment mode based on stored type, category, and amount
            boolean isInvestmentCategory = "Investments".equals(transaction.category);
            if (isInvestmentCategory && ("income".equals(transaction.type) || ("investment".equals(transaction.type) && transaction.amount >= 0))) {
                // Investment ROI
                spType.setSelection(typeAdapter.getPosition("Investment"));
                spInvestmentMode.setSelection(investmentModeAdapter.getPosition("ROI"));
            } else if (isInvestmentCategory && ("expense".equals(transaction.type) || ("investment".equals(transaction.type) && transaction.amount < 0))) {
                // Investment Contribution
                spType.setSelection(typeAdapter.getPosition("Investment"));
                spInvestmentMode.setSelection(investmentModeAdapter.getPosition("Contribution"));
            } else {
                if ("expense".equals(transaction.type)) {
                    spType.setSelection(typeAdapter.getPosition("Expense"));
                } else if ("income".equals(transaction.type)) {
                    spType.setSelection(typeAdapter.getPosition("Income"));
                } else if ("investment".equals(transaction.type)) {
                    // Fallback for legacy data without Investments category
                    spType.setSelection(typeAdapter.getPosition("Investment"));
                    if (transaction.amount >= 0) {
                        spInvestmentMode.setSelection(investmentModeAdapter.getPosition("ROI"));
                    } else {
                        spInvestmentMode.setSelection(investmentModeAdapter.getPosition("Contribution"));
                    }
                }
            }
            int categoryPosition = categoryAdapter.getPosition(transaction.category);
            if (categoryPosition >= 0) {
                spCategory.setSelection(categoryPosition);
            }
            etAmount.setText(String.valueOf(Math.abs(transaction.amount)));
            etDescription.setText(transaction.description);
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String selectedTypeLabel = spType.getSelectedItem().toString();
            String investmentMode = spInvestmentMode.getSelectedItem() != null
                    ? spInvestmentMode.getSelectedItem().toString()
                    : "ROI";

            // For Income, we don't want the user picking from expense categories.
            // Store a fixed label instead; for Expense/Investment we use the selected category.
            String category;
            if ("Income".equalsIgnoreCase(selectedTypeLabel)) {
                category = "Income";
            } else {
                category = spCategory.getSelectedItem().toString();
            }
            String amountStr = etAmount.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                String storedType;

                if ("Investment".equalsIgnoreCase(selectedTypeLabel)) {
                    if ("Contribution".equalsIgnoreCase(investmentMode)) {
                        storedType = "expense";
                        amount = -Math.abs(amount); // Investment contribution is an expense
                    } else { // ROI
                        storedType = "income";
                        amount = Math.abs(amount); // Investment ROI is income
                    }
                } else if ("Expense".equalsIgnoreCase(selectedTypeLabel)) {
                    storedType = "expense";
                    amount = -Math.abs(amount);
                } else { // Income
                    storedType = "income";
                    amount = Math.abs(amount);
                }

                Transaction newTransaction = transaction != null ? transaction : new Transaction();
                if (transaction == null) {
                    newTransaction.username = currentUsername;
                }
                newTransaction.category = category;
                newTransaction.amount = amount;
                newTransaction.description = description;
                newTransaction.date = calendar.getTimeInMillis();
                newTransaction.type = storedType;

                executor.execute(() -> {
                    if (transaction == null) {
                        AppDatabase.getInstance(getApplicationContext()).transactionDao().insert(newTransaction);
                    } else {
                        AppDatabase.getInstance(getApplicationContext()).transactionDao().update(newTransaction);
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(this, transaction == null ? "Transaction added" : "Transaction updated", Toast.LENGTH_SHORT).show();
                        loadTransactions();
                    });
                });
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteTransaction(Transaction transaction) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    executor.execute(() -> {
                        AppDatabase.getInstance(getApplicationContext()).transactionDao().delete(transaction);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                            loadTransactions();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getCategoryColor(Transaction transaction) {
        // Special handling for Investments: ROI vs Contribution use distinct colors
        if ("Investments".equals(transaction.category)) {
            if ("income".equals(transaction.type)) {
                return ContextCompat.getColor(this, R.color.category_investment_roi);
            } else if ("expense".equals(transaction.type)) {
                return ContextCompat.getColor(this, R.color.category_investment_contribution);
            }
        }

        Integer colorRes = CATEGORY_COLORS.get(transaction.category);
        return colorRes != null ? ContextCompat.getColor(this, colorRes) : ContextCompat.getColor(this, R.color.category_travel);
    }

    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
        private final List<Transaction> transactions;

        public TransactionAdapter(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        @Override
        public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TransactionViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            holder.bind(transaction);
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        class TransactionViewHolder extends RecyclerView.ViewHolder {
            private final View viewCategoryColor;
            private final TextView tvCategory;
            private final TextView tvDescription;
            private final TextView tvDate;
            private final TextView tvAmount;
            private final LinearLayout llAdminActions;
            private final ImageButton btnEdit;
            private final ImageButton btnDelete;

            public TransactionViewHolder(View itemView) {
                super(itemView);
                viewCategoryColor = itemView.findViewById(R.id.viewCategoryColor);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                llAdminActions = itemView.findViewById(R.id.llAdminActions);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }

            public void bind(Transaction transaction) {
                // Set category color
                viewCategoryColor.setBackgroundColor(getCategoryColor(transaction));

                tvCategory.setText(transaction.category);
                tvDescription.setText(transaction.description);

                // Format date
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                tvDate.setText(dateFormat.format(transaction.date));

                // Format amount
                String amountStr = String.format(Locale.getDefault(), "$%,.2f", Math.abs(transaction.amount));
                if (transaction.amount < 0) {
                    tvAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.expense_icon));
                    tvAmount.setText("-" + amountStr);
                } else {
                    tvAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.income_icon));
                    tvAmount.setText("+" + amountStr);
                }

                // Show/hide admin actions
                if (isAdmin) {
                    llAdminActions.setVisibility(View.VISIBLE);
                    btnEdit.setOnClickListener(v -> showAddEditTransactionDialog(transaction));
                    btnDelete.setOnClickListener(v -> deleteTransaction(transaction));
                } else {
                    llAdminActions.setVisibility(View.GONE);
                }
            }
        }
    }
}

