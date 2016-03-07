package com.movies.popularmovies;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class MovieUtility {

    private static final int DESIRED_WIDTH = 300;
    /**
     * calculate the screen width and image/grid width
     * @param context
     * @return
     */
    public static int getImageWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int optimalColumnCount = Math.round(screenWidth / DESIRED_WIDTH);
        int imageWidth = screenWidth / optimalColumnCount;
        return imageWidth;
    }
}
