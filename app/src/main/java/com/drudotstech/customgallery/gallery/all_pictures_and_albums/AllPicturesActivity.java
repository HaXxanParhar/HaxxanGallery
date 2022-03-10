package com.drudotstech.customgallery.gallery.all_pictures_and_albums;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.ViewImageActivity;
import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.crop.CropImage;
import com.drudotstech.customgallery.crop.MyCropActivity;
import com.drudotstech.customgallery.croppy.croppylib.Croppy;
import com.drudotstech.customgallery.croppy.croppylib.main.CropRequest;
import com.drudotstech.customgallery.croppy.croppylib.main.CroppyTheme;
import com.drudotstech.customgallery.croppy.croppylib.main.StorageType;
import com.drudotstech.customgallery.gallery.adapters.PicturesAdapter;
import com.drudotstech.customgallery.gallery.models.GalleryMediaModel;
import com.drudotstech.customgallery.gallery.models.GalleryResult;
import com.drudotstech.customgallery.gallery.recyclerview.MarginDecoration;
import com.drudotstech.customgallery.gallery.utils.GalleryConfig;
import com.drudotstech.customgallery.gallery.utils.GalleryConstants;
import com.drudotstech.customgallery.mycanvas.EditorActivity;
import com.drudotstech.customgallery.utils.FileUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

public class AllPicturesActivity extends AppCompatActivity implements PicturesAdapter.PictureClickCallback {

    private static final int RC_PERMISSIONS = 1;
    private static final int REQUEST_CROPPY = 110;
    private final Context context = AllPicturesActivity.this;

    // ---------------------------------------- V I E W S ------------------------------------------
    private TextView tvAlbum, tvNext;
    private ProgressBar loading;
    private ImageView ivBack, ivArrow;
    private View rootView, rlSelectAlbum;


    // ------------------------------------ R E C Y C L E R V I E W --------------------------------
    private RecyclerView recyclerView;
    private PicturesAdapter adapter;
    private ArrayList<GalleryMediaModel> list;


    // -------------------------------------- V A R I A B L E S ------------------------------------
    private GalleryConfig galleryConfig;
    private String albumName = "Recents";
    private String albumPath = "Recents";
    private PicturesAdapter.PicHolder previousHolder = null;
    private int selectedCount = 0;
    private int previousSelected = -1;

    private ActivityResultLauncher<Intent> albumListLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override

                public void onActivityResult(ActivityResult result) {

                    // when results are returned
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        final Intent data = result.getData();
                        albumName = data.getStringExtra(GalleryConstants.ALBUM_NAME);
                        albumPath = data.getStringExtra(GalleryConstants.ALBUM_PATH);
                        galleryConfig = data.getParcelableExtra(GalleryConstants.CONFIG_KEY);

                        // get media from the album
                        if (!TextUtils.isEmpty(albumPath)) {
                            getAlbumImages(albumPath);
                        } else { // get Recent media if no album path is found
                            getAllImages();
                            albumName = "Recents";
                        }
                        setRecyclerView();
                        tvAlbum.setText(albumName);
                        // animate the arrow
//                ivArrow.animate().rotation(-90).setDuration(200);
                    }

                    // when no results are returned, hence keep this activity opened (do nothing)
                }
            });

    private ActivityResultLauncher<Intent> editPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override

                public void onActivityResult(ActivityResult result) {

                    // when edit picture is returned
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        final Intent data = result.getData();
                        String media = data.getStringExtra(GalleryConstants.MEDIA_PATH);
                        String type = data.getStringExtra(GalleryConstants.MEDIA_TYPE);
                        String uriString = data.getStringExtra(GalleryConstants.MEDIA_URI);
                        setResultAndClose(media, uriString, type);
                    }

                    // when no results are returned, hence keep this activity opened (do nothing)
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pictures);

        galleryConfig = getIntent().getParcelableExtra(GalleryConstants.CONFIG_KEY);

        init();
        checkStoragePermission();

        ivBack.setElevation(8f);

        tvNext.setOnClickListener(view -> openImageWithCroppy());
        ivBack.setOnClickListener(v -> back());
        rlSelectAlbum.setOnClickListener(view -> openAlbumsList());
    }


    private void init() {
        list = new ArrayList<>();

        rootView = findViewById(android.R.id.content);
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.hasFixedSize();

        ivBack = findViewById(R.id.iv_back);
        tvNext = findViewById(R.id.tv_done);

        loading = findViewById(R.id.loader);
        tvAlbum = findViewById(R.id.tv_album);
        rlSelectAlbum = findViewById(R.id.rl_select_album);
        ivArrow = findViewById(R.id.iv_album_arrow);
        ivArrow.setRotation(-90);
        tvAlbum.setText(albumName);
    }


    private void checkStoragePermission() {
        // check permission
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // request permission
            ActivityCompat.requestPermissions(AllPicturesActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , RC_PERMISSIONS);
        } else {
            // get albums and set recyclerView
            if (list.isEmpty()) {
                loading.setVisibility(View.VISIBLE);
                // get picture of the album by Media store query
                getAllImages();
                setRecyclerView();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS
                && grantResults.length >= 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAllImages();
            setRecyclerView();
        } else {
            setRecyclerView();
            Toast.makeText(context, "Permission is denied!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This Method gets all the images and returns and ArrayList of pictures
     */
    public void getAllImages() {
        if (list == null)
            list = new ArrayList<>();
        else list.clear();

        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // uri to get the list

        // define the data values to get from the query
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA, // image path
                MediaStore.Images.Media.DISPLAY_NAME, // image name
                MediaStore.Images.Media.SIZE}; // image size

        // cursor to traverse the query results
        Cursor cursor = AllPicturesActivity.this.getContentResolver().
                query(contentUri,
                        projection, // projection
                        null, // get the list which contains
                        null, // the album path
                        MediaStore.Images.Media.DATE_TAKEN + " DESC");// order by date taken descending. i.e. latest photos first

        // traverse the cursor
        try {
            cursor.moveToFirst();
            do {
                // get picture data and set in temp picture
                GalleryMediaModel tempPicture = new GalleryMediaModel();
                tempPicture.setMediaName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                tempPicture.setMediaPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                tempPicture.setMediaSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
                list.add(tempPicture);// adding in the list
            } while (cursor.moveToNext()); // end the loop when no record is found by the cursor next
            cursor.close(); // close the cursor after the loop
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This Method gets all the images in the folder paths passed as a String to the method and returns
     * and ArrayList of pictures
     */
    public void getAlbumImages(String albumPath) {

        if (list == null)
            list = new ArrayList<>();
        else list.clear();

        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // uri to get the images

        // define the data values to get from the query
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA, // image path
                MediaStore.Images.Media.DISPLAY_NAME, // image name
                MediaStore.Images.Media.SIZE}; // image size

        // cursor to traverse the query results
        Cursor cursor = AllPicturesActivity.this.getContentResolver().
                query(contentUri,
                        projection, // projection
                        MediaStore.Images.Media.DATA + " like ? ", // get the images which contains
                        new String[]{"%" + albumPath + "%"}, // the album path
                        MediaStore.Images.Media.DATE_TAKEN + " DESC");// order by date taken descending. i.e. latest photos first

        // traverse the cursor
        try {
            cursor.moveToFirst();
            do {
                // get picture data and set in temp picture
                GalleryMediaModel tempPicture = new GalleryMediaModel();
                tempPicture.setMediaName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                tempPicture.setMediaPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                tempPicture.setMediaSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
                list.add(tempPicture);// adding in the list
            } while (cursor.moveToNext()); // end the loop when no record is found by the cursor next
            cursor.close(); // close the cursor after the loop
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecyclerView() {
        if (adapter == null) {
            adapter = new PicturesAdapter(list, AllPicturesActivity.this, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setList(list);
        }
        loading.setVisibility(View.GONE);
    }

    private void openAlbumsList() {
        Intent intent = new Intent(context, AlbumsListActivity.class);
        intent.putExtra(GalleryConstants.CONFIG_KEY, galleryConfig);
        intent.putExtra(GalleryConstants.ALBUM_NAME, albumName);
        albumListLauncher.launch(intent);
        overridePendingTransition(0, 0);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // make the arrow up (to animate from up to down when returning to this activity)
            ivArrow.setRotation(90);
        }, 500);

    }

    private void setResultAndClose(String media, String uri, String mediaType) {
        List<GalleryMediaModel> selectedPictures = new ArrayList<>();
        selectedPictures.add(new GalleryMediaModel(media, uri, mediaType));

        Intent intent = new Intent();
        GalleryResult result = new GalleryResult(selectedPictures, galleryConfig);
        intent.putExtra(GalleryConstants.RESULTS, result);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }


    // When user click on a single picture
    @Override
    public void onPictureClicked(PicturesAdapter.PicHolder holder, int position) {
        // select the image on click
        setSelected(holder, position);
    }

    private void setSelected(PicturesAdapter.PicHolder holder, int position) {

        // for single selection, if user select another picture, the previous selection will be unselected
        if (galleryConfig.isSingleSelection()) {
            if (previousSelected != -1 && previousHolder != null && previousSelected != position) { // there is a previous selection
                list.get(previousSelected).setSelected(false);

                // hide the image selection
                previousHolder.selectionView.setVisibility(View.GONE);
                previousHolder.ivSelect.setVisibility(View.GONE);
                selectedCount = Math.max(0, selectedCount - 1); // decrement the count
            }
        }

        final boolean wasSelected = list.get(position).isSelected();//previous selected state

        // check if the max selection has reached
        if (selectedCount == galleryConfig.getMaxSelection() && !wasSelected) {
            // show the message
            Snackbar.make(rootView, "You can only select up-to " + galleryConfig.getMaxSelection() + " images", Snackbar.LENGTH_LONG).show();
        } else {

            // make the current image selected and update the model and UI
            list.get(position).setSelected(!wasSelected); // change the current selected state
            previousSelected = position; // save this position to make it unselected for single selection
            previousHolder = holder; // save holder to hide selection

            // show that image is selected
            if (list.get(position).isSelected()) {
                holder.selectionView.setVisibility(View.VISIBLE);
                holder.ivSelect.setVisibility(View.VISIBLE);
                selectedCount++; // increment the count
            } else { // hide the image selection
                holder.selectionView.setVisibility(View.GONE);
                holder.ivSelect.setVisibility(View.GONE);
                selectedCount--; // decrement the count
            }

            if (selectedCount > 0) {
                tvNext.setEnabled(true);
                tvNext.setTextColor(ContextCompat.getColor(context, R.color.app_color));
            } else {
                tvNext.setEnabled(false);
                tvNext.setTextColor(ContextCompat.getColor(context, R.color.grey));
            }
//            tvAlbum.setText(selectedCount + " selected");
        }
    }

    private void openImageWithCroppy() {
        if (selectedCount <= 0) return;

        // getting selected image
        String path = null;
        int foundCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                path = list.get(i).getMediaPath();
                foundCount++;
                if (foundCount == galleryConfig.getMaxSelection())
                    break;
            }
        }

        if (path == null) return; // null check
        // creating uri
        Uri sourceUri = FileUtils.getUriFromPath(path);

        CropRequest.Auto cropRequest = new CropRequest.Auto(sourceUri, REQUEST_CROPPY,
                StorageType.CACHE, new ArrayList<>(), new CroppyTheme(R.color.app_color));

        Croppy.INSTANCE.start(this, cropRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//     handle result of CropImageActivity
        super.onActivityResult(requestCode, resultCode, data);

        // Result of Crop activity is returned here
        if (requestCode == REQUEST_CROPPY && data != null) {
            final Uri uri = data.getData();
            String mediaPath = FileUtils.getPath(context, uri);
            editPictureLauncher.launch(new Intent(context, EditorActivity.class).putExtra("media", mediaPath));
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(context, ViewImageActivity.class).putExtra("image", result.getUri().toString()));
                Toast.makeText(this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if arrow is up, animate it to down
        if (ivArrow != null && ivArrow.getRotation() == 90) {
            ivArrow.animate().rotation(-90).setDuration(200);
        }
    }

    private void back() {
        setResult(RESULT_CANCELED); // to indicate no result is returned (no images are selected)
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

    @Override
    public void onBackPressed() {
        // close the fragment first
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
        // when no fragment is opened, close the activity
        else {
            back();
        }
    }
}
