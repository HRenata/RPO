package com.rpo.laba2.movies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class Repository {
    SQLiteDatabase db;

    Repository(SQLiteDatabase db) {
        this.db = db;
    }

    public void createTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS movies (title TEXT, releaseDate TEXT, overview TEXT, vote REAL, path TEXT)");
    }

    public boolean isEmpty() {
        Cursor query = db.rawQuery("SELECT * FROM movies;", null);
        if(query.moveToFirst()){
            return false;
        }
        return true;
    }

    public List<Movie> getMovieList() {
        List<Movie> movieList = new ArrayList<>();

        Cursor query = db.rawQuery("SELECT * FROM movies;", null);

        //поменять условие уикла он бесконечен что ли
        if (query.moveToFirst()) {
            while (!query.isAfterLast()) {
                String title = query.getString(0);
                String releaseDate = query.getString(1);
                String overview = query.getString(2);
                float vote = query.getInt(3);
                String path = query.getString(4);

                movieList.add(new Movie(vote, path, title, releaseDate, overview));
                query.moveToNext();
            }

            query.close();
        }

        return movieList;
    }

    public void deleteTable() {
        db.execSQL("DROP TABLE IF EXISTS movies");
    }

    public void addMovie(String title, String releaseDate, String overview, float vote, String path) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("releaseDate", releaseDate);
        cv.put("overview", overview);
        cv.put("vote", vote);
        cv.put("path", path);

        db.insert("movies", null, cv);
    }
}
