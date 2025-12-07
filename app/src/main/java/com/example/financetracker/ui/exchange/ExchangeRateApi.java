package com.example.financetracker.ui.exchange;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ExchangeRateApi {
    @GET("latest")
    Call<ExchangeRatesResponse> getLatest(@Query("from") String baseCurrency);
}
