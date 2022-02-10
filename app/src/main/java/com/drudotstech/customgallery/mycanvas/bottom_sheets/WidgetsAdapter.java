package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.StickerView;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/8/2022 at 6:31 PM
 ******************************************************/


public class WidgetsAdapter extends RecyclerView.Adapter<WidgetsAdapter.ViewHolder> {

    Context context;
    int[] array;
    SelectStickerCallback selectStickerCallback;

    public WidgetsAdapter(Context context, int[] array, SelectStickerCallback selectStickerCallback) {
        this.context = context;
        this.array = array;
        this.selectStickerCallback = selectStickerCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_widget, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int image = array[position];
        holder.ivSticker.text = "";

        holder.ivSticker.setOnClickListener(v -> {
            if (selectStickerCallback != null) {
                selectStickerCallback.onStickerSelected(image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        StickerView ivSticker;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSticker = itemView.findViewById(R.id.iv_widget);
        }
    }
}
