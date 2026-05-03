package com.fa.employeemanager.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    public static Retrofit getClient() {

        // 🚨 sécurité anti-crash
        if (ApiConfig.BASE_URL == null || ApiConfig.BASE_URL.isEmpty()) {
            return null;
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static void reset() {
        retrofit = null;
    }
}