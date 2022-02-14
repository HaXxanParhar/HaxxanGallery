package com.drudotstech.customgallery.mycanvas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.drudotstech.customgallery.BaseActivity;
import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.croppy.croppylib.main.BitmapResult;
import com.drudotstech.customgallery.croppy.croppylib.main.BlurBitmapCallback;
import com.drudotstech.customgallery.croppy.croppylib.main.BlurTask;
import com.drudotstech.customgallery.filters.FilterAdapter;
import com.drudotstech.customgallery.filters.FilterModel;
import com.drudotstech.customgallery.filters.FilterTask;
import com.drudotstech.customgallery.filters.FilterType;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.SelectStickerCallback;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.StickersBottomSheet;
import com.drudotstech.customgallery.utils.AnimationHelper;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class EditorActivity2 extends BaseActivity implements FilterAdapter.FilterSelectionCallback,
        BlurBitmapCallback, SelectStickerCallback, FilterTask.ApplyFilterCallback {


    // ------------------------------------- C O N S T A N T S  ------------------------------------
    private final Context context = EditorActivity2.this;
    public Bitmap originalBitmap;
    public Bitmap blurredBitmap;


    // ---------------------------------------- V I E W S ------------------------------------------
    private View ivFilter, ivAdjust, ivStickers, ivText;
    private View mainToolbar, secondToolbar;
    private ImageView ivOriginal, ivBlurred;
    private RelativeLayout rlCanvas;
    private View ivBack, ivClose, tvSave, tvDone;
    private TextView tvToolbarName;
    private View llStickers, ivAddSticker;
    private View llAdjust, llBrightness, llSaturation, llContrast, llWarmth, llSlider;
    private TextView tvSlider;
    private Slider slider;
    private View loading;

    private StickersBottomSheet stickersBottomSheet;

    // ------------------------------------ R E C Y C L E R V I E W --------------------------------
    private RecyclerView rvFilters;
    private List<FilterModel> filters;
    private FilterAdapter filterAdapter;
    private LinearLayoutManager filtersLayoutManager;
    private int selectedFilterPosition;


    // -------------------------------------- V A R I A B L E S ------------------------------------
    private MyCanvas myCanvas;
    private CanvasState memento;
    private AnimationHelper animationHelper = null;
    private Bitmap tempBitmap;
    private FilterType filterType;
    private String media;
    private int brightness = 50;
    private int contrast = 50;
    private int saturation = 50;
    private int warmth = 50;
    private int selected = -1; // 0 , 1, 2, 3, 4 -> for bottom options

    // to only scroll once per scrolling
    private boolean scrolled = false;


    // -------------------------------------- L A U N C H E R S ------------------------------------
    private ActivityResultLauncher<Intent> someLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == RESULT_OK) {

            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor2);

        //--------------------------------- I N I T   V I E W S ------------------------------------
        mainToolbar = findViewById(R.id.main_action_bar);
        ivBack = findViewById(R.id.iv_back);
        tvSave = findViewById(R.id.tv_next);

        secondToolbar = findViewById(R.id.second_action_bar);
        ivClose = findViewById(R.id.iv_close);
        tvToolbarName = findViewById(R.id.tv_actionbar_name);
        tvDone = findViewById(R.id.tv_done);

        ivOriginal = findViewById(R.id.iv_original_image);
        ivBlurred = findViewById(R.id.iv_blurred_image);
        rlCanvas = findViewById(R.id.rl_canvas);

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

        llStickers = findViewById(R.id.ll_stickers);
        ivAddSticker = findViewById(R.id.iv_sticker);


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


        // ------------------- Filter -----------------------
        ivFilter.setOnClickListener(view -> {
            selected = 0;
            showSecondMenu(true);
            saveCurrentState();
            applyFilter(selectedFilterPosition);
        });

        // ------------------- Adjust -----------------------
        ivAdjust.setOnClickListener(view -> {
            selected = 1;
            showSecondMenu(true);
            saveCurrentState();
        });

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


        // --------------- Stickers -----------------------
        ivStickers.setOnClickListener(view -> {
            selected = 2;
            showSecondMenu(true);
            saveCurrentState();
        });

        ivAddSticker.setOnClickListener(v -> {
            if (stickersBottomSheet == null)
                stickersBottomSheet = new StickersBottomSheet(this);
            stickersBottomSheet.show(getSupportFragmentManager(), "stickers");
        });


        // --------------- Others -----------------------

        // save the current changes
        tvDone.setOnClickListener(view -> saveChanges());

        // close without applying changes
        ivClose.setOnClickListener(view -> {
            if (selected > 3) { // Slider of adjust options are opened, so don't reset
                showSecondMenu(false);
            } else {
                revertBackToPreviousState();
                showSecondMenu(false);
            }
        });

        // save the final bitmap
        tvSave.setOnClickListener(view -> saveImageAndSetResult());

        // close the activity
        ivBack.setOnClickListener(view -> hideMenuOrBack());

    }

    private void saveCurrentState() {
        // create a memento of current canvas state to revert back when changes are not saved
        memento = myCanvas.getCurrentCanvasState();
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
//                        rlCanvas.setBrightness(brightness / 50.0f);
                        break;

                    case 5:
                        contrast = value;
                        tvSlider.setText(contrast + "");
//                        ivImageFilterView.setContrast(contrast / 50.0f);
                        break;

                    case 6:
                        saturation = value;
                        tvSlider.setText(saturation + "");
//                        ivImageFilterView.setSaturation(saturation / 50.0f);
                        break;

                    case 7:
                        warmth = value;
                        tvSlider.setText(warmth + "");
                        if (warmth == 50) {
//                            ivImageFilterView.setWarmth(1);
                        } else if (warmth > 50) {
//                            ivImageFilterView.setWarmth(2);
                        } else {
//                            ivImageFilterView.setWarmth(5);
                        }
                        break;
                }
            }
        });
    }

    private void revertBackToPreviousState() {

        if (memento != null)
            myCanvas.updateFromCanvasState(memento);

        // resetting image filter view
        brightness = 50;
        contrast = 50;
        saturation = 50;
        warmth = 50;
    }

    private void saveChanges() {

        if (selected > 3) {
            showSecondMenu(false);
        }

        switch (selected) {
            case 0:
            case 1:
            case 2:
            case 3:
                showSecondMenu(false);
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
                    animationHelper.moveFromBottomToTop(llStickers, 66f);
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

        // Hide/close Views
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
                    animationHelper.moveToBottom(llStickers, 100f);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = -1;
                    myCanvas.resetStickerSelection();
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

                            ivOriginal.setImageBitmap(resource);
                            initCanvas(null);

                            blurredBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
                            blurredBitmap = CanvasUtils.getScreenFillBitmap(context, blurredBitmap);
                            new BlurTask(context, blurredBitmap, 32, EditorActivity2.this).execute();
                        }
                    });

        } catch (Exception e) {
            hideLoading();
            Toast.makeText(context, "Exception in load bitmap : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initCanvas(Bitmap blurredBitmap) {
        // create canvas
        myCanvas = new MyCanvas(this);

        // add canvas to the relative layout
        rlCanvas.addView(myCanvas);

        // add the first layer
        Bitmap tempBitmap = CanvasUtils.getScreenFitBitmap(context, originalBitmap);
        RectF rectF = CanvasUtils.getCenteredRect(context, tempBitmap);
        myCanvas.addLayer(new LayerModel( tempBitmap, rectF, FilterType.NO_FILTER));
    }

    @Override
    public void onBlurCompleted(BitmapResult bitmapResult) {
        hideLoading();
        if (bitmapResult.isStatus()) {
            blurredBitmap = bitmapResult.getBitmap();
            ivBlurred.setImageBitmap(blurredBitmap);
            myCanvas.addBackgroundBitmap(blurredBitmap);

        } else {
            final Exception exception = bitmapResult.getException();
            Toast.makeText(context, "Exception : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * When filter is selected from the filters RecyclerView
     */
    @Override
    public void onFilterSelected(int position) {
        selectedFilterPosition = position;
        applyFilter(position);
    }

    private void applyFilter(int position) {
        FilterModel filter = filters.get(position);
        filterType = filter.getFilterType();

        // create a temp bitmap from original bitmap & apply filter to temp bitmap
        Bitmap tempBitmap = CanvasUtils.getScreenFitBitmap(context, originalBitmap);

        // apply filter
        new FilterTask(context, tempBitmap, filterType, this).execute();


        // to keep the filters in center
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
        if (x > threshold) {
            rvFilters.smoothScrollBy(width, 0);
        } else {
            rvFilters.smoothScrollBy(-width, 0);
        }
    }

    /**
     * When filter is applied, it returns the bitmap
     */
    @Override
    public void onFilterApplied(BitmapResult bitmapResult) {
        if (bitmapResult.isStatus()) {
            // add temp bitmap in the canvas
            Bitmap tempBitmap = bitmapResult.getBitmap();
            RectF rectF = CanvasUtils.getCenteredRect(context, tempBitmap);
            LayerModel filterLayer = new LayerModel( tempBitmap, rectF, filterType);
            myCanvas.addLayer(filterLayer);
        } else {
            final Exception exception = bitmapResult.getException();
            Toast.makeText(context, "Exception while applying filter : " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When Sticker is selected from the bottom sheet
     */
    @Override
    public void onStickerSelected(int stickerSrc) {
        try {
            Bitmap sticker = CanvasUtils.getBitmapFromVector(context, stickerSrc);
            StickerView stickerView = new StickerView(context, sticker,myCanvas.screenRect);
            LayerModel filterLayer = new LayerModel( stickerView);
            myCanvas.addLayer(filterLayer);

            if (stickersBottomSheet != null)
                stickersBottomSheet.dismiss();

        } catch (Exception e) {
            Toast.makeText(context, "Exception while creating Bitmap from Vector : " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            revertBackToPreviousState();
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
//        loading.setVisibility(View.VISIBLE);

        // apply the selected Filter


//        final Uri uri = FileUtils.insertImage(context, getContentResolver(), editedBitmap, "Edited_Image", "edited image");
//        if (uri == null) {
//            Toast.makeText(context, "inserted uri is null", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        String path = FileUtils.getPath(context, uri);
//        Intent intent = new Intent();
//        intent.putExtra(GalleryConstants.MEDIA_PATH, path);
//        intent.putExtra(GalleryConstants.MEDIA_URI, uri.toString());
//        intent.putExtra(GalleryConstants.MEDIA_TYPE, "image");
//        setResult(RESULT_OK, intent);
//        finish();
//        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        originalBitmap.recycle();
        blurredBitmap.recycle();
    }

}