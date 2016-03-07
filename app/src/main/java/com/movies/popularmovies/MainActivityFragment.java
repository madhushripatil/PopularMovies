package com.movies.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.movies.popularmovies.adapter.MovieImageAdapter;
import com.movies.popularmovies.model.MovieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieImageAdapter movieImageAdapter;
    List<MovieStore> movieList = new ArrayList<>();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        int actualPosterViewWidth = MovieUtility.getImageWidth(getContext());
        movieImageAdapter = new MovieImageAdapter(getActivity(), actualPosterViewWidth, movieList);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(movieImageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                MovieStore movieObject = movieImageAdapter.getItem(position);
                Intent intent = new Intent(getContext(), MovieDetailViewerActivity.class)
                        .putExtra(getString(R.string.movie_data_parcel_to_send), movieObject);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovieGrid() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));

        MovieDetailFetcher movieDetailFetcher = new MovieDetailFetcher();
        movieDetailFetcher.execute(sortBy);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieGrid();
    }

    public class MovieDetailFetcher extends AsyncTask<String, Void, List<MovieStore>> {

        private final String LOG_TAG = MovieDetailFetcher.class.getSimpleName();

        public List<MovieStore> getMovieDataFromJson(String movieDataRawJson) throws JSONException {

            JSONObject jsonObject = null;
            JSONArray movieResultsArray = null;
            int totalMovies = 0;
            int loopCounter = 0;
            final String BASE_URL_POSTER_PATH = "http://image.tmdb.org/t/p/w185";

            // Information to be fetched from JSON response
            String originalTitle = "";
            String posterImagePath = "";
            String overview = "";
            double topRating = 0;
            String releaseDate = "";

            if(movieDataRawJson != null && !(movieDataRawJson.isEmpty())) {
                jsonObject = new JSONObject(movieDataRawJson);
            }

            if(jsonObject != null && jsonObject.has("results")) {
                movieResultsArray = jsonObject.getJSONArray("results");
                movieList = new ArrayList<>(movieResultsArray.length());
            }

            if(movieResultsArray != null) {
                totalMovies = movieResultsArray.length();
            }

            Log.v(LOG_TAG, "Total Movies Fetched:  " + totalMovies);

            for(loopCounter = 0; loopCounter < totalMovies; loopCounter++) {
                JSONObject movieObject = movieResultsArray.getJSONObject(loopCounter);
                if(movieObject != null) {
                    if(movieObject.has("original_title")) {
                        originalTitle = movieObject.getString("original_title");
                    }
                    if(movieObject.has("poster_path")) {
                        posterImagePath = BASE_URL_POSTER_PATH + movieObject.getString("poster_path");
                    }
                    if(movieObject.has("overview")) {
                        overview = movieObject.getString("overview");
                    }
                    if(movieObject.has("vote_average")) {
                        topRating = movieObject.getDouble("vote_average");
                    }
                    if(movieObject.has("release_date")) {
                        releaseDate = movieObject.getString("release_date");
                    }
                }
                MovieStore movieStore = new MovieStore(originalTitle, posterImagePath, overview, topRating, releaseDate);
                movieList.add(movieStore);
            }

            return movieList;
        }

        @Override
        protected List<MovieStore> doInBackground(String... params) {

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            URL url = null;

            final String BASE_MOVIE_URL = "http://api.themoviedb.org/3/discover/movie/?";
            final String QUERY_PARAM_SORT_BY = "sort_by";
            final String QUERY_PARAM_API_KEY = "api_key";

            String movieDataRawJson = null;
            List<MovieStore> movieStoreList = null;

            try {
                // Construct URL for querying Movie DB API
                Uri movieDBUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM_SORT_BY, params[0])
                        .appendQueryParameter(QUERY_PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                url = new URL(movieDBUri.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    movieStoreList = null;
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                }

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieStoreList = null;
                } else {
                    movieDataRawJson = buffer.toString();
                }

                Log.d(LOG_TAG, movieDataRawJson);

                try {
                    movieStoreList = getMovieDataFromJson(movieDataRawJson);
                    Log.v(LOG_TAG, movieStoreList.toString());
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error parsing JSON", e);
                }

            } catch (MalformedURLException malformedUrlException) {
                Log.e(LOG_TAG, malformedUrlException.getMessage(), malformedUrlException);
            } catch (IOException ioException) {
                Log.e(LOG_TAG, ioException.getMessage(), ioException);
            } finally{
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return movieStoreList;
        }

        @Override
        protected void onPostExecute(List<MovieStore> movieData) {
            if(movieData != null) {
                movieImageAdapter.clear();
                movieImageAdapter.addAll(movieData);
            }
        }
    }
}
