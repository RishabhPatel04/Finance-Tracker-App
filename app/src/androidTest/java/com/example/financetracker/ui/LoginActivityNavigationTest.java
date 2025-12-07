package com.example.financetracker.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;

import com.example.financetracker.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityNavigationTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void clickingGotoSignUp_startsSignUpActivity() {
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.btnGotoSignUp)).perform(click());
        intended(hasComponent(SignUpActivity.class.getName()));
    }

    @Test
    public void successfulLogin_navigatesToLandingPage() throws InterruptedException {
        ActivityScenario.launch(LoginActivity.class);

        onView(withId(R.id.etUsername)).perform(replaceText("testuser1"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(replaceText("testuser1"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());

        Thread.sleep(1500);

        intended(hasComponent(LandingPageActivity.class.getName()));
    }
}
