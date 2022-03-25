package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.MyGifView;
import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.utils.MyUtils;

import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/8/2022 at 6:31 PM
 ******************************************************/


public class GifsAdapter extends RecyclerView.Adapter<GifsAdapter.ViewHolder> {

    private static final int CLICK_DELAY = 200;

    Context context;
    List<GifModel> list;
    SelectGifCallback callback;


    public GifsAdapter(Context context, List<GifModel> list, SelectGifCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_gif, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyUtils.setViewSize(context, holder.gifView, 3);

        GifModel current = list.get(position);

        if (current.getMovie() == null) {

            new GetGifMovieTask(current.getResourceId(), position, (movie, position1) -> {
                list.get(position1).setMovie(movie);
                notifyItemChanged(position1);
            }).execute(context);

        } else {
            holder.gifView.setMovie(current.getMovie());

            holder.gifView.setOnTouchListener((v, event) -> {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.gifView.play();
                        current.clickedTime = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_UP:
                        holder.gifView.pause();

                        // check if it is a click
                        long now = System.currentTimeMillis();
                        // it is a click if upTime - downTime is within the Click Delay
                        if (now - current.clickedTime <= CLICK_DELAY && callback != null) {
                            callback.onGifSelected(current.getMovie());
                        }
                        break;
                }
                return true;
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
//        holder.gifView.play();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
//        holder.gifView.pause();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void set(int position, Movie movie) {
        list.get(position).setMovie(movie);
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MyGifView gifView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gifView = itemView.findViewById(R.id.iv_gif);
        }
    }
}
