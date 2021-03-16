package com.rpo.laba2.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieFragment extends Fragment {
    static final String BASE_URL = "https://api.themoviedb.org/3/";
    final static String API_KEY = "8b17355716fcff9ee89b3c49749db9eb";

    static Retrofit retrofit = null;

    private RecyclerView recyclerView;
    private Context context;

    private SQLiteDatabase db;
    private Repository moviesDb;

    public MovieFragment(Context context) {
        super(R.layout.fragment_movie);

        this.context = context;

        db = ((Activity)context).getBaseContext().openOrCreateDatabase("movies.db", context.MODE_PRIVATE, null);
        moviesDb = new Repository(db);

        connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public static MovieFragment newInstance(Context context) {
        MovieFragment fragment = new MovieFragment(context);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false);
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
                recyclerView = ((Activity) context).findViewById(R.id.rvMovieList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                // определяем слушателя нажатия элемента в списке
                MovieListAdapter.OnStateClickListener stateClickListener = new MovieListAdapter.OnStateClickListener() {
                    @Override
                    public void onStateClick(Movie state, int position) {
                        createNewActivityWithData(state);
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
                    Toast.makeText(context.getApplicationContext(), "Нет доступа к сети и сохраненных данных",
                            Toast.LENGTH_SHORT).show();
                }

                recyclerView = ((Activity) context).findViewById(R.id.rvMovieList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                // определяем слушателя нажатия элемента в списке
                MovieListAdapter.OnStateClickListener stateClickListener = new MovieListAdapter.OnStateClickListener() {
                    @Override
                    public void onStateClick(Movie state, int position) {
                        createNewActivityWithData(state);
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

    public void createNewActivityWithData(Movie state) {
        Bundle bundle = new Bundle();
        bundle.putString("title", state.getTitle());
        bundle.putString("date", state.getReleaseDate());
        bundle.putString("vote", state.getVoteAverage().toString());
        bundle.putString("overview", state.getOverview());
        bundle.putString("path", state.getPosterPath());

        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        Fragment catFragment = MovieItemFragment.newInstance(context);
        catFragment.setArguments(bundle);
        ft.replace(R.id.movie_fragment, catFragment);
        ft.commit();

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                getParentFragmentManager().beginTransaction().remove(catFragment).commit();
            }
        });
    }
}
