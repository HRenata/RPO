package com.rpo.laba2.movies;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopRatedResponse {
    @SerializedName("results")
    private List<Movie> result;

    public TopRatedResponse(List<Movie> result) {
        this.result = result;
    }

    public List<Movie> getResult(){
        return result;
    }
}
