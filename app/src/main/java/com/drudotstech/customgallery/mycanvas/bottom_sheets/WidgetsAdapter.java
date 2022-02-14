package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.CanvasUtils;
import com.drudotstech.customgallery.utils.MyUtils;

import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/8/2022 at 6:31 PM
 ******************************************************/


public class WidgetsAdapter extends RecyclerView.Adapter<WidgetsAdapter.ViewHolder> {

    Context context;
    List<WidgetModel> list;
    SelectWidgetCallback selectStickerCallback;

    public WidgetsAdapter(Context context, List<WidgetModel> list, SelectWidgetCallback selectStickerCallback) {
        this.context = context;
        this.list = list;
        this.selectStickerCallback = selectStickerCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_widget, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        MyUtils.setViewSize(context, holder.textView, 3);

        WidgetModel current = list.get(position);

        if (current != null) {
            if (holder.textView == null) {
                Toast.makeText(context, "stickerView is null at " + position, Toast.LENGTH_SHORT).show();
                return;
            }

            float getTextSize = MyUtils.getTextSize(current.getText());
            holder.textView.setTextSize(getTextSize);

            holder.textView.setText(current.getText());
            holder.textView.setTypeface(ResourcesCompat.getFont(context, current.getFont()));

            holder.textView.setOnClickListener(v -> {
                if (selectStickerCallback != null) {
                    holder.textView.setDrawingCacheEnabled(true);
                    holder.textView.buildDrawingCache();
                    Bitmap bitmap = holder.textView.getDrawingCache();
                    Bitmap copyBitmap = CanvasUtils.copyBitmap(bitmap);
                    holder.textView.setDrawingCacheEnabled(false);
                    selectStickerCallback.onWidgetSelected(current, copyBitmap);
                }
            });
        }
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
