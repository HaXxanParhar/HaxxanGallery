package com.drudotstech.customgallery.gallery.album_only;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.gallery.adapters.PictureFolderAdapter;
import com.drudotstech.customgallery.gallery.interfaces.AlbumClickCallback;
import com.drudotstech.customgallery.gallery.models.GalleryResult;
import com.drudotstech.customgallery.gallery.models.ImageAblum;
import com.drudotstech.customgallery.gallery.recyclerview.MarginDecoration;
import com.drudotstech.customgallery.gallery.utils.GalleryConfig;
import com.drudotstech.customgallery.gallery.utils.GalleryConstants;

import java.util.ArrayList;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

public class AlbumsActivity extends AppCompatActivity implements AlbumClickCallback {


    // ------------------------------------- C O N S T A N T S  ------------------------------------
    private static final int RC_PERMISSIONS = 1;
    private final Activity context = AlbumsActivity.this;

    // ---------------------------------------- V I E W S ------------------------------------------
    private TextView empty;
    // ------------------------------------ R E C Y C L E R V I E W --------------------------------
    private RecyclerView recyclerView;
    private ArrayList<ImageAblum> albums;
    // -------------------------------------- V A R I A B L E S ------------------------------------
    private GalleryConfig galleryConfig;
    private ActivityResultLauncher<Intent> picturesLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override

        public void onActivityResult(ActivityResult result) {

            // when results are returned, hence return the results and close this activity
            if (result.getResultCode() == RESULT_OK) {
                // add the results from the next activity (imageDisplay) and return to previous
                assert result.getData() != null;
                GalleryResult galleryResult = result.getData().getParcelableExtra(GalleryConstants.RESULTS);
                Intent intent = new Intent();
                intent.putExtra(GalleryConstants.RESULTS, galleryResult);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }

            // when no results are returned, hence keep this activity opened (do nothing)
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        init();//init views

        galleryConfig = getIntent().getParcelableExtra(GalleryConstants.CONFIG_KEY);

        // check gallery permission or open gallery
        checkStoragePermission();

        findViewById(R.id.iv_back).setOnClickListener(v -> back());
    }

    private void init() {
        empty = findViewById(R.id.empty);
        recyclerView = findViewById(R.id.folderRecycler);
        recyclerView.addItemDecoration(new MarginDecoration(context));
        recyclerView.hasFixedSize();
        albums = new ArrayList<>();
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
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }


    private ArrayList<ImageAblum> getPicturePaths() {
        int count = 0;
        ArrayList<ImageAblum> albums = new ArrayList<>();

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
                count++;

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
                    albums.get(index).updateImagesCounter(); // update counter
                }
            }
            // move the cursor to next position
            while (cursor.moveToNext()); // Loops ends when no next element is found
            // close the cursor after loop ends
            cursor.close();

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
        Intent intent = new Intent(context, AlbumPicturesActivity.class);
        intent.putExtra(GalleryConstants.ALBUM_PATH, albumPath);
        intent.putExtra(GalleryConstants.ALBUM_NAME, albumName);
        intent.putExtra(GalleryConstants.CONFIG_KEY, galleryConfig);
        picturesLauncher.launch(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
    }
}