package com.drudotstech.customgallery.gallery.recyclerview;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;


/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

/**
 * recyclerViewPagerImageIndicator's ViewHolder
 */
public class indicatorHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    View positionController;
    private CardView card;

    indicatorHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.imageIndicator);
        card = itemView.findViewById(R.id.indicatorCard);
        positionController = itemView.findViewById(R.id.activeImage);
    }
}
