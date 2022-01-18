package com.drudotstech.customgallery.gallery.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.gallery.interfaces.imageIndicatorListener;
import com.drudotstech.customgallery.gallery.models.GalleryMediaModel;

import java.util.ArrayList;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

public class RecyclerViewPagerImageIndicator extends RecyclerView.Adapter<indicatorHolder> {

    private final imageIndicatorListener imageListerner;
    ArrayList<GalleryMediaModel> pictureList;
    Context pictureContx;

    /**
     * @param pictureList   ArrayList of pictureFacer objects
     * @param context       The Activity of fragment context
     * @param imageListener Interface for communication between adapter and fragment
     */
    public RecyclerViewPagerImageIndicator(ArrayList<GalleryMediaModel> pictureList, Context context, imageIndicatorListener imageListener) {
        this.pictureList = pictureList;
        this.pictureContx = context;
        this.imageListerner = imageListener;
    }


    @NonNull
    @Override
    public indicatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.indicator_holder, parent, false);
        return new indicatorHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull indicatorHolder holder, @SuppressLint("RecyclerView") final int position) {

        final GalleryMediaModel pic = pictureList.get(position);

        holder.positionController.setBackgroundColor(pic.isVisible() ? Color.parseColor("#00000000") : Color.parseColor("#8c000000"));

        Glide.with(pictureContx)
                .load(pic.getMediaPath())
                .apply(new RequestOptions().centerCrop())
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.card.setCardElevation(5);
                pic.setVisible(true);
                notifyDataSetChanged();
                imageListerner.onImageIndicatorClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }
}
