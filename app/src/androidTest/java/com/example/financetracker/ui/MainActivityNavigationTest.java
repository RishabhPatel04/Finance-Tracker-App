package com.example.financetracker.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
public class MainActivityNavigationTest {

    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Before
    public void setUp() {
        Intents.init();
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void clickingLogin_startsLoginActivity() {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.btnLogin)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void clickingCreateAccount_startsSignUpActivity() {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.btnCreateAccount)).perform(click());
        intended(hasComponent(SignUpActivity.class.getName()));
    }

    @Test
    public void whenUserIsLoggedIn_redirectsToLandingPage() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).commit();

        ActivityScenario.launch(MainActivity.class);
        intended(hasComponent(LandingPageActivity.class.getName()));
    }
}
