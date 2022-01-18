package com.drudotstech.customgallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.drudotstech.customgallery.gallery.AlbumsActivity;
import com.drudotstech.customgallery.gallery.models.GalleryMediaModel;
import com.drudotstech.customgallery.gallery.models.GalleryResult;
import com.drudotstech.customgallery.gallery.utils.GalleryConfig;
import com.drudotstech.customgallery.gallery.utils.GalleryConstants;

import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

public class SelectMediaActivity extends AppCompatActivity {


    // ------------------------------------- C O N S T A N T S  ------------------------------------
    private final Context context = SelectMediaActivity.this;

    // ---------------------------------------- V I E W S ------------------------------------------
    private View rlPhoto, rlCamera, rlMultiplePhotos, rlGif, rlFlashes;


    // -------------------------------------- V A R I A B L E S ------------------------------------
    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == RESULT_OK && result.getData() != null) {
                GalleryResult galleryResult = result.getData().getParcelableExtra(GalleryConstants.RESULTS);
                if (galleryResult.getConfig().isSingleSelection()) {
                    // get results of Single image
                    GalleryMediaModel galleryMediaModel = galleryResult.getSinglePicture();
                    Toast.makeText(context, "Got Single image", Toast.LENGTH_SHORT).show();
                } else {
                    // get results of multiple images
                    final List<GalleryMediaModel> pictures = galleryResult.getPictures();
                    Toast.makeText(context, "Got " + pictures.size() + " images", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No resulsts", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_media);

        //--------------------------------- I N I T   V I E W S ------------------------------------
        rlCamera = findViewById(R.id.rl_camera);
        rlPhoto = findViewById(R.id.rl_photo);
        rlMultiplePhotos = findViewById(R.id.rl_photos);
        rlGif = findViewById(R.id.rl_gif);
        rlFlashes = findViewById(R.id.rl_flashes);

        //------------------------------  S E T U P  &   L O A D  ----------------------------------


        //------------------------------------ A C T I O N S ---------------------------------------
        rlPhoto.setOnClickListener(view -> openGallery());
        rlMultiplePhotos.setOnClickListener(view -> openMultiSelectionGallery());
    }

    private void openMultiSelectionGallery() {
        GalleryConfig galleryConfig = new GalleryConfig(GalleryConfig.MULTIPLE_SELECTION);
        final Intent intent = new Intent(context, AlbumsActivity.class);
        intent.putExtra(GalleryConstants.CONFIG_KEY, galleryConfig);
        galleryLauncher.launch(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
    }

    private void openGallery() {
        GalleryConfig galleryConfig = new GalleryConfig(GalleryConfig.SINGLE_SELECTION);
        final Intent intent = new Intent(context, AlbumsActivity.class);
        intent.putExtra(GalleryConstants.CONFIG_KEY, galleryConfig);
        galleryLauncher.launch(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
    }
}