package com.drudotstech.customgallery.mycanvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.WidgetModel;

import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/16/2022 at 6:31 PM
 ******************************************************/


public class TextFontAdapter extends RecyclerView.Adapter<TextFontAdapter.ViewHolder> {

    private final Context context;
    private final List<WidgetModel> list;
    private final FontSelectionCallback callback;

    private int previousPosition = 0;

    public TextFontAdapter(Context context, List<WidgetModel> list, FontSelectionCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_text_font, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WidgetModel current = list.get(position);

        if (current != null) {

            holder.textView.setText(current.getText());
            final Typeface font = ResourcesCompat.getFont(context, current.getFont());
            holder.textView.setTypeface(font);

            setSelected(holder, current.isSelected());

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // do nothing if same position is clicked again
                    if (previousPosition == position)
                        return;

                    // call the callback to show filter effect in activity
                    callback.onFontSelected(position);

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
            holder.textView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_black_rounded_selected));
        } else {
            holder.textView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_black_rounded));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setSelectedFont(int index) {
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

    public interface FontSelectionCallback {
        void onFontSelected(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_font);
        }
    }
}
