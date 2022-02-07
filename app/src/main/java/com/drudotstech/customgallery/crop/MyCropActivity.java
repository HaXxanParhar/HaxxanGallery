package com.drudotstech.customgallery.crop;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.drudotstech.customgallery.R;

public class MyCropActivity extends AppCompatActivity {


    private final Context context = MyCropActivity.this;
    private float portrait = 0.66666f; // 2:3
    /**
     * Persist URI image to crop URI if specific permissions are required
     */
    private Uri mCropImageUri;

    /**
     * the options that were set for the crop image
     */
    private CropImageOptions mOptions;

    private TextView tvNext;
    private ImageView mCropImageView, overLay;
    private int screenWidth;
    private int screenHeight;

    public static void setViewSize(Context context, View view, float ratio) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        //ratio = w/h -->  h = w/r
        final float h = displayMetrics.widthPixels / ratio;
        layoutParams.height = (int) h;
        view.setLayoutParams(layoutParams);
    }

    public static void setViewSize(Context context, View view, String ratioString) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        float ratio = 1;
        if (!TextUtils.isEmpty(ratioString)) {
            try {
                ratio = Float.parseFloat(ratioString);
            } catch (NumberFormatException e) {
                ratio = 1;
            }
        }
        //ratio = w/h -->  h = w/r
        final float h = displayMetrics.widthPixels / ratio;
        layoutParams.height = (int) h;
        view.setLayoutParams(layoutParams);
    }

    public static void setViewSize(Context context, View fromView, View toView, String ratioString) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ViewGroup.LayoutParams fromLayoutParams = fromView.getLayoutParams();
        Log.d("PARAMS", "From Params -----> " + fromLayoutParams.width + " , " + fromLayoutParams.height);
        float ratio = 1;
        if (!TextUtils.isEmpty(ratioString)) {
            try {
                ratio = Float.parseFloat(ratioString);
            } catch (NumberFormatException e) {
                ratio = 1;
            }
        }
        //ratio = w/h -->  h = w/r
        final float h = fromLayoutParams.width / ratio;

        ViewGroup.LayoutParams toLayoutParams = toView.getLayoutParams();
        Log.d("PARAMS", "To   Params -----> " + toLayoutParams.width + " , " + toLayoutParams.height);
        toLayoutParams.height = (int) h;
        toLayoutParams.width = fromLayoutParams.width;
        toView.setLayoutParams(toLayoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_crop);

        mCropImageView = findViewById(R.id.cropImageView);
        overLay = findViewById(R.id.overlay);
        tvNext = findViewById(R.id.tv_next);

        mCropImageUri = Uri.parse(getIntent().getStringExtra("uri"));

//        Bundle bundle = getIntent().getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE);
//        mCropImageUri = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE);
//        mOptions = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS);

        if (savedInstanceState == null) {
            if (mCropImageUri == null || mCropImageUri.equals(Uri.EMPTY)) {
                if (CropImage.isExplicitCameraPermissionRequired(this)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    CropImage.startPickImageActivity(this);
                }
            } else if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                setUriToImageView();
            }
        }
//        tvNext.setOnClickListener(view -> cropImage());
    }

    private void setUriToImageView() {
        DisplayMetrics metrics = mCropImageView.getResources().getDisplayMetrics();
        double densityAdj = metrics.density > 1 ? 1 / metrics.density : 1;
        screenWidth = (int) (metrics.widthPixels * densityAdj);
        screenHeight = (int) (metrics.heightPixels * densityAdj);
        BitmapUtils.BitmapSampled decodeResult = BitmapUtils.decodeSampledBitmap(context, mCropImageUri, screenWidth, screenHeight);
        final Bitmap bitmap = decodeResult.bitmap;
        mCropImageView.setImageBitmap(bitmap);
        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();
        Log.d("PARAMS", "From Params -----> " + width + " , " + height);

        //ratio = w/h -->  h = w/r
        final float h = getRatioHeight(width);
        final float w = getRatioWidth(height);
        Log.d("PARAMS", "proc Params 1 -----> " + width + " , " + h);
        Log.d("PARAMS", "proc Params 2 -----> " + w + " , " + height);


        ViewGroup.LayoutParams toLayoutParams = overLay.getLayoutParams();
        // image is taller
        if (h > height) {
            toLayoutParams.height = height;
            toLayoutParams.width = (int) getRatioWidth(height);
        } else {
            toLayoutParams.height = (int) getRatioHeight(width);
            toLayoutParams.width = width;
        }
        overLay.setLayoutParams(toLayoutParams);
        Log.d("PARAMS", "To   Params -----> " + toLayoutParams.width + " , " + toLayoutParams.height);
    }

    private float getRatioWidth(int height) {
        //ratio = w/h -->  w = r/h
        return portrait * height;
    }

    private float getRatioHeight(int width) {
        //ratio = w/h -->  h = w/r
        return width / portrait;
    }

}