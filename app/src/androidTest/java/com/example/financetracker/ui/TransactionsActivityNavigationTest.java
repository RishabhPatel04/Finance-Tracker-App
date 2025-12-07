package com.example.financetracker.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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

/**
 * Navigation tests for TransactionsActivity.
 * Verifies that tapping the Exchange Rates card starts ExchangeRatesActivity.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TransactionsActivityNavigationTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void exchangeRatesCard_startsExchangeRatesActivity() {
        ActivityScenario.launch(TransactionsActivity.class);
        onView(withId(R.id.cardExchangeRates)).perform(click());
        intended(hasComponent(com.example.financetracker.ui.exchange.ExchangeRatesActivity.class.getName()));
    }
}
