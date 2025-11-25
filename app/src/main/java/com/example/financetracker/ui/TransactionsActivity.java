package com.example.financetracker.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private EditText etSearch;
    private ImageButton btnAddTransaction;
    private ImageButton btnBack; // Declare the back button
    private LinearLayout llEmptyState;
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

    private void showAddEditTransactionDialog(Transaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(transaction == null ? "Add Transaction" : "Edit Transaction");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_transaction, null);
        Spinner spType = dialogView.findViewById(R.id.spType);
        Spinner spCategory = dialogView.findViewById(R.id.spCategory);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDate = dialogView.findViewById(R.id.etDate);

        // Setup type spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Expense", "Income"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

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
            spType.setSelection(transaction.type.equals("income") ? 1 : 0);
            int categoryPosition = categoryAdapter.getPosition(transaction.category);
            if (categoryPosition >= 0) {
                spCategory.setSelection(categoryPosition);
            }
            etAmount.setText(String.valueOf(Math.abs(transaction.amount)));
            etDescription.setText(transaction.description);
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String type = spType.getSelectedItem().toString().toLowerCase();
            String category = spCategory.getSelectedItem().toString();
            String amountStr = etAmount.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (type.equals("expense")) {
                    amount = -Math.abs(amount); // Expenses are negative
                } else {
                    amount = Math.abs(amount); // Income is positive
                }

                Transaction newTransaction = transaction != null ? transaction : new Transaction();
                if (transaction == null) {
                    newTransaction.username = currentUsername;
                }
                newTransaction.category = category;
                newTransaction.amount = amount;
                newTransaction.description = description;
                newTransaction.date = calendar.getTimeInMillis();
                newTransaction.type = type;

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

    private int getCategoryColor(String category) {
        Integer colorRes = CATEGORY_COLORS.get(category);
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
                viewCategoryColor.setBackgroundColor(getCategoryColor(transaction.category));

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

