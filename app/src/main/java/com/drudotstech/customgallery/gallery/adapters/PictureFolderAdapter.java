package com.drudotstech.customgallery.gallery.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.gallery.interfaces.AlbumClickCallback;
import com.drudotstech.customgallery.gallery.models.ImageAblum;

import java.util.ArrayList;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

public class PictureFolderAdapter extends RecyclerView.Adapter<PictureFolderAdapter.FolderHolder> {

    Bitmap selected_img_bitmap;
    String input = "";
    String imageEncoded;
    private ArrayList<ImageAblum> folders;
    private Context context;
    private AlbumClickCallback albumClickCallback;


    /**
     * @param folders An ArrayList of String that represents paths to folders on the external storage that contain pictures
     * @param context The Activity or fragment Context
     * @param listen  interFace for communication between adapter and fragment or activity
     */
    public PictureFolderAdapter(ArrayList<ImageAblum> folders, Context context, AlbumClickCallback listen) {
        this.folders = folders;
        this.context = context;
        this.albumClickCallback = listen;
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.picture_folder_item, parent, false);
        return new FolderHolder(cell);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, @SuppressLint("RecyclerView") int position) {
        final ImageAblum folder = folders.get(position);

        holder.folderPic.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(folder.getFirstPic())
                .apply(new RequestOptions().centerCrop())
                .into(holder.folderPic);


        //setting the number of images
        String text = "" + folder.getFolderName();
        holder.folderName.setText(text + " (" + folder.getNumberOfPics() + ")");

        holder.main_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                albumClickCallback.onAlbumClicked(folder.getPath(), position, folder.getFolderName());

            }
        });

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }


    public class FolderHolder extends RecyclerView.ViewHolder {
        ImageView folderPic;
        TextView folderName;
        TextView folderSize;
        CardView folderCard;
        RelativeLayout main_rel;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            folderPic = itemView.findViewById(R.id.folderPic);
            folderName = itemView.findViewById(R.id.folderName);
//            folderSize=itemView.findViewById(R.id.folderSize);
            folderCard = itemView.findViewById(R.id.folderCard);
            main_rel = itemView.findViewById(R.id.main_rel);
        }
    }

}
