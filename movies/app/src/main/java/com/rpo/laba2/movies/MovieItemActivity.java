package com.rpo.laba2.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_item_activity);

        //получаем данные о фильме из мэйн активити
        Bundle arguments = getIntent().getExtras();
        String title = arguments.getString("title");
        String date = arguments.getString("date");
        String vote = arguments.getString("vote");
        String overview = arguments.getString("overview");
        String path = arguments.getString("path");

        //устанавливаем полученные данные в поля
        ImageView ivMovie = (ImageView) findViewById(R.id.ivMovie);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        TextView tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        TextView tvVote = (TextView) findViewById(R.id.tvVote);
        TextView tvOverview = (TextView) findViewById(R.id.tvOverview);

        tvTitle.setText(title);
        tvReleaseDate.setText(date);
        tvVote.setText(vote);
        tvOverview.setText(overview);

        Picasso.get().load("https://image.tmdb.org/t/p/w500" + path).into(ivMovie);
    }
}