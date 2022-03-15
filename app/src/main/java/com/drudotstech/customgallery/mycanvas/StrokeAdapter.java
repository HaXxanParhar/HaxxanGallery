package com.drudotstech.customgallery.mycanvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.models.AlignModel;
import com.drudotstech.customgallery.mycanvas.models.StrokeModel;

import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/16/2022 at 6:31 PM
 ******************************************************/


public class StrokeAdapter extends RecyclerView.Adapter<StrokeAdapter.ViewHolder> {

    private final Context context;
    private final List<StrokeModel> list;
    private final StrokeSelectionCallback callback;

    private int previousPosition = 0;

    public StrokeAdapter(Context context, List<StrokeModel> list, StrokeSelectionCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_stroke, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        StrokeModel current = list.get(position);

        if (current != null) {

            holder.imageView.setImageResource(current.getSrc());

            setSelected(holder, current.isSelected());

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // do nothing if same position is clicked again
                    if (previousPosition == position)
                        return;

                    // call the callback to show filter effect in activity
                    callback.onStrokeSelected(position);

                    // unselect previous selection first
                    list.get(previousPosition).setSelected(false);
                    notifyItemChanged(previousPosition);

                    // make the current filter selected and previous unselected
                    current.setSelected(true);
                    setSelected(holder, true);

                    // assign the current position as previous
                    previousPosition = position;
                }
            });
        }
    }

    private void setSelected(@NonNull ViewHolder holder, boolean isSelected) {
        if (isSelected) {
            holder.imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_black_rounded_selected));
        } else {
            holder.imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_black_rounded));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setSelectedAlignment(int index) {
        // do nothing if same position is clicked again
        if (previousPosition == index)
            return;

        if (list != null && !list.isEmpty() && list.size() > index) {

            // first unselect the previous
            list.get(previousPosition).setSelected(false);
            notifyItemChanged(previousPosition);

            // select the index
            list.get(index).setSelected(true);
            notifyItemChanged(index);

            previousPosition = index;
        }
    }

    public interface StrokeSelectionCallback {
        void onStrokeSelected(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_stroke);
        }
    }
}
