package com.example.financetracker.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financetracker.R;
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AdminActivity provides user management functionality for admin users.
 * Allows viewing, editing, and deleting users.
 */
public class AdminActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private RecyclerView rvUsers;
    private TextView tvUserCount;
    private UserAdapter adapter;
    private List<User> userList;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Verify admin access
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isAdmin = prefs.getBoolean("isAdmin", false);
        if (!isAdmin) {
            Toast.makeText(this, "Access denied. Admin privileges required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvUsers = findViewById(R.id.rvUsers);
        tvUserCount = findViewById(R.id.tvUserCount);
        Button btnBack = findViewById(R.id.btnBack);

        userList = new ArrayList<>();
        adapter = new UserAdapter(userList);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadUsers();
    }

    /**
     * Loads all users from the database and updates the UI.
     */
    private void loadUsers() {
        executor.execute(() -> {
            List<User> users = AppDatabase.getInstance(getApplicationContext()).userDao().getAllUsers();
            runOnUiThread(() -> {
                userList.clear();
                userList.addAll(users);
                adapter.notifyDataSetChanged();
                tvUserCount.setText("Total Users: " + users.size());
            });
        });
    }

    /**
     * Shows a dialog to edit user details.
     */
    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit User");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        EditText etUsername = dialogView.findViewById(R.id.etUsername);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        android.widget.CheckBox cbIsAdmin = dialogView.findViewById(R.id.cbIsAdmin);

        etUsername.setText(user.username);
        etEmail.setText(user.email);
        etPassword.setText(user.password);
        cbIsAdmin.setChecked(user.isAdmin);

        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            boolean isAdmin = cbIsAdmin.isChecked();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if username is already taken by another user
            executor.execute(() -> {
                User existingUser = AppDatabase.getInstance(getApplicationContext()).userDao().getByUsername(username);
                if (existingUser != null && existingUser.userId != user.userId) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // Update user
                user.username = username;
                user.email = email;
                user.password = password;
                user.isAdmin = isAdmin;

                AppDatabase.getInstance(getApplicationContext()).userDao().update(user);
                runOnUiThread(() -> {
                    Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    loadUsers();
                });
            });
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * Shows a confirmation dialog before deleting a user.
     */
    private void showDeleteUserDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete user '" + user.username + "'? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    executor.execute(() -> {
                        AppDatabase.getInstance(getApplicationContext()).userDao().delete(user);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * RecyclerView adapter for displaying users.
     */
    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private final List<User> users;

        public UserAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvUsername;
            private final TextView tvEmail;
            private final TextView tvAdminBadge;
            private final Button btnEdit;
            private final Button btnDelete;

            public UserViewHolder(View itemView) {
                super(itemView);
                tvUsername = itemView.findViewById(R.id.tvUsername);
                tvEmail = itemView.findViewById(R.id.tvEmail);
                tvAdminBadge = itemView.findViewById(R.id.tvAdminBadge);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }

            public void bind(User user) {
                tvUsername.setText(user.username);
                tvEmail.setText(user.email);
                
                if (user.isAdmin) {
                    tvAdminBadge.setVisibility(View.VISIBLE);
                } else {
                    tvAdminBadge.setVisibility(View.GONE);
                }

                btnEdit.setOnClickListener(v -> showEditUserDialog(user));
                btnDelete.setOnClickListener(v -> showDeleteUserDialog(user));
            }
        }
    }
}

