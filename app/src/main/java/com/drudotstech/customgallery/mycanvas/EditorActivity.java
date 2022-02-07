package com.drudotstech.customgallery.mycanvas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.drudotstech.customgallery.BaseActivity;
import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.StickersActivity;
import com.drudotstech.customgallery.croppy.croppylib.main.BitmapResult;
import com.drudotstech.customgallery.croppy.croppylib.main.BlurBitmapCallback;
import com.drudotstech.customgallery.croppy.croppylib.main.BlurTask;
import com.drudotstech.customgallery.filters.FilterAdapter;
import com.drudotstech.customgallery.filters.FilterModel;
import com.drudotstech.customgallery.filters.FilterType;
import com.drudotstech.customgallery.filters.FilterUtils;
import com.drudotstech.customgallery.gallery.utils.GalleryConstants;
import com.drudotstech.customgallery.utils.AnimationHelper;
import com.drudotstech.customgallery.utils.FileUtils;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends BaseActivity implements FilterAdapter.FilterSelectionCallback,
        BlurBitmapCallback {


    public static Bitmap originalBitmap;
    // All the bitmap changes will be applied to this except the Filter, which will be applied when
    // user saves the image.
    public static Bitmap unfilteredBitmap;
    // this bitmap will be shown to the user. When user select the filter, copy the unfiltered bitmap into this one;
    // and apply filter and show.
    public static Bitmap filteredBitmap;
    public static Bitmap blurredBitmap;
    // ------------------------------------- C O N S T A N T S  ------------------------------------
    private final Context context = EditorActivity.this;
    // ---------------------------------------- V I E W S ------------------------------------------
    private View ivFilter, ivAdjust, ivStickers, ivText;
    private View mainToolbar, secondToolbar;
    private ImageView ivOriginal, ivBlurred, ivImageView;
    private ImageFilterView ivImageFilterView;
    private View ivBack, ivClose, tvSave, tvDone;
    private TextView tvToolbarName;
    private View llAdjust, llBrightness, llSaturation, llContrast, llWarmth, llSlider;
    private TextView tvSlider;
    private Slider slider;
    private View loading;
    // ------------------------------------ R E C Y C L E R V I E W --------------------------------
    private RecyclerView rvFilters;
    private List<FilterModel> filters;
    private FilterAdapter filterAdapter;
    private LinearLayoutManager filtersLayoutManager;
    private int selectedFilterPosition;
    // -------------------------------------- V A R I A B L E S ------------------------------------
    private AnimationHelper animationHelper = null;
    private Bitmap tempBitmap;
    private String media;
    private int brightness = 50;
    private int contrast = 50;
    private int saturation = 50;
    private int warmth = 50;
    private int selected = -1; // 0 , 1, 2, 3, 4 -> for bottom options

    // to only scroll once per scrolling
    private boolean scrolled = false;


    // -------------------------------------- L A U N C H E R S ------------------------------------
    private ActivityResultLauncher<Intent> stickersLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == RESULT_OK) {
                // todo: show the filter as well if applied
                ivImageView.setImageBitmap(unfilteredBitmap);
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //--------------------------------- I N I T   V I E W S ------------------------------------
        mainToolbar = findViewById(R.id.main_action_bar);
        ivBack = findViewById(R.id.iv_back);
        tvSave = findViewById(R.id.tv_next);

        secondToolbar = findViewById(R.id.second_action_bar);
        ivClose = findViewById(R.id.iv_close);
        tvToolbarName = findViewById(R.id.tv_actionbar_name);
        tvDone = findViewById(R.id.tv_done);

        ivOriginal = findViewById(R.id.iv_original_image);
        ivImageFilterView = findViewById(R.id.iv_filter_image);
        ivImageView = findViewById(R.id.iv_image);
        ivBlurred = findViewById(R.id.iv_blurred_image);

        ivFilter = findViewById(R.id.iv_filter);
        ivAdjust = findViewById(R.id.iv_adjust);
        ivStickers = findViewById(R.id.iv_stickers);

        rvFilters = findViewById(R.id.rv_filters);
        rvFilters.setVisibility(View.GONE);

        llAdjust = findViewById(R.id.ll_adjust);
        llBrightness = findViewById(R.id.ll_brightness);
        llContrast = findViewById(R.id.ll_contrast);
        llSaturation = findViewById(R.id.ll_saturation);
        llWarmth = findViewById(R.id.ll_warmth);
        llSlider = findViewById(R.id.rl_slider);
        tvSlider = findViewById(R.id.tv_slider);
        slider = findViewById(R.id.slider);
        loading = findViewById(R.id.rl_loading);


        //------------------------------  S E T U P  &   L O A D  ----------------------------------

        // init the Animation helper class
        animationHelper = new AnimationHelper(300);


        // get media from previous activity
        media = getIntent().getStringExtra("media");

        //create bitmap from media and load in imageView
        loadBitmaps();

        // set Filters RecyclerView
        setRecyclerView();

        // set listener for slider value change
        setSliderChangeListener();


        //------------------------------------ A C T I O N S ---------------------------------------

        findViewById(R.id.btn_show).setOnClickListener(view -> {
            if (ivOriginal.getAlpha() == 0) {
                ivOriginal.animate().setDuration(100).alpha(1);
            } else {
                ivOriginal.animate().setDuration(100).alpha(0);
            }
        });

        ivFilter.setOnClickListener(view -> {
            selected = 0;
            showSecondMenu(true);
        });

        ivAdjust.setOnClickListener(view -> {
            selected = 1;
            // set temp Bitmap so the changes will only be applied to temp
            tempBitmap = filteredBitmap.copy(filteredBitmap.getConfig(), true);
            ivImageFilterView.setImageBitmap(tempBitmap);
            ivImageFilterView.setVisibility(View.VISIBLE);
            ivImageView.setVisibility(View.GONE);

            showSecondMenu(true);
        });

        ivStickers.setOnClickListener(view -> {
            Intent intent = new Intent(context, StickersActivity.class);
//            intent.putExtra(GalleryConstants.ORIGINAL_BITMAP, originalBitmap);
//            intent.putExtra(GalleryConstants.BLURRED_BITMAP, blurredBitmap);
//            intent.putExtra(GalleryConstants.FILTERED_BITMAP, filteredBitmap);
//            intent.putExtra(GalleryConstants.BITMAP, unfilteredBitmap);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });


        ivClose.setOnClickListener(view -> {
            if (selected > 3) { // Slider of adjust options are opened, so don't reset
                showSecondMenu(false);
            } else {
                resetToEditedPicture();
                showSecondMenu(false);
            }
        });

        tvSave.setOnClickListener(view -> saveImageAndSetResult());
        tvDone.setOnClickListener(view -> saveChanges());
        ivBack.setOnClickListener(view -> hideMenuOrBack());

        // Adjust menu clicks
        llBrightness.setOnClickListener(view -> {
            selected = 4;
            showSecondMenu(true);
        });
        llContrast.setOnClickListener(view -> {
            selected = 5;
            showSecondMenu(true);
        });
        llSaturation.setOnClickListener(view -> {
            selected = 6;
            showSecondMenu(true);
        });
        llWarmth.setOnClickListener(view -> {
            selected = 7;
            showSecondMenu(true);
        });
    }

    @SuppressLint("RestrictedApi")
    private void setSliderChangeListener() {
        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                final int value = (int) slider.getValue();
                switch (selected) {
                    case 4:
                        brightness = value;
                        tvSlider.setText(brightness + "");
                        ivImageFilterView.setBrightness(brightness / 50.0f);
                        break;

                    case 5:
                        contrast = value;
                        tvSlider.setText(contrast + "");
                        ivImageFilterView.setContrast(contrast / 50.0f);
                        break;

                    case 6:
                        saturation = value;
                        tvSlider.setText(saturation + "");
                        ivImageFilterView.setSaturation(saturation / 50.0f);
                        break;

                    case 7:
                        warmth = value;
                        tvSlider.setText(warmth + "");
                        if (warmth == 50) {
                            ivImageFilterView.setWarmth(1);
                        } else if (warmth > 50) {
                            ivImageFilterView.setWarmth(2);
                        } else {
                            ivImageFilterView.setWarmth(5);
                        }
                        break;
                }
            }
        });
    }


    private void resetToEditedPicture() {
        ivImageView.setVisibility(View.VISIBLE);
        ivImageView.setImageBitmap(filteredBitmap);

        // resetting image filter view
        ivImageFilterView.setVisibility(View.GONE);
        brightness = 50;
        contrast = 50;
        saturation = 50;
        warmth = 50;
        ivImageFilterView.setBrightness(1);
        ivImageFilterView.setContrast(1);
        ivImageFilterView.setSaturation(1);
        ivImageFilterView.setWarmth(1);
    }

    private void saveChanges() {

        if (selected > 3) {
            showSecondMenu(false);
        }

        switch (selected) {
            case 0:
                if (tempBitmap != null) {
                    filteredBitmap = tempBitmap.copy(tempBitmap.getConfig(), true);
                    ivImageView.setImageBitmap(filteredBitmap);
                }
                showSecondMenu(false);
                break;

            case 1:
                showSecondMenu(false);
                ivImageFilterView.buildDrawingCache();
                final Bitmap drawingCache = ivImageFilterView.getDrawingCache();
                if (drawingCache == null) {
                    Toast.makeText(context, "Drawing cache is null", Toast.LENGTH_SHORT).show();
                    return;
                }
                unfilteredBitmap = drawingCache.copy(drawingCache.getConfig(), true);
                filteredBitmap = drawingCache.copy(drawingCache.getConfig(), true);
                ivImageView.setImageBitmap(filteredBitmap);
                ivImageView.setVisibility(View.VISIBLE);
                ivImageFilterView.setVisibility(View.GONE);
                break;
        }
    }

    private void showSecondMenu(boolean shouldShowSecondToolbar) {

        // Show Views
        if (shouldShowSecondToolbar) {

            // update the toolbar title
            switch (selected) {
                case 0:
                    tvToolbarName.setText("Filters");
                    animationHelper.moveFromBottomToTop(rvFilters, 66f);
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    break;

                case 1:
                    tvToolbarName.setText("Adjust");
                    animationHelper.moveFromBottomToTop(llAdjust, 66f);
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    break;

                case 2:
                    tvToolbarName.setText("Stickers");
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    break;
                case 3:
                    tvToolbarName.setText("Text");
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    break;

                ///  --------- sub menus ------------------
                case 4:
                    tvToolbarName.setText("Brightness");
                    slider.setValue(brightness);
                    tvSlider.setText(brightness + "");
                    showSlider(true);
                    break;

                case 5:
                    tvToolbarName.setText("Contrast");
                    slider.setValue(contrast);
                    tvSlider.setText(contrast + "");
                    showSlider(true);
                    break;

                case 6:
                    tvToolbarName.setText("Saturation");
                    slider.setValue(saturation);
                    tvSlider.setText(saturation + "");
                    showSlider(true);
                    break;

                case 7:
                    tvToolbarName.setText("Warmth");
                    slider.setValue(warmth);
                    tvSlider.setText(warmth + "");
                    showSlider(true);
                    break;
            }

        }

        // Hide Views
        else {

            switch (selected) {
                case 0:
                    tvToolbarName.setText("Filters");
                    animationHelper.moveToBottom(rvFilters, 100f);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = -1;
                    break;
                case 1:
                    tvToolbarName.setText("Adjust");
                    animationHelper.moveToBottom(llAdjust, 100f);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = -1;
                    break;
                case 2:
                    tvToolbarName.setText("Stickers");
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = -1;
                    break;
                case 3:
                    tvToolbarName.setText("Text");
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = -1;
                    break;


                ///  --------- sub menus ------------------
                case 4:
                case 5:
                case 6:
                case 7:
                    tvToolbarName.setText("Adjust");
                    showSlider(false);
                    selected = 1;
                    break;
            }

        }
    }

    private void showSlider(boolean shouldShow) {
        if (shouldShow) {
            llSlider.setVisibility(View.VISIBLE);
            animationHelper.moveFromBottomToTop(llSlider, 66f);
        } else {
            animationHelper.moveToBottom(llSlider, 100f);
        }
    }

    private void setRecyclerView() {
        filters = new ArrayList<>();
        filters.add(new FilterModel(FilterType.NO_FILTER, "Original", R.drawable.image2, true));
        filters.add(new FilterModel(FilterType.DEEP_BLUE, "Deep Blue", R.drawable.image));
        filters.add(new FilterModel(FilterType.RAINBOW, "Rainbow", R.drawable.image1));
        filters.add(new FilterModel(FilterType.ANTHONY, "Anthony", R.drawable.image3));
        filters.add(new FilterModel(FilterType.MARK, "Magical", R.drawable.image));
        filters.add(new FilterModel(FilterType.SUMMER, "Summer", R.drawable.image3));
        filters.add(new FilterModel(FilterType.WINTER, "Winter", R.drawable.image));
        filters.add(new FilterModel(FilterType.FILTER1, "Inspiring", R.drawable.image1));
        filters.add(new FilterModel(FilterType.FILTER2, "Energetic", R.drawable.image3));
        filters.add(new FilterModel(FilterType.MARK, "Fabulous", R.drawable.image));
        filters.add(new FilterModel(FilterType.SUMMER, "Summer", R.drawable.image3));
        filters.add(new FilterModel(FilterType.WINTER, "Winter", R.drawable.image));
        filters.add(new FilterModel(FilterType.FILTER1, "Inspiring", R.drawable.image1));
        filters.add(new FilterModel(FilterType.FILTER2, "Energetic", R.drawable.image3));


        filterAdapter = new FilterAdapter(context, filters, this);
        filtersLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvFilters.setLayoutManager(filtersLayoutManager);
        rvFilters.setAdapter(filterAdapter);
    }

    private void loadBitmaps() {
        showLoading();
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(media)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            originalBitmap = resource;

                            ivImageView.setDrawingCacheEnabled(true);
                            ivImageFilterView.setDrawingCacheEnabled(true);

                            filteredBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
                            unfilteredBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
                            ivOriginal.setImageBitmap(resource);
                            ivImageView.setImageBitmap(filteredBitmap);
                            ivImageView.animate().setDuration(100).alpha(1);

                            blurredBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
                            new BlurTask(context, blurredBitmap, 22, EditorActivity.this).execute();
                        }
                    });

        } catch (Exception e) {
            hideLoading();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBlurCompleted(BitmapResult bitmapResult) {
        hideLoading();
        if (bitmapResult.isStatus()) {
            ivBlurred.setImageBitmap(bitmapResult.getBitmap());
            ivBlurred.setVisibility(View.VISIBLE);
        }
    }

    /**
     * When filter is selected from the filters RecyclerView
     */
    @Override
    public void onFilterSelected(int position) {
        selectedFilterPosition = position;
        FilterModel filter = filters.get(position);
        final FilterType filterType = filter.getFilterType();

        // copy a temp Bitmap from non-filtered bitmap in order to apply the filter
        tempBitmap = unfilteredBitmap.copy(unfilteredBitmap.getConfig(), true);

        // apply the filter
        if (filterType != FilterType.NO_FILTER) {
            tempBitmap = FilterUtils.applyFilter(context, tempBitmap, filterType);
        }

        // show in the imageView
        if (tempBitmap != null) {
            ivImageView.setImageBitmap(tempBitmap);
            ivOriginal.animate().setDuration(100).alpha(0);
        }

        // scroll to middle
//        final int width = rvFilters.getWidth();
//        final int height = rvFilters.getHeight();
//        final int centerX = width / 2;


        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final int SCREEN_WIDTH = displayMetrics.widthPixels;
        int first = filtersLayoutManager.findFirstVisibleItemPosition();

        RecyclerView.ViewHolder holder = rvFilters.findViewHolderForAdapterPosition(first);
        if (null == holder) {
            Log.d("FILTER_RV", " ----------- Starting View at " + position + " is NUll --------- ");
            return;
        }

        View firstView = holder.itemView;
        int startX = firstView.getRight();

        holder = rvFilters.findViewHolderForAdapterPosition(position);
        if (null == holder) {
            Log.d("FILTER_RV", " ----------- View at " + position + " is NUll --------- ");
            return;
        }

        View selectedView = holder.itemView;
        final int width = selectedView.getWidth();
        int threshold = SCREEN_WIDTH / 2;
        final float x = selectedView.getX() - startX;
//        Log.d("FILTER_RV", " X : " + selectedView.getX() + " | start X : " + startX + "  ---> " + x + " > " + threshold);
        if (x > threshold) {
            rvFilters.smoothScrollBy(width, 0);
        } else {
            rvFilters.smoothScrollBy(-width, 0);
        }

    }

    @Override
    public void onBackPressed() {
        hideMenuOrBack();
    }

    private void hideMenuOrBack() {
        if (selected > 3) { // more menus are opened, hide them first
            showSecondMenu(false);
        } else if (selected != -1) {
            resetToEditedPicture();
            showSecondMenu(false);
        } else {
            showSaveChangesPopup();
        }
    }

    private void showSaveChangesPopup() {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_yes_cancel, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_Dialog));
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        //for popup animation
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
        //transparent background
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        TextView tvCancel, tvYes, tvMessage, tvTitle;
        tvTitle = view.findViewById(R.id.tv_title);
        tvMessage = view.findViewById(R.id.tv_message);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvYes = view.findViewById(R.id.tv_yes);
        tvTitle.setText("Save Changes");
        tvMessage.setText("Do you want to save the changes?");
        tvCancel.setText("Discard");
        tvYes.setText("Yes");

        //functionality upon clicks
        tvCancel.setOnClickListener(v -> {
            finish();
            alertDialog.dismiss();
        });
        tvYes.setOnClickListener(v -> {
            saveImageAndSetResult();
            alertDialog.dismiss();
        });
    }

    private void saveImageAndSetResult() {
        loading.setVisibility(View.VISIBLE);

        // apply the selected Filter


        final Uri uri = FileUtils.insertImage(context, getContentResolver(), filteredBitmap, "Edited_Image", "edited image");
        if (uri == null) {
            Toast.makeText(context, "inserted uri is null", Toast.LENGTH_SHORT).show();
            return;
        }
        String path = FileUtils.getPath(context, uri);
        Intent intent = new Intent();
        intent.putExtra(GalleryConstants.MEDIA_PATH, path);
        intent.putExtra(GalleryConstants.MEDIA_URI, uri.toString());
        intent.putExtra(GalleryConstants.MEDIA_TYPE, "image");
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        originalBitmap.recycle();
        blurredBitmap.recycle();
        filteredBitmap.recycle();
        unfilteredBitmap.recycle();
    }

}