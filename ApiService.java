package com.example.travelplane.api;

import com.example.travelplane.models.Destination;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit API Service Interface
 * Defines API endpoints for fetching travel destinations
 */
public interface ApiService {

    /**
     * Fetch all destinations (posts) from JSONPlaceholder API
     * @return List of Destination objects
     */
    @GET("posts")
    Call<List<Destination>> getDestinations();
}

