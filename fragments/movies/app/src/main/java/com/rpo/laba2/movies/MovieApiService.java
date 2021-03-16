package com.rpo.laba2.movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("movie/{id}")
    Call<Movie> getMovie(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{genre}")
    Call<TopRatedResponse> getTopMovies(@Path("genre") String genre, @Query("api_key") String apiKey);
}
