package com.drudotstech.customgallery.gallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.gallery.adapters.PicturesAdapter;
import com.drudotstech.customgallery.gallery.interfaces.OnPictureSelectedCallBack;
import com.drudotstech.customgallery.gallery.models.GalleryMediaModel;
import com.drudotstech.customgallery.gallery.models.GalleryResult;
import com.drudotstech.customgallery.gallery.recyclerview.MarginDecoration;
import com.drudotstech.customgallery.gallery.utils.GalleryConfig;
import com.drudotstech.customgallery.gallery.utils.GalleryConstants;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

public class ImageDisplayActivity extends AppCompatActivity implements
        OnPictureSelectedCallBack, PicturesAdapter.LongClickCallback, PicturesAdapter.PictureClickCallback {

    private final Context context = ImageDisplayActivity.this;

    // ---------------------------------------- V I E W S ------------------------------------------
    private TextView tvAlbum, tvDone;
    private ProgressBar loading;
    private ImageView ivBack;
    private View rootView;


    // ------------------------------------ R E C Y C L E R V I E W --------------------------------
    private RecyclerView recyclerView;
    private PicturesAdapter adapter;
    private ArrayList<GalleryMediaModel> list;


    // -------------------------------------- V A R I A B L E S ------------------------------------
    private GalleryConfig galleryConfig;
    private String albumPath;
    private String albumName;
    private int selectedCount = 0;
    private boolean isSelectionEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        // get album name and path
        albumPath = getIntent().getStringExtra(GalleryConstants.ALBUM_PATH);
        albumName = getIntent().getStringExtra(GalleryConstants.ALBUM_NAME);
        galleryConfig = getIntent().getParcelableExtra(GalleryConstants.CONFIG_KEY);


        init();
        enableSelection(false);

        tvDone.setOnClickListener(view -> setResultAndClose());
        ivBack.setOnClickListener(v -> back());


        if (list.isEmpty()) {
            loading.setVisibility(View.VISIBLE);
            // get picture of the album by Media store query
            list = getAllImagesByFolder(albumPath);
            adapter = new PicturesAdapter(list, ImageDisplayActivity.this, this, this);
            recyclerView.setAdapter(adapter);
            loading.setVisibility(View.GONE);
        }
    }

    private void setResultAndClose() {
        List<GalleryMediaModel> selectedPictures = new ArrayList<>();
        for (GalleryMediaModel picture : list) {
            if (picture.isSelected()) {
                selectedPictures.add(picture);
            }
        }
        Intent intent = new Intent();
        GalleryResult result = new GalleryResult(selectedPictures, galleryConfig);
        intent.putExtra(GalleryConstants.RESULTS, result);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }


    private void init() {
        rootView = findViewById(android.R.id.content);
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.hasFixedSize();

        ivBack = findViewById(R.id.iv_back);
        tvDone = findViewById(R.id.tv_done);

        loading = findViewById(R.id.loader);
        tvAlbum = findViewById(R.id.tv_album);
        tvAlbum.setText(albumName);
    }

    private void back() {
        if (isSelectionEnabled) {
            enableSelection(false);
        } else {
            setResult(RESULT_CANCELED); // to indicate no result is returned (no images are selected)
            finish();
            overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
        }
    }

    /**
     * @param holder   The ViewHolder for the clicked picture
     * @param position The position in the grid of the picture that was clicked
     * @param pics     An ArrayList of all the items in the Adapter
     */


    /**
     * This Method gets all the images in the folder paths passed as a String to the method and returns
     * and ArrayList of pictures
     *
     * @param albumPath a String corresponding to a folder path on the device external storage
     */
    public ArrayList<GalleryMediaModel> getAllImagesByFolder(String albumPath) {
        ArrayList<GalleryMediaModel> images = new ArrayList<>();
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // uri to get the images

        // define the data values to get from the query
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA, // image path
                MediaStore.Images.Media.DISPLAY_NAME, // image name
                MediaStore.Images.Media.SIZE}; // image size

        // cursor to traverse the query results
        Cursor cursor = ImageDisplayActivity.this.getContentResolver().
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

                images.add(tempPicture);// adding in the list
            } while (cursor.moveToNext()); // end the loop when no record is found by the cursor next
            cursor.close(); // close the cursor after the loop
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }

    // When user click on 'Select' in Picture Browser Fragment
    @Override
    public void onPictureSelected(int position) {
        Log.d("PictureClicked", "------------------------------     Selected   " + isSelectionEnabled + "   ---------------------------");
        // make the picture in the list as selected = true
        if (list != null && list.size() > position) {
            list.get(position).setSelected(true);
            setResultAndClose();
        } else {
            Toast.makeText(context, "The returned position of selected image is not valid", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onLongClicked(PicturesAdapter.PicHolder holder, int position, ArrayList<GalleryMediaModel> pics) {
        if (!isSelectionEnabled) {
            enableSelection(true);
            setSelected(holder, position, pics);
        }
    }

    private void enableSelection(boolean selection) {

        if (!isSelectionEnabled) {// if selection was not enabled before,
            selectedCount = 0; // init the count with 0 as this is the first selection
        }
        isSelectionEnabled = selection; // assign the selection value


        if (selection) { // change the actionbar to reflect selection is enabled
            tvAlbum.setText(selectedCount + " selected");
            tvDone.setVisibility(View.VISIBLE);
            ivBack.setImageResource(R.drawable.ic_baseline_close_24);
        } else { // change the actionbar to normal
            tvAlbum.setText(albumName);
            tvDone.setVisibility(View.GONE);
            ivBack.setImageResource(R.drawable.ic_baseline_arrow_back_24);

            // set all pictures to selected false
            for (GalleryMediaModel picture : list) {
                if (picture.isSelected()) {
                    picture.setSelected(false);
                }
            }
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }

    // When user click on a single picture
    @Override
    public void onPictureClicked(PicturesAdapter.PicHolder holder, int position, ArrayList<GalleryMediaModel> pics) {
        Log.d("PictureClicked", "------------------------------     Clicked   " + isSelectionEnabled + "   ---------------------------");
        // when selection is not enabled, open image on the click
        if (!isSelectionEnabled) {
            openImageInFragment(holder, position, pics);
        } else {
            setSelected(holder, position, pics);
        }
    }

    private void setSelected(PicturesAdapter.PicHolder holder, int position, ArrayList<GalleryMediaModel> pics) {
        final boolean wasSelected = pics.get(position).isSelected();//previous selected state

        // check if the max selection has reached
        if (selectedCount == galleryConfig.getMaxSelection() && !wasSelected) {
            // show the message
            Snackbar.make(rootView, "You can only select up-to " + galleryConfig.getMaxSelection() + " images", Snackbar.LENGTH_LONG).show();
        } else {

            // make the current image selected and update the model and UI
            pics.get(position).setSelected(!wasSelected); // change the current selected state

            // show that image is selected
            if (pics.get(position).isSelected()) {
                holder.selectionView.setVisibility(View.VISIBLE);
                holder.ivSelect.setVisibility(View.VISIBLE);
                selectedCount++; // increment the count
            } else { // hide the image selection
                holder.selectionView.setVisibility(View.GONE);
                holder.ivSelect.setVisibility(View.GONE);
                selectedCount--; // decrement the count
            }
            tvAlbum.setText(selectedCount + " selected");
        }
    }

    private void openImageInFragment(PicturesAdapter.PicHolder holder, int position, ArrayList<GalleryMediaModel> pics) {
        // creating the Picture Browser Fragment
        PictureBrowserFragment browser = PictureBrowserFragment.newInstance(pics, position,
                ImageDisplayActivity.this, this);

        // creating transition for the fragment

        //browser.setEnterTransition(new Slide());
        //browser.setExitTransition(new Slide()); uncomment these to use slide transition and comment the two lines below
        browser.setEnterTransition(new Fade());
        browser.setExitTransition(new Fade());

        // opening the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.picture, position + "picture")
                .add(R.id.displayContainer, browser)
                .addToBackStack(null)
                .commit();
    }
}
