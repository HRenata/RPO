package com.rpo.laba2.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    static final String BASE_URL = "https://api.themoviedb.org/3/";
    final static String API_KEY = "8b17355716fcff9ee89b3c49749db9eb";

    static Retrofit retrofit = null;

    private RecyclerView recyclerView;
    private Context context;

    private SQLiteDatabase db;
    private Repository moviesDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        context = this;

        db = getBaseContext().openOrCreateDatabase("movies.db", MODE_PRIVATE, null);
        moviesDb = new Repository(db);

        connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
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

                moviesDb.deleteTable();
                moviesDb.createTable();
                for(Movie movie : adapter.getMovieList()) {
                    moviesDb.addMovie(movie.getTitle(), movie.getReleaseDate(), movie.getOverview(),
                            movie.getVoteAverage(), movie.getPosterPath());
                }

                // устанавливаем для списка адаптер
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<TopRatedResponse> call, Throwable throwable) {
                //нет доступа к сети
                //попытка вытащить данные из бд, если таковые имеются

                //нет бд
                if(moviesDb.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Нет доступа к сети и сохраненных данных",
                            Toast.LENGTH_SHORT).show();
                }

                //
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

                List<Movie> movieList = moviesDb.getMovieList();

                // создаем адаптер
                MovieListAdapter adapter = new MovieListAdapter(movieList, stateClickListener);

                // устанавливаем для списка адаптер
                recyclerView.setAdapter(adapter);
            }
        });
    }



}
