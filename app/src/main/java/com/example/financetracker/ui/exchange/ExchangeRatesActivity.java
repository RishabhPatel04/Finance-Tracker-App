package com.example.financetracker.ui.exchange;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financetracker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExchangeRatesActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://api.frankfurter.app/";

    private RecyclerView rvRates;
    private ProgressBar progressBar;
    private TextView tvError;
    private ExchangeRateAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rates);

        rvRates = findViewById(R.id.rvRates);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);

        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        adapter = new ExchangeRateAdapter();
        rvRates.setLayoutManager(new LinearLayoutManager(this));
        rvRates.setAdapter(adapter);

        fetchRates();
    }

    private void fetchRates() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExchangeRateApi api = retrofit.create(ExchangeRateApi.class);
        Call<ExchangeRatesResponse> call = api.getLatest("USD");
        call.enqueue(new Callback<ExchangeRatesResponse>() {
            @Override
            public void onResponse(Call<ExchangeRatesResponse> call, Response<ExchangeRatesResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    showError("Failed to load rates");
                    return;
                }

                ExchangeRatesResponse body = response.body();
                Map<String, Double> rates = body.rates;
                if (rates == null || rates.isEmpty()) {
                    showError("No rates available");
                    return;
                }

                String[] desired = getResources().getStringArray(R.array.currencies);
                List<String> desiredCodes = Arrays.asList(desired);

                List<ExchangeRateAdapter.RateItem> items = new ArrayList<>();
                for (String code : desiredCodes) {
                    if ("USD".equals(code)) {
                        continue; // base currency
                    }
                    Double rate = rates.get(code);
                    if (rate != null) {
                        items.add(new ExchangeRateAdapter.RateItem(code, rate));
                    }
                }

                if (items.isEmpty()) {
                    showError("No matching currencies found");
                } else {
                    adapter.setItems(items);
                    rvRates.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ExchangeRatesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        rvRates.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        if (message == null || message.trim().isEmpty()) {
            tvError.setText("Something went wrong loading exchange rates.");
        } else {
            tvError.setText(message);
        }
    }
}
