package com.rpo.laba2.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieItemFragment extends Fragment {
    private Context context;

    public MovieItemFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    public static MovieItemFragment newInstance(Context context) {
        MovieItemFragment fragment = new MovieItemFragment(context);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_movie_item);
    }

    private void setContentView(int activity_movie_item_activity) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_item, container, false);

        //получаем данные о фильме из мэйн активити
        Bundle arguments = this.getArguments();
        String title = arguments.getString("title");
        String date = arguments.getString("date");
        String vote = arguments.getString("vote");
        String overview = arguments.getString("overview");
        String path = arguments.getString("path");

        //устанавливаем полученные данные в поля
        ImageView ivMovie = (ImageView) view.findViewById(R.id.ivMovie);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        TextView tvReleaseDate = (TextView) view.findViewById(R.id.tvReleaseDate);
        TextView tvVote = (TextView) view.findViewById(R.id.tvVote);
        TextView tvOverview = (TextView) view.findViewById(R.id.tvOverview);

        tvTitle.setText(title);
        tvReleaseDate.setText(date);
        tvVote.setText(vote);
        tvOverview.setText(overview);
        Picasso.get().load("https://image.tmdb.org/t/p/w500" + path).into(ivMovie);

        // Inflate the layout for this fragment
        return view;
    }
}