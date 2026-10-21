package com.example.skuska;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MealApiService {
    @GET("get-jedla")
    Call<String> getRawMeals();
}
