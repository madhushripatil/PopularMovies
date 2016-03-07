package com.movies.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.movies.popularmovies.model.MovieStore;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailViewerActivityFragment extends Fragment {

    public MovieDetailViewerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail_viewer, container, false);
        MovieStore movieStore = getActivity().getIntent().getParcelableExtra(getString(R.string.movie_data_parcel_to_send));

        TextView movieTitleTextView = (TextView) view.findViewById(R.id.movie_title);
        movieTitleTextView.setText(movieStore.getOriginalTitle());

        int imageWidth = MovieUtility.getImageWidth(getContext());
        int imageHeight = (int)(imageWidth/0.66);
        ImageView moviePosterImageView = (ImageView) view.findViewById(R.id.poster_image);
        Picasso.with(getActivity())
                .load(movieStore.getPosterImagePath())
                .resize(imageWidth, imageHeight)
                .into(moviePosterImageView);

        TextView releaseDateTextView = (TextView) view.findViewById(R.id.release_date);
        releaseDateTextView.setText(movieStore.getReleaseDate());

        TextView ratingTextView = (TextView) view.findViewById(R.id.rating);
        ratingTextView.setText(movieStore.getTopRating() + "/10");

        TextView overviewTextView = (TextView) view.findViewById(R.id.movie_overview);
        overviewTextView.setText(movieStore.getOverview());

        return view;
    }
}
