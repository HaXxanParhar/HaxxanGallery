package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.content.Context;
import android.graphics.Movie;
import android.os.AsyncTask;

import com.drudotstech.customgallery.R;

import java.util.ArrayList;
import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/21/2022 at 7:22 PM
 ******************************************************/


public class GetGifMoviesListTask extends AsyncTask<Context, Void, Void> {


    private List<Movie> movies;
    private GifReceivedCallback callback;

    public GetGifMoviesListTask(List<Movie> movies, GifReceivedCallback callback) {
        this.movies = movies;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Context... params) {
        Context context = params[0];
        for (int src : getGifsList()) {
            Movie movie = Movie.decodeStream(context.getResources().openRawResource(src));
            movies.add(movie);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        callback.onGifReceived();
    }

    private List<Integer> getGifsList() {
        // get list list
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.gif_hi1);
        list.add(R.drawable.gif_hi6);
        list.add(R.drawable.gif_5);

        list.add(R.drawable.gif_hi4);
        list.add(R.drawable.gif_3);
        list.add(R.drawable.gif_night2);

        list.add(R.drawable.gif_1);
        list.add(R.drawable.gif_moring4);
        list.add(R.drawable.gif_2);

        list.add(R.drawable.gif_night1);
        list.add(R.drawable.gif_hi5);
        list.add(R.drawable.gif_hi7);

        list.add(R.drawable.gif_7);
        list.add(R.drawable.gif_4);
        list.add(R.drawable.gif_hi3);

        list.add(R.drawable.gif_night);
        list.add(R.drawable.gif_6);
        list.add(R.drawable.gif_night4);

        list.add(R.drawable.gif_8);
        list.add(R.drawable.gif_9);
        list.add(R.drawable.gif_night3);

        list.add(R.drawable.gif_10);
        list.add(R.drawable.gif_11);
        list.add(R.drawable.gif_night);

        list.add(R.drawable.gif_12);
        list.add(R.drawable.gif_13);
        list.add(R.drawable.gif_night5);

        return list;
    }

    public interface GifReceivedCallback {
        void onGifReceived();
    }
}
