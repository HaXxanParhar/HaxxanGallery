package com.drudotstech.customgallery.gallery.all_pictures_and_albums;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.gallery.adapters.PictureFolderAdapter;
import com.drudotstech.customgallery.gallery.interfaces.AlbumClickCallback;
import com.drudotstech.customgallery.gallery.models.ImageAblum;
import com.drudotstech.customgallery.gallery.recyclerview.MarginDecoration;
import com.drudotstech.customgallery.gallery.utils.GalleryConfig;
import com.drudotstech.customgallery.gallery.utils.GalleryConstants;

import java.util.ArrayList;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

public class AlbumsListActivity extends AppCompatActivity implements AlbumClickCallback {


    // ------------------------------------- C O N S T A N T S  ------------------------------------
    private static final int RC_PERMISSIONS = 1;
    private final Activity context = AlbumsListActivity.this;

    // ---------------------------------------- V I E W S ------------------------------------------
    private TextView empty, tvAlbum;
    private ImageView ivArrow;
    private View rlSelectAlbum;
    // ------------------------------------ R E C Y C L E R V I E W --------------------------------
    private RecyclerView recyclerView;
    private ArrayList<ImageAblum> albums;
    // -------------------------------------- V A R I A B L E S ------------------------------------
    private GalleryConfig galleryConfig;
    private String previousSelectedAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_list);

        init();//init views

        galleryConfig = getIntent().getParcelableExtra(GalleryConstants.CONFIG_KEY);

        // set the name of the previously selected album
        previousSelectedAlbum = getIntent().getStringExtra(GalleryConstants.ALBUM_NAME);

        // animate the arrow to rotate up
        ivArrow.animate().rotation(90).setDuration(200);
        tvAlbum.setText(previousSelectedAlbum);

        // check gallery permission or open gallery
        checkStoragePermission();

        rlSelectAlbum.setOnClickListener(view -> back());
        findViewById(R.id.iv_back).setOnClickListener(v -> back());
    }

    private void init() {
        empty = findViewById(R.id.empty);
        recyclerView = findViewById(R.id.folderRecycler);
        recyclerView.addItemDecoration(new MarginDecoration(context));
        recyclerView.hasFixedSize();
        albums = new ArrayList<>();
        rlSelectAlbum = findViewById(R.id.rl_select_album);
        ivArrow = findViewById(R.id.iv_album_arrow);
        tvAlbum = findViewById(R.id.tv_album);
    }

    private void checkStoragePermission() {
        // check permission
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // request permission
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSIONS);
        } else {
            // get albums and set recyclerView
            albums = getPicturePaths();
            setRecyclerView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS
                && grantResults.length >= 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            albums = getPicturePaths();
            setRecyclerView();
        } else {
            setRecyclerView();
            Toast.makeText(context, "Permission is denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void back() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(0, 0);
    }


    private ArrayList<ImageAblum> getPicturePaths() {

        int allImagesCount = 0;

        // list of all the albums
        ArrayList<ImageAblum> albums = new ArrayList<>();

        // recent album object to add in the list at position 0
        ImageAblum recentAlbum = new ImageAblum();

        // to list the albums where at least one picture is added
        // i.e. to check if any picture is added the album before or not
        ArrayList<String> addedAlbums = new ArrayList<>();

        // uri for MediaStore Query
        Uri allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // define the values to get from the query
        String[] projection = {MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATE_TAKEN};

        // creating Cursor. cursor will have the first result returned by the query
        Cursor cursor = context.getContentResolver().query(
                allImagesUri, // content uri
                projection, // the data projection to get from query
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC");// order by date taken descending. i.e. latest photos first

        try {
            // move the cursor to the first element of the result set
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return new ArrayList<>();// return empty array if cursor is null
            }

            // get data from cursor
            do {

                ImageAblum tempAlbum = new ImageAblum(); // model to store single Album


                // image name
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                // album name i.e. --> Screenshots
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                // image path i.e. --> /storage/52DD-BE21/Pictures/Screenshots/Screenshot_20201221-215817.jpg
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                // extract path from image path till FolderName i.e. -->   /storage/52DD-BE21/Pictures/
                String albumPath = imagePath.substring(0, imagePath.lastIndexOf(folder + "/"));
                // add the folder name at the end of the above path to create folder path i.e. -->   /storage/52DD-BE21/Pictures/Screenshots/
                albumPath = albumPath + folder + "/";

//                Log.d("YAM", "----------------------------------- " + count + " -----------------------------------");
//                Log.d("YAM", " Name      : " + name);
//                Log.d("YAM", " Folder    : " + folder);
//                Log.d("YAM", " data path : " + imagePath);
//                Log.d("YAM", " paths    : " + albumPath);

                // this will only run once and will add the recent album
                if (albums.isEmpty()) {
                    recentAlbum.setFolderName("Recents");
                    recentAlbum.setPath("");
                    recentAlbum.setFirstPic(imagePath);
                    albums.add(recentAlbum);
                }

                // if the first picture is not added in this album | new album
                if (!addedAlbums.contains(albumPath)) {
                    tempAlbum.setPath(albumPath); // add album path in Album model
                    tempAlbum.setFolderName(folder); // add folder name
                    tempAlbum.setFirstPic(imagePath);// add the first picture of the album
                    tempAlbum.updateImagesCounter(); // increment the pictures count
                    albums.add(tempAlbum); // add the new album in the albums list

                    addedAlbums.add(albumPath); // add the album in addedAlbums list
                } else {  // First picture is added so only update the images counter
                    final int index = addedAlbums.indexOf(albumPath); //  get the index of album
                    albums.get(index + 1).updateImagesCounter(); // update counter
                }
                allImagesCount++;
            }
            // move the cursor to next position
            while (cursor.moveToNext()); // Loops ends when no next element is found
            // close the cursor after loop ends
            cursor.close();
            // set all images count in the recent album
            recentAlbum.setNumberOfPics(allImagesCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < albums.size(); i++) {
            Log.d("picture folders", albums.get(i).getFolderName() + " and path = " + albums.get(i).getPath() + " " + albums.get(i).getNumberOfPics());
        }

        return albums;
    }

    private void setRecyclerView() {
        if (albums.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
            PictureFolderAdapter folderAdapter = new PictureFolderAdapter(albums, context, this);
            recyclerView.setAdapter(folderAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    // When clicked on the album, open the images activity and pass the folder path
    @Override
    public void onAlbumClicked(String albumPath, int position, String albumName) {
        // open the images activity
        Intent intent = new Intent();
        intent.putExtra(GalleryConstants.ALBUM_PATH, albumPath);
        intent.putExtra(GalleryConstants.ALBUM_NAME, albumName);
        intent.putExtra(GalleryConstants.CONFIG_KEY, galleryConfig);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0, 0);
    }
}