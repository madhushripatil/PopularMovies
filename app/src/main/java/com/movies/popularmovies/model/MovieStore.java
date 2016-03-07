package com.movies.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieStore implements Parcelable{

    private String originalTitle = "";
    private String posterImagePath = "";
    private String overview = "";
    private double topRating = 0;
    private String releaseDate = "";

    public MovieStore(String originalTitle, String posterImagePath, String overview, double topRating, String releaseDate) {
        this.originalTitle = originalTitle;
        this.posterImagePath = posterImagePath;
        this.overview = overview;
        this.topRating = topRating;
        this.releaseDate = releaseDate;
    }

    private MovieStore(Parcel source) {
        originalTitle = source.readString();
        posterImagePath = source.readString();
        overview = source.readString();
        topRating = source.readDouble();
        releaseDate = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterImagePath);
        dest.writeString(overview);
        dest.writeDouble(topRating);
        dest.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MovieStore> CREATOR =
            new Parcelable.Creator<MovieStore>() {

                @Override
                public MovieStore createFromParcel(Parcel source) {
                    return new MovieStore(source);
                }

                @Override
                public MovieStore[] newArray(int size) {
                    return new MovieStore[size];
                }
            };

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterImagePath() {
        return posterImagePath;
    }

    public String getOverview() {
        return overview;
    }

    public double getTopRating() {
        return topRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
