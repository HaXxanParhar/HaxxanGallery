package com.drudotstech.customgallery.gallery.adapters;

import static androidx.core.view.ViewCompat.setTransitionName;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.gallery.models.GalleryMediaModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/
public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PicHolder> {

    private final Context context;
    private final PictureClickCallback pictureClickCallback;
    private final LongClickCallback longClickCallback;
    private ArrayList<GalleryMediaModel> pictureList;

    public PicturesAdapter(ArrayList<GalleryMediaModel> pictureList, Context context, PictureClickCallback pictureClickCallback, LongClickCallback longClickCallback) {
        this.pictureList = pictureList;
        this.context = context;
        this.pictureClickCallback = pictureClickCallback;
        this.longClickCallback = longClickCallback;
    }

    @NonNull
    @Override
    public PicHolder onCreateViewHolder(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View cell = inflater.inflate(R.layout.pic_holder_item, container, false);
        return new PicHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull final PicHolder holder, final int position) {

        final GalleryMediaModel image = pictureList.get(position);

        holder.loading.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(image.getMediaPath())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.loading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.picture);

        // to enable transition from recyclerview image to Image in Picture Browser Fragment
        setTransitionName(holder.picture, position + "_image");

        holder.rlMain.setOnClickListener(v -> pictureClickCallback.onPictureClicked(holder, position, pictureList));

        holder.rlMain.setOnLongClickListener(view -> {
            longClickCallback.onLongClicked(holder, position, pictureList);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return pictureList == null ? 0 : pictureList.size();
    }

    public interface LongClickCallback {
        void onLongClicked(PicturesAdapter.PicHolder holder, int position, ArrayList<GalleryMediaModel> pics);
    }

    public interface PictureClickCallback {
        void onPictureClicked(PicturesAdapter.PicHolder holder, int position, ArrayList<GalleryMediaModel> pics);
    }

    public static class PicHolder extends RecyclerView.ViewHolder {

        public ImageView picture, ivSelect;
        public AVLoadingIndicatorView loading;
        public View selectionView, rlMain;

        PicHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.image);
            loading = itemView.findViewById(R.id.loading_image);
            ivSelect = itemView.findViewById(R.id.iv_select);
            ivSelect.setVisibility(View.GONE);
            selectionView = itemView.findViewById(R.id.selection_view);
            selectionView.setVisibility(View.GONE);
            rlMain = itemView.findViewById(R.id.rl_main);

        }
    }
}
