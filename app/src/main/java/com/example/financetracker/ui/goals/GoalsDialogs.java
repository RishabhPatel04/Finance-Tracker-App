package com.example.financetracker.ui.goals;

import android.app.AlertDialog;
import android.content.Context;

public class GoalsDialogs {
    static void showCreate(Context c){
        new AlertDialog.Builder(c).setTitle("Create Goals")
                .setMessage("TODO: build create-goal form")
                .setPositiveButton("OK", null).show();
    }
    static void showUpdate(Context c){
        new AlertDialog.Builder(c).setTitle("Update Goals")
                .setMessage("TODO: pick a goal to edit")
                .setPositiveButton("Ok", null).show();
    }
    static void showRemove(Context c){
        new AlertDialog.Builder(c).setTitle("Remove Goals")
                .setMessage("TODO: pick a goal to delete")
                .setPositiveButton("OK", null).show();

    }
}
