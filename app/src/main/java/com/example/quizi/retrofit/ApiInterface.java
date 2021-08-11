package com.example.quizi.retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("api_category.php?encode=url3986")
    Call < String > appCategories();

    @GET("api.php?encode=url3986")
    Call < String > appQuestions(
            @Query("amount") int amount,
            @Query("category") String category,
            @Query("difficulty") String difficulty);

}