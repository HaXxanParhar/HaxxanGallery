package com.drudotstech.customgallery.croppy.croppylib.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.AspectRatioItemViewState;

import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/24/2022 at 5:06 PM
 ******************************************************/


public class AspectRatioAdapter extends RecyclerView.Adapter<AspectRatioAdapter.ViewHolder> {

    Context context;
    List<AspectRatioItemViewState> list;
    SelectAspectRatioCallback callback;
    private int previousSelectedIndex = 0;
    private boolean isFirstTime = true;

    public AspectRatioAdapter(Context context, List<AspectRatioItemViewState> list, SelectAspectRatioCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_aspect_ratio, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AspectRatioItemViewState current = list.get(position);

        if (current.isSelected()) {
            holder.tvAspectRatio.setBackgroundResource(R.drawable.bg_aspect_selected);
        } else {
            holder.tvAspectRatio.setBackgroundResource(R.drawable.bg_aspect_not_selected);
        }

        holder.tvAspectRatio.setText(current.getAspectRatioItem().getAspectRatioNameRes());

        holder.rlItem.setOnClickListener(view -> {
            current.setSelected(true);
            holder.tvAspectRatio.setBackgroundResource(R.drawable.bg_aspect_selected);

            if (previousSelectedIndex != -1 && previousSelectedIndex != position) {
                list.get(previousSelectedIndex).setSelected(false);
                notifyItemChanged(previousSelectedIndex);
            }
            previousSelectedIndex = position;

            callback.onAspectRatioSelected(current.getAspectRatioItem());
        });


        // click the first item when loading for the first time
//        if (isFirstTime && position == 0) {
//            Toast.makeText(context, "calling...", Toast.LENGTH_SHORT).show();
//            callback.onAspectRatioSelected(current.getAspectRatioItem());
//            isFirstTime = false;
//        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View rlItem;
        TextView tvAspectRatio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rlItem = itemView.findViewById(R.id.rl_item);
            tvAspectRatio = itemView.findViewById(R.id.tv_aspect_ratio);
        }
    }
}
