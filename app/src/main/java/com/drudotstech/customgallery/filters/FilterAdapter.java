package com.drudotstech.customgallery.filters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;

import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/27/2022 at 4:02 PM
 ******************************************************/


public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    final int padding = 4;
    Context context;
    List<FilterModel> list;
    FilterSelectionCallback callback;
    ViewHolder previousViewHolder = null;
    int previousPosition = 0;

    public FilterAdapter(Context context, List<FilterModel> list, FilterSelectionCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterModel current = list.get(position);

        if (current != null) {
            holder.ivImage.setImageResource(current.getDrawable());
            holder.tvName.setText(current.getName());

            setSelected(holder, current.isSelected());

            holder.rlMain.setOnClickListener(view -> {

                // do nothing if same position is clicked again
                if (previousPosition == position)
                    return;

                // call the callback to show filter effect in activity
                callback.onFilterSelected(position);

                // make the current filter selected and previous unselected
                current.setSelected(true);
                setSelected(holder, true);

                list.get(previousPosition).setSelected(false);
                setSelected(previousViewHolder, false);

                previousViewHolder = holder;
                previousPosition = position;
            });
        }

        if (previousViewHolder == null && position == previousPosition) {
            previousViewHolder = holder;
        }
    }

    private void setSelected(@NonNull ViewHolder holder, boolean isSelected) {
        if (isSelected) {
            holder.rlMain.setPadding(padding, padding, padding, padding);
            holder.rlMain.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_filter_selected));
        } else {
            holder.rlMain.setPadding(0, 0, 0, 0);
            holder.rlMain.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public interface FilterSelectionCallback {
        void onFilterSelected(int position);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName;
        View rlMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_filter);
            tvName = itemView.findViewById(R.id.tv_filter_name);
            rlMain = itemView.findViewById(R.id.rl_main);
        }
    }
}
