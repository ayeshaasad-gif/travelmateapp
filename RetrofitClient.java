package com.example.travelplane.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit Client Singleton
 * Provides a single instance of Retrofit for making API calls
 */
public class RetrofitClient {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    private static RetrofitClient instance;
    private Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Get singleton instance of RetrofitClient
     * @return RetrofitClient instance
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    /**
     * Get API Service for making API calls
     * @return ApiService interface implementation
     */
    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
}

