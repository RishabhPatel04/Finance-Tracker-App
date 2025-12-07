package com.example.financetracker.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;

import com.example.financetracker.MainActivity;
import com.example.financetracker.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LandingPageNavigationTest {

    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private static final String KEY_IS_ADMIN = "isAdmin";

    @Before
    public void setUp() {
        Intents.init();
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_ADMIN, true).commit();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void menuTransactions_startsTransactionsActivity() {
        ActivityScenario.launch(LandingPageActivity.class);
        onView(withId(R.id.btnMenu)).perform(click());
        onView(withText("Transactions")).perform(click());
        intended(hasComponent(TransactionsActivity.class.getName()));
    }

    @Test
    public void menuBudgets_startsBudgetsActivity_whenAdmin() {
        ActivityScenario.launch(LandingPageActivity.class);
        onView(withId(R.id.btnMenu)).perform(click());
        onView(withText("Budgets")).perform(click());
        intended(hasComponent(com.example.financetracker.ui.budget.BudgetsActivity.class.getName()));
    }

    @Test
    public void menuGoals_startsGoalsActivity_whenAdmin() {
        ActivityScenario.launch(LandingPageActivity.class);
        onView(withId(R.id.btnMenu)).perform(click());
        onView(withText("Goals")).perform(click());
        intended(hasComponent(com.example.financetracker.ui.goals.GoalsActivity.class.getName()));
    }

    @Test
    public void menuSettings_startsSettingsActivity() {
        ActivityScenario.launch(LandingPageActivity.class);
        onView(withId(R.id.btnMenu)).perform(click());
        onView(withText("Settings")).perform(click());
        intended(hasComponent(SettingsActivity.class.getName()));
    }

    @Test
    public void profileAdmin_startsAdminActivity_whenAdmin() {
        ActivityScenario.launch(LandingPageActivity.class);
        onView(withId(R.id.btnProfile)).perform(click());
        onView(withText("Admin Area")).perform(click());
        intended(hasComponent(AdminActivity.class.getName()));
    }

    @Test
    public void profileLogout_returnsToMainActivity() {
        ActivityScenario.launch(LandingPageActivity.class);
        onView(withId(R.id.btnProfile)).perform(click());
        onView(withText("Logout")).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }
}
