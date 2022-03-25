package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.content.Context;
import android.graphics.Movie;
import android.os.AsyncTask;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/21/2022 at 7:22 PM
 ******************************************************/


public class GetGifMovieTask extends AsyncTask<Context, Void, Movie> {


    private final int resourceId;
    private final int position;
    private final GetMovieCallback callback;

    public GetGifMovieTask(int resourceId, int position, GetMovieCallback callback) {
        this.resourceId = resourceId;
        this.position = position;
        this.callback = callback;
    }

    @Override
    protected Movie doInBackground(Context... params) {
        Context context = params[0];
        return Movie.decodeStream(context.getResources().openRawResource(resourceId));
    }

    @Override
    protected void onPostExecute(Movie movie) {
        super.onPostExecute(movie);
        if (callback != null)
            callback.onGifSelected(movie, position);
    }

}
