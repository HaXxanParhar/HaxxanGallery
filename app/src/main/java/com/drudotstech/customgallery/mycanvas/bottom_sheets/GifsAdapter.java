package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
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

    Context context;
    List<Movie> list;
    SelectGifCallback callback;

    public GifsAdapter(Context context, List<Movie> list, SelectGifCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_gif, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyUtils.setViewSize(context, holder.gifView, 3);

        Movie current = list.get(position);
        holder.gifView.setMovie(current);

        holder.itemView.setOnClickListener(v -> callback.onGifSelected(current));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MyGifView gifView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gifView = itemView.findViewById(R.id.iv_gif);
        }
    }
}
