package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.CanvasUtils;
import com.drudotstech.customgallery.utils.MyUtils;

import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/8/2022 at 6:31 PM
 ******************************************************/


public class EmojisAdapter extends RecyclerView.Adapter<EmojisAdapter.ViewHolder> {

    Context context;
    List<String> list;
    SelectEmojiCallback callback;

    public EmojisAdapter(Context context, List<String> list, SelectEmojiCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_emoji, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyUtils.setViewSize(context, holder.textView, 3);

        String current = list.get(position);

        holder.textView.setText(current);

        holder.textView.setOnClickListener(v -> {
            if (callback != null) {
                holder.textView.setDrawingCacheEnabled(true);
                holder.textView.buildDrawingCache();
                Bitmap bitmap = holder.textView.getDrawingCache();
                Bitmap copyBitmap = CanvasUtils.copyBitmap(bitmap);
                holder.textView.setDrawingCacheEnabled(false);
                callback.onEmojiSelected(current, copyBitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.iv_widget);
        }
    }
}
