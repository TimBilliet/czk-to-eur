package com.github.timbilliet.czk2;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("latest/EUR")
    Call<ExchangeRateResponse> getRate();
}
