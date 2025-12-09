package com.example.financetracker.ui.goals;

import static kotlin.text.Typography.dollar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.financetracker.R;
import com.example.financetracker.data.AppDatabase;
import com.example.financetracker.data.goals.Goal;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoalsDialogs {
    public static void showCreate(Activity a){
        View content = LayoutInflater.from(a).inflate(R.layout.dialog_goal_form, null, false);
        TextInputEditText eTitle =  content.findViewById(R.id.etTitle);
        TextInputEditText eAmount = content.findViewById(R.id.etAmount);
        TextView tvDate = content.findViewById(R.id.tvDate);
        final long[] dueDate = { -1L};

        content.findViewById(R.id.btnPickDate).setOnClickListener(v ->{
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                    .datePicker()
                    .build();
            picker.addOnPositiveButtonClickListener(selection ->{
                dueDate[0] = selection;
                tvDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                        .format(new Date(selection)));
            });
            picker.show(((androidx.fragment.app.FragmentActivity)a).getSupportFragmentManager(), "date");
        });
        new MaterialAlertDialogBuilder(a)
                .setTitle("Create Goal")
                .setView(content)
                .setPositiveButton("Save",( d,w) ->{
            String title = safe(eTitle.getText());
            String amtStr = safe(eAmount.getText());

            if (title.isEmpty() || amtStr.isEmpty()){
                Snackbar.make(a.findViewById(android.R.id.content),
                        "Title and amount are required", Snackbar.LENGTH_SHORT).show();
                return ;
            }
            double amountDollars;
            try{
                amountDollars = Double.parseDouble(amtStr);
            }catch(NumberFormatException ex){
                Snackbar.make(a.findViewById(android.R.id.content),
                        "Invalid amount", Snackbar.LENGTH_SHORT).show();
                return;
            }
            final Long targetCents = Math.round(amountDollars * 100.0);


            Executors.newSingleThreadExecutor().execute(() ->{
                Goal g = new Goal();
                g.title = title;
                g.progressCents = 0L;
                g.savedCents = 0L;
                g.targetCents = targetCents;
                g.dueDateMillis = dueDate[0];
                g.createdAt = System.currentTimeMillis();

                AppDatabase.getInstance(a.getApplicationContext()).goalDao().insert(g);
                a.runOnUiThread(() ->
                        Snackbar.make(a.findViewById(android.R.id.content),
                                "Goal created ", Snackbar.LENGTH_SHORT).show());

            });
        })
                .setNegativeButton("Cancel", null)
                .show();
    }
    public static void showUpdate(Activity a){
        Executors.newSingleThreadExecutor().execute(()->{
            List<Goal> goals = AppDatabase.getInstance(a.getApplicationContext()).goalDao().listNow();
            String[] labels = new String[goals.size()];
            for (int i =0; i< goals.size(); i++) labels[i] = goals.get(i).title;

            a.runOnUiThread(() ->{
                if (goals.isEmpty()){
                    Snackbar.make(a.findViewById(android.R.id.content),
                            "No goals yet", Snackbar.LENGTH_SHORT).show();
                    return;
            }
                new MaterialAlertDialogBuilder(a)
                        .setTitle("Select a goal to update")
                        .setItems(labels, (d, which) ->showEditForm(a,goals.get(which)))
                        .show();
            });
        });
    }
    private static void showEditForm(Activity a, Goal goal) {
        View content = LayoutInflater.from(a).inflate(R.layout.dialog_goal_form, null, false);
        TextInputEditText etTitle = content.findViewById(R.id.etTitle);
        TextInputEditText etAmount = content.findViewById(R.id.etAmount);
        TextView tvDate = content.findViewById(R.id.tvDate);
        final long[] dueDate = {goal.dueDateMillis};

        etTitle.setText(goal.title);
        etAmount.setText(String.valueOf(goal.targetCents));
        if (goal.dueDateMillis > 0) {
            tvDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                    .format(new Date(goal.dueDateMillis)));
        }
        content.findViewById(R.id.btnPickDate).setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(goal.dueDateMillis > 0 ? goal.dueDateMillis : null)
                    .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                dueDate[0] = selection;
                tvDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                        .format(new Date(selection)));
            });
            picker.show(((androidx.fragment.app.FragmentActivity) a).getSupportFragmentManager(), "date");

        });
        new MaterialAlertDialogBuilder(a)
                .setTitle("Update Goal")
                .setView(content)
                .setPositiveButton("Save", (d, w) -> {
                    String title = safe(etTitle.getText());
                    String amtStr = safe(etAmount.getText());
                    if (title.isEmpty() || amtStr.isEmpty()) {
                        Snackbar.make(a.findViewById(android.R.id.content),
                                "Title and amount are required", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    double amountDollars = Double.parseDouble(amtStr);
                    final long targetCents = Math.round(amountDollars *100);

                    Executors.newSingleThreadExecutor().execute(() -> {
                        Goal g = new Goal();
                        g.title = title;
                        g.progressCents = 0L;
                        g.savedCents = 0L;
                        g.targetCents = targetCents;
                        g.dueDateMillis = dueDate[0];
                        g.createdAt = System.currentTimeMillis();

                        AppDatabase.getInstance(a.getApplicationContext()).goalDao().update(goal);
                        a.runOnUiThread(() ->
                                Snackbar.make(a.findViewById(android.R.id.content),
                                        "Goal update", Snackbar.LENGTH_SHORT).show());
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    public static void showRemove(Activity a){
        Executors.newSingleThreadExecutor().execute(()->{
            List<Goal> goals = AppDatabase.getInstance(a.getApplicationContext()).goalDao().listNow();
            String[] labels = new String[goals.size()];
            for (int i = 0; i< goals.size(); i++) labels[i] = goals.get(i).title;
//s
            a.runOnUiThread(()->{
                if(goals.isEmpty()){
                    Snackbar.make(a.findViewById(android.R.id.content), " No goals o remove", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                new MaterialAlertDialogBuilder(a)
                        .setTitle("Select a goal to remove")
                        .setItems(labels, (d, which)->{
                            Goal g = goals.get(which);
                            new MaterialAlertDialogBuilder(a)
                                    .setTitle("Remove goal?")
                                    .setMessage("Delete \"" + g.title + "\"?")
                                    .setPositiveButton("Delete", (dd,w)->
                                            Executors.newSingleThreadExecutor().execute(()->{
                                        AppDatabase.getInstance(a.getApplicationContext()).goalDao().delete(g);
                                        a.runOnUiThread(()->
                                                Snackbar.make(a.findViewById(android.R.id.content),
                                                        "Goal removed", Snackbar.LENGTH_SHORT).show());
                            }))
                                    .setNegativeButton("Cancel", null)
                                    .show();


                        })
                        .show();
            });
        });
    }
    private static String safe(CharSequence cs){
        return cs == null ?"":cs.toString().trim();
    }

}

