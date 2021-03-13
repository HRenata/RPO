package com.rpo.laba2.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    static final String TAG = MainActivity.class.getSimpleName();
    static final String BASE_URL = "https://api.themoviedb.org/3/";
    static Retrofit retrofit = null;
    final static String API_KEY = "8b17355716fcff9ee89b3c49749db9eb";
    private RecyclerView recyclerView;
    private Context context;

    ArrayList<Movie> states = new ArrayList<Movie>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        context = this;
        connect();
    }

    private void connect() {
        //подключаемся к апи с фильмами
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        MovieApiService movieApiService = retrofit.create(MovieApiService.class);

        //запрос к апи
        Call<TopRatedResponse> call = movieApiService.getTopMovies("top_rated", API_KEY);
        call.enqueue(new Callback<TopRatedResponse>() {
            @Override
            public void onResponse(Call<TopRatedResponse> call, Response<TopRatedResponse> response) {
                recyclerView = findViewById(R.id.rvMovieList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                // определяем слушателя нажатия элемента в списке
                MovieListAdapter.OnStateClickListener stateClickListener = new MovieListAdapter.OnStateClickListener() {
                    @Override
                    public void onStateClick(Movie state, int position) {
                        //формируем данные для отправки в муви айтем активити
                        Intent intent = new Intent(getApplicationContext(), MovieItemActivity.class);
                        intent.putExtra("title", state.getTitle());
                        intent.putExtra("date", state.getReleaseDate());
                        intent.putExtra("vote", state.getVoteAverage().toString());
                        intent.putExtra("overview", state.getOverview());
                        intent.putExtra("path", state.getPosterPath());
                        startActivity(intent);
                    }
                };

                // создаем адаптер
                MovieListAdapter adapter = new MovieListAdapter(response.body().getResult(), stateClickListener);
                // устанавливаем для списка адаптер
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<TopRatedResponse> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

}
