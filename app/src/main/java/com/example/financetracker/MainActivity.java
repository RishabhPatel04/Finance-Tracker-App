package com.example.financetracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity is the authenticated landing screen of the app.
 * For now it only shows a placeholder layout. After a successful login,
 * users are navigated here.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    /**
     * Called when the activity is starting. Sets the content view
     * to {@code R.layout.activity_main}.
     *
     * @param savedInstanceState previous state if activity is re-created
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
