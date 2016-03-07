package com.movies.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.movies.popularmovies.R;
import com.movies.popularmovies.model.MovieStore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieImageAdapter extends ArrayAdapter<MovieStore> {

    private int posterWidth;
    private int posterHeight;

    public MovieImageAdapter(Context context, int actualPosterViewWidth, List<MovieStore> movieStoreList) {
        super(context, 0, movieStoreList);
        this.posterWidth = actualPosterViewWidth;
        this.posterHeight = (int)(posterWidth/0.66);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieStore movieStore = (MovieStore) getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_posters, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_poster_imageview);
        Picasso.with(getContext())
                .load(movieStore.getPosterImagePath())
                .resize(posterWidth, posterHeight)
                .into(imageView);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
