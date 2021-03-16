package com.rpo.laba2.movies;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;


public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    interface OnStateClickListener{
        void onStateClick(Movie state, int position);
    }

    private List<Movie> movieList;
    private final OnStateClickListener onClickListener;

    MovieListAdapter(List<Movie> list, OnStateClickListener onClickListener){
        this.movieList = list;
        this.onClickListener = onClickListener;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView poster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            poster = itemView.findViewById(R.id.ivMovie);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_row, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movieAttr = movieList.get(position);

        MovieViewHolder vHolder = (MovieViewHolder) holder;
        vHolder.title.setText(movieAttr.getTitle());

        Picasso.get().load("https://image.tmdb.org/t/p/w500" + movieAttr.getPosterPath()).into(vHolder.poster);

        // обработка нажатия на фильм
        vHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                // вызываем метод слушателя, передавая ему данные
                onClickListener.onStateClick(movieAttr, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
