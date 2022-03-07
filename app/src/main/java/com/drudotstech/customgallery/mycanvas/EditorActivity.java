package com.drudotstech.customgallery.mycanvas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.drudotstech.customgallery.gallery.utils.GalleryConstants;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.EmojisBottomSheet;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.SelectEmojiCallback;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.SelectStickerCallback;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.SelectWidgetCallback;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.StickersBottomSheet;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.WidgetModel;
import com.drudotstech.customgallery.mycanvas.bottom_sheets.WidgetsBottomSheet;
import com.drudotstech.customgallery.mycanvas.models.AlignModel;
import com.drudotstech.customgallery.mycanvas.models.CanvasState;
import com.drudotstech.customgallery.mycanvas.models.LayerModel;
import com.drudotstech.customgallery.mycanvas.models.TextInfo;
import com.drudotstech.customgallery.mycanvas.my_color_picker.AlphaPicker;
import com.drudotstech.customgallery.mycanvas.my_color_picker.HuePicker;
import com.drudotstech.customgallery.mycanvas.my_color_picker.NumberPicker;
import com.drudotstech.customgallery.mycanvas.my_color_picker.SaturationPicker;
import com.drudotstech.customgallery.utils.AnimationHelper;
import com.drudotstech.customgallery.utils.FileUtils;
import com.drudotstech.customgallery.utils.MyUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n")
public class EditorActivity extends BaseActivity implements FilterAdapter.FilterSelectionCallback,
        BlurBitmapCallback, SelectStickerCallback, FilterTask.ApplyFilterCallback, SelectWidgetCallback,
        SelectEmojiCallback, TextFontAdapter.FontSelectionCallback, AddTextFragment.AddTextCallback,
        TextAlignAdapter.AlignmentSelectionCallback {

    // region --> V A R I A B L E S <--
    private static final int RC_LOCATION = 101;
    // ------------------------------------- C O N S T A N T S  ------------------------------------
    private final Context context = EditorActivity.this;
    public Bitmap originalBitmap;
    public Bitmap blurredBitmap;

    // ---------------------------------------- V I E W S ------------------------------------------
    private View ivFilter, ivAdjust, ivStickers, ivText, ivMore;
    private View secondToolbar;
    private RelativeLayout mainToolbar, rlBottom;
    private ImageView ivOriginal;
    private RelativeLayout rlCanvas;
    private View ivBack, ivClose, tvSave, tvDone;
    private TextView tvToolbarName;
    private View loading;

    // Filter module Views
    private RecyclerView rvFilters;
    private List<FilterModel> filters;
    private FilterAdapter filterAdapter;
    private LinearLayoutManager filtersLayoutManager;
    private int selectedFilterPosition;

    // Stickers module Views
    private View llStickers, ivAddSticker, ivAddWidgets, ivAddEmoji;
    private StickersBottomSheet stickersBottomSheet;
    private WidgetsBottomSheet widgetsBottomSheet;
    private EmojisBottomSheet emojisBottomSheet;

    // Adjust module Views
    private View llAdjust, llBrightness, llSaturation, llContrast, llWarmth, llSlider;
    private TextView tvSlider;
    private Slider slider;

    // Text module Views
    private View llText, llFont, llColor, llAlpha, llAlignment, ivKeyboard, llColorPickers, llAlphaPickers;
    private HuePicker huePicker;
    private SaturationPicker saturationPicker;
    private AlphaPicker alphaPicker;
    private List<View> lines;
    private RecyclerView rvFonts;
    private List<WidgetModel> fonts;
    private TextFontAdapter fontAdapter;
    private LinearLayoutManager fontsLayoutManager;
    private RecyclerView rvAlignments;
    private List<AlignModel> alignments;
    private TextAlignAdapter textAlignAdapter;
    private LinearLayoutManager alignmentsLayoutManager;

    // Drawer Layout Views
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View ivCloseDrawer;
    private View llRotate, llBlur, llBrush, llWhitener;

    // Rotate Flip Views
    private View llRotateModule, llRotateImage, llFlipHorizontal, llFlipVertical;

    // Drawing Views
    private MyBrushView brushView;
    private View llDrawModule, llBrushSize, llBrushColor, llBrushAlpha, llBrushSizePickers,
            llBrushColorPickers, llBrushAlphaPickers;
    private NumberPicker brushSizePicker;
    private HuePicker brushHuePicker;
    private SaturationPicker brushSaturationPicker;
    private AlphaPicker brushAlphaPicker;
    private List<View> lines2;


    // -------------------------------------- V A R I A B L E S ------------------------------------
    private MyCanvas myCanvas;
    private CanvasState memento;
    private LayerModel paintLayerMemento;
    private AnimationHelper animationHelper = null;
    private Bitmap tempBitmap;
    private FilterType filterType;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextInfo textInfo;
    private String media;

    private int selectedTextTab = 0;
    private int selectedDrawTab = 0;

    private float brightness = 50;
    private float contrast = 50;
    private float saturation = 50;
    private float hue = 50;
    private int blurAmount = 0;
    private Menu selected = Menu.NONE; // 0 , 1, 2, 3, 4 -> for bottom options

    // to only scroll once per scrolling
    private boolean scrolled = false;
    // endregion

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        initViews();

        //------------------------------  S E T U P  &   L O A D  ----------------------------------

        animationHelper = new AnimationHelper(300); // init the Animation helper class

        media = getIntent().getStringExtra("media"); // get media from previous activity

        initCanvas(); //create bitmap from media and load in imageView

        setFiltersRecyclerView();  // set Filters RecyclerView

        setFontsRecyclerView();  // set Fonts RecyclerView

        setAlignmentRecyclerView();  // set Text Alignment RecyclerView

        setSliderChangeListener();  // set listener for slider value change

        setTextColorPickerListeners();  // set listener of Color Pickers for Text Module

        setBrushPickersListeners(); // set the pickers listeners for the Drawing Module

        // init location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


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
            selected = Menu.FILTERS;
            showSecondMenu(true);
            saveCurrentState();
            applyFilter(selectedFilterPosition);
        });


        // ------------------- Adjust -----------------------
        ivAdjust.setOnClickListener(view -> {
            selected = Menu.ADJUST;
            showSecondMenu(true);

            // add the paint layer if not already added
            myCanvas.addPaintLayer();

            // save the current canvas state
            saveCurrentState();
        });

        llBrightness.setOnClickListener(view -> {
            selected = Menu.BRIGHTNESS;
            // save the current paint layer
            paintLayerMemento = myCanvas.getPaintLayer();

            showSecondMenu(true);
        });

        llContrast.setOnClickListener(view -> {
            selected = Menu.CONTRAST;
            showSecondMenu(true);
            // save the current paint layer
            paintLayerMemento = myCanvas.getPaintLayer();
        });

        llSaturation.setOnClickListener(view -> {
            selected = Menu.SATURATION;
            showSecondMenu(true);
            // save the current paint layer
            paintLayerMemento = myCanvas.getPaintLayer();
        });

        llWarmth.setOnClickListener(view -> {
            selected = Menu.HUE;
            showSecondMenu(true);
            // save the current paint layer
            paintLayerMemento = myCanvas.getPaintLayer();
        });


        // --------------- Stickers -----------------------
        ivStickers.setOnClickListener(view -> {
            selected = Menu.STICKERS;
            showSecondMenu(true);
            saveCurrentState();
//            myCanvas.setSelectionEnabled(true);
        });

        ivAddSticker.setOnClickListener(v -> {
            if (stickersBottomSheet == null)
                stickersBottomSheet = new StickersBottomSheet(this);
            stickersBottomSheet.show(getSupportFragmentManager(), "stickers");
        });

        ivAddWidgets.setOnClickListener(v -> {
            checkLocationPermission();
        });

        ivAddEmoji.setOnClickListener(v -> {
            if (emojisBottomSheet == null)
                emojisBottomSheet = new EmojisBottomSheet(this);
            emojisBottomSheet.show(getSupportFragmentManager(), "emojis");
        });


        // --------------- Text Module -----------------------
        ivText.setOnClickListener(v -> {
            selected = Menu.ADD_TEXT;
            showSecondMenu(true);
            saveCurrentState();
//            myCanvas.setSelectionEnabled(true);

            // check if no text layer is added already
            final int index = myCanvas.findStickerViewLayer(StickerView.EDITABLE_TEXT);
            if (index == -1) {
                openAddTextFragment();
            }
        });

        llFont.setOnClickListener(v -> {
            selectTextTab(0);
        });

        llColor.setOnClickListener(v -> {
            selectTextTab(1);
        });

        llAlpha.setOnClickListener(v -> {
            selectTextTab(2);
        });

        llAlignment.setOnClickListener(v -> {
            selectTextTab(3);
        });

        ivKeyboard.setOnClickListener(v -> {
            openAddTextFragment();
        });


        // --------------- Drawer  -----------------------
        ivMore.setOnClickListener(v -> drawer.openDrawer(GravityCompat.END));
        ivCloseDrawer.setOnClickListener(v -> drawer.closeDrawer(GravityCompat.END));


        // ---------------  Rotate & Flip  -----------------------
        llRotate.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.END);
            selected = Menu.ROTATE_FLIP;
            showSecondMenu(true);
            saveCurrentState();
        });

        llRotateImage.setOnClickListener(v -> myCanvas.rotateImage(90));

        llFlipHorizontal.setOnClickListener(v -> myCanvas.flipHorizontal());

        llFlipVertical.setOnClickListener(v -> myCanvas.flipVertical());


        // ---------------  Blur  -----------------------
        llBlur.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.END);
            selected = Menu.BLUR;
            showSecondMenu(true);
            saveCurrentState();
        });


        // ---------------  Draw with Brush  -----------------------
        llBrush.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.END);
            selected = Menu.BRUSH;
            showSecondMenu(true);
            saveCurrentState();
            myCanvas.setDrawingEnabled(true);
        });

        llBrushSize.setOnClickListener(v -> {
            selectDrawTab(0);
        });

        llBrushColor.setOnClickListener(v -> {
            selectDrawTab(1);
        });

        llBrushAlpha.setOnClickListener(v -> {
            selectDrawTab(2);
        });


        // --------------- Others -----------------------
        tvDone.setOnClickListener(view -> {

            // hide the main menus
            switch (selected) {
                case FILTERS:
                case ADJUST:
                case STICKERS:
                case ADD_TEXT:
                case ROTATE_FLIP:
                case BLUR:
                case BRUSH:
                    showSecondMenu(false);
                    myCanvas.setDrawingEnabled(false);
                    break;
            }

//            myCanvas.setSelectionEnabled(false); // disable selection

            // selected is within the adjust menu range
            if (selected == Menu.BRIGHTNESS
                    || selected == Menu.CONTRAST
                    || selected == Menu.SATURATION
                    || selected == Menu.HUE) {
                // update the adjust memento
                paintLayerMemento = myCanvas.getPaintLayer();
                showSecondMenu(false);
            }
        });

        // close without applying changes
        ivClose.setOnClickListener(view -> {

            // Slider of adjust options are opened, so don't revert
            if (selected == Menu.BRIGHTNESS
                    || selected == Menu.CONTRAST
                    || selected == Menu.SATURATION
                    || selected == Menu.HUE) {
                showSecondMenu(false);

                // revert back to the adjust memento
                if (paintLayerMemento != null)
                    myCanvas.setPaintLayer(paintLayerMemento);

            } else {
                revertBackToPreviousState(); // revert to memento state
//                myCanvas.setSelectionEnabled(false); // disable selection
                showSecondMenu(false);
                myCanvas.setDrawingEnabled(false);
            }
        });

        // save the final bitmap
        tvSave.setOnClickListener(view -> saveImageAndSetResult());

        // close the activity
        ivBack.setOnClickListener(view -> hideMenuOrBack());

    }

    private void initViews() {
        rlBottom = findViewById(R.id.rl_bottom);
        mainToolbar = findViewById(R.id.main_action_bar);
        ivBack = findViewById(R.id.iv_back);
        navigationView = findViewById(R.id.navigation_view_editor);
        drawer = findViewById(R.id.drawer_layout_editor);
        ivCloseDrawer = findViewById(R.id.iv_close_drawer);

        tvSave = findViewById(R.id.tv_next);
        secondToolbar = findViewById(R.id.second_action_bar);
        secondToolbar.setVisibility(View.GONE);
        ivClose = findViewById(R.id.iv_close);
        tvToolbarName = findViewById(R.id.tv_actionbar_name);
        tvDone = findViewById(R.id.tv_done);

        ivOriginal = findViewById(R.id.iv_original_image);
        rlCanvas = findViewById(R.id.rl_canvas);

        ivFilter = findViewById(R.id.iv_filter);
        ivAdjust = findViewById(R.id.iv_adjust);
        ivStickers = findViewById(R.id.iv_stickers);
        ivText = findViewById(R.id.iv_text);

        rvFilters = findViewById(R.id.rv_filters);
        rvFilters.setVisibility(View.GONE);

        llAdjust = findViewById(R.id.ll_adjust);
        llAdjust.setVisibility(View.GONE);
        llBrightness = findViewById(R.id.ll_brightness);
        llContrast = findViewById(R.id.ll_contrast);
        llSaturation = findViewById(R.id.ll_saturation);
        llWarmth = findViewById(R.id.ll_warmth);
        llSlider = findViewById(R.id.rl_slider);
        tvSlider = findViewById(R.id.tv_slider);
        slider = findViewById(R.id.slider);
        loading = findViewById(R.id.rl_loading);

        llStickers = findViewById(R.id.ll_stickers);
        llStickers.setVisibility(View.GONE);
        ivAddSticker = findViewById(R.id.iv_sticker);
        ivAddWidgets = findViewById(R.id.iv_widget);
        ivAddEmoji = findViewById(R.id.iv_emoji);

        llText = findViewById(R.id.ll_text);
        llText.setVisibility(View.GONE);
        llFont = findViewById(R.id.ll_text_fonts);
        llColor = findViewById(R.id.ll_text_color);
        llAlpha = findViewById(R.id.ll_text_opacity);
        llAlignment = findViewById(R.id.ll_text_alignment);
        ivKeyboard = findViewById(R.id.iv_keyboard);
        llColorPickers = findViewById(R.id.ll_color_pickers);
        huePicker = findViewById(R.id.hue_picker);
        saturationPicker = findViewById(R.id.saturation_picker);
        llAlphaPickers = findViewById(R.id.ll_alpha_pickers);
        alphaPicker = findViewById(R.id.alpha_picker);
        rvFonts = findViewById(R.id.rv_text_font);
        rvAlignments = findViewById(R.id.rv_text_alignment);
        lines = new ArrayList<>();
        lines.add(findViewById(R.id.line_1));
        lines.add(findViewById(R.id.line_2));
        lines.add(findViewById(R.id.line_3));
        lines.add(findViewById(R.id.line_4));

        ivMore = findViewById(R.id.iv_setting);
        llRotate = findViewById(R.id.ll_rotate);
        llBlur = findViewById(R.id.ll_blur);
        llBrush = findViewById(R.id.ll_brush);

        llRotateModule = findViewById(R.id.ll_rotate_module);
        llRotateImage = findViewById(R.id.ll_rotate_image);
        llFlipHorizontal = findViewById(R.id.ll_flip_horizontal);
        llFlipVertical = findViewById(R.id.ll_flip_vertical);

        brushView = findViewById(R.id.brush_view);
        llDrawModule = findViewById(R.id.ll_draw_module);
        llBrushSize = findViewById(R.id.ll_brush_size);
        llBrushColor = findViewById(R.id.ll_brush_color);
        llBrushAlpha = findViewById(R.id.ll_brush_alpha);
        llBrushSizePickers = findViewById(R.id.ll_size_pickers);
        llBrushColorPickers = findViewById(R.id.ll_brush_color_pickers);
        llBrushAlphaPickers = findViewById(R.id.ll_brush_alpha_pickers);
        brushSizePicker = findViewById(R.id.size_picker);
        brushHuePicker = findViewById(R.id.brush_hue_picker);
        brushSaturationPicker = findViewById(R.id.brush_saturation_picker);
        brushAlphaPicker = findViewById(R.id.brush_alpha_picker);
        lines2 = new ArrayList<>();
        lines2.add(findViewById(R.id.brush_line_1));
        lines2.add(findViewById(R.id.brush_line_2));
        lines2.add(findViewById(R.id.brush_line_3));
    }

    private void initCanvas() {
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

                            // create canvas
                            myCanvas = new MyCanvas(context);

                            // add canvas to the relative layout
                            rlCanvas.addView(myCanvas);

                            // set height & width
                            final int bottomHeight = rlBottom.getLayoutParams().height;
                            final int topHeight = mainToolbar.getLayoutParams().height;
                            CanvasUtils.offsetFromScreen = (float) (bottomHeight + topHeight);

                            float screenWidth = CanvasUtils.getScreenWidth(context);
                            float screenHeight = CanvasUtils.getScreenHeight(context);
                            final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myCanvas.getLayoutParams();
                            layoutParams.height = (int) screenHeight - topHeight - bottomHeight;
                            layoutParams.width = (int) screenWidth;
                            myCanvas.setLayoutParams(layoutParams);
                            myCanvas.init();

                            // add the first layer
                            Bitmap tempBitmap = CanvasUtils.getScreenFitBitmap(context, originalBitmap);
                            RectF rectF = CanvasUtils.getCenteredRect(context, tempBitmap);
                            myCanvas.addLayer(new LayerModel(tempBitmap, rectF, FilterType.NO_FILTER));

                            setCanvasListeners();

                            blurredBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
                            blurredBitmap = CanvasUtils.getScreenFillBitmap(context, blurredBitmap);
                            new BlurTask(context, blurredBitmap, 32, EditorActivity.this).execute();
                        }
                    });
        } catch (Exception e) {
            hideLoading();
            Toast.makeText(context, "Exception in load bitmap : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setCanvasListeners() {

        // When clicked on a Layer. Useful for editing the sticker containing Text
        myCanvas.setOnLayerClickListener(new MyCanvas.OnLayerClickListener() {
            @Override
            public void onLayerClick(LayerModel layerModel) {
                Toast.makeText(context, "clicked!", Toast.LENGTH_SHORT).show();

                // If Text Module is opened
                if (selected == Menu.ADD_TEXT
                        && layerModel.type == LayerModel.STICKER
                        && layerModel.sticker != null
                        && layerModel.sticker.stickerType == StickerView.EDITABLE_TEXT
                        && layerModel.textInfo != null) {

                    Toast.makeText(context, "layer clicked :" + layerModel.sticker.text, Toast.LENGTH_SHORT).show();

                    textInfo = layerModel.textInfo.copy();

                    // set font
                    fontAdapter.setSelectedFont(textInfo.fontIndex);

                    // set text alignment
                    textAlignAdapter.setSelectedAlignment(textInfo.alignmentIndex);

                    // set color pickers
                    huePicker.updatePosition(textInfo.huePosition);
                    saturationPicker.updatePosition(textInfo.saturationPosition);
                    alphaPicker.updatePosition(textInfo.alphaPosition);

                    String stickerText = layerModel.sticker.text;
                    openEditTextFragment(stickerText);
                }
            }
        });

        /**
         * When a Layer is selected. Not same as clicked because layer selection happens on Touch Down Before click is decided
         */
        myCanvas.setOnLayerSelectListener(new MyCanvas.OnLayerSelectListener() {
            @Override
            public void onLayerSelected(LayerModel layerModel, int layerIndex) {
                Toast.makeText(context, "Layer " + layerIndex + " selected!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //---------------------------------------  S E T U P  -----------------------------------------

    /**
     * When blur result is returned
     */
    @Override
    public void onBlurCompleted(BitmapResult bitmapResult) {
        if (bitmapResult.isStatus()) {

            // blur task is called for background
            if (selected == Menu.NONE) {
                hideLoading();
                blurredBitmap = bitmapResult.getBitmap();
                myCanvas.addBackgroundBitmap(blurredBitmap);
            }

            // blur task is called from the Slider/Blur menu
            else if (selected == Menu.BLUR) {
                myCanvas.blur(bitmapResult.getBitmap(), blurAmount);
            }
        } else {
            final Exception exception = bitmapResult.getException();
            Toast.makeText(context, "Exception : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDrawerLayout() {
        drawer.closeDrawer(GravityCompat.END);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull @NotNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull @NotNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull @NotNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void saveCurrentState() {
        // create a memento of current canvas state to revert back when changes are not saved
        memento = myCanvas.getCurrentCanvasState();
    }

    private void revertBackToPreviousState() {
        if (memento != null)
            myCanvas.updateFromCanvasState(memento);
    }


    //---------------------------------  C O M M O N   M E T H O D S  ------------------------------

    private void saveChanges() {

    }

    private void showSecondMenu(boolean shouldShowSecondToolbar) {

        // Show Views
        if (shouldShowSecondToolbar) {

            // update the toolbar title
            switch (selected) {
                case FILTERS:
                    tvToolbarName.setText("Filters");
                    animationHelper.moveFromBottomToTop(rvFilters, 66f);
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    break;

                case ADJUST:
                    tvToolbarName.setText("Adjust");
                    animationHelper.moveFromBottomToTop(llAdjust, 66f);
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    break;

                case STICKERS:
                    tvToolbarName.setText("Stickers");
                    animationHelper.moveFromBottomToTop(llStickers, 66f);
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    break;

                case ADD_TEXT:
                    tvToolbarName.setText("Text");
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    // show the text module
                    animationHelper.moveFromBottomToTop(llText, 200f);
                    // make selection from the Text Tab
                    selectTextTab(selectedTextTab);
                    break;

                //  --------------- sub menus ------------------
                case BRIGHTNESS:
                    tvToolbarName.setText("Brightness");
                    slider.setValue((int) brightness);
                    tvSlider.setText(((int) brightness) + "");
                    showSlider(true);
                    break;

                case CONTRAST:
                    tvToolbarName.setText("Contrast");
                    slider.setValue((int) contrast);
                    tvSlider.setText(((int) contrast) + "");
                    showSlider(true);
                    break;

                case SATURATION:
                    tvToolbarName.setText("Saturation");
                    slider.setValue((int) saturation);
                    tvSlider.setText(((int) saturation) + "");
                    showSlider(true);
                    break;

                case HUE:
                    tvToolbarName.setText("Hue");
                    slider.setValue((int) hue);
                    tvSlider.setText(((int) hue) + "");
                    showSlider(true);
                    break;


                // -------------------- Drawer Menus ----------------------
                case ROTATE_FLIP:
                    tvToolbarName.setText("Rotate");
                    // show the rotate module menus from bottom
                    animationHelper.moveFromBottomToTop(llRotateModule, 66f);
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    break;

                case BLUR:
                    final LayerModel firstLayer = myCanvas.getFirstLayer();
                    if (firstLayer != null) {
                        tvToolbarName.setText("Blur");
                        // show the slider from bottom
                        slider.setValue(firstLayer.blurAmount);
                        tvSlider.setText(firstLayer.blurAmount + "");
                        showSlider(true);
                        // show the second action bar
                        animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    } else {
                        selected = Menu.NONE;
                    }
                    break;

                case BRUSH:
                    tvToolbarName.setText("Draw");
                    // show the second action bar
                    animationHelper.moveFromTopToBottom(secondToolbar, 48f);
                    // show the draw module
                    animationHelper.moveFromBottomToTop(llDrawModule, 200f);
                    // make selection from the Draw Tab
                    selectDrawTab(selectedDrawTab);
                    break;
            }
        }

        // Hide/close Views
        else {

            switch (selected) {
                case FILTERS:
                    animationHelper.moveToBottom(rvFilters, 100f);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = Menu.NONE;

                    break;
                case ADJUST:
                    animationHelper.moveToBottom(llAdjust, 100f);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = Menu.NONE;
                    break;


                case STICKERS:
                    animationHelper.moveToBottom(llStickers, 100f);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = Menu.NONE;
                    myCanvas.resetStickerSelection();
                    break;

                case ADD_TEXT:
                    // hide the bottom view as well
                    animationHelper.moveToBottom(llText, 200f);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = Menu.NONE;
                    break;


                ///  --------- sub menus ------------------
                case BRIGHTNESS:
                case CONTRAST:
                case SATURATION:
                case HUE:
                    tvToolbarName.setText("Adjust");
                    showSlider(false);
                    selected = Menu.ADJUST;
                    break;

                // -------------------- Drawer Menus ----------------------
                case ROTATE_FLIP:
                    // hide the rotate menu to bottom
                    animationHelper.moveToBottom(llRotateModule, 100f);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = Menu.NONE;
                    break;

                case BLUR:
                    // hide the slider
                    showSlider(false);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = Menu.NONE;
                    break;

                case BRUSH:
                    // hide the bottom view as well
                    animationHelper.moveToBottom(llDrawModule, 200f);
                    // hide the second action bar
                    animationHelper.moveToTop(secondToolbar, 48f);
                    selected = Menu.NONE;
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

    @Override
    public void onBackPressed() {
        hideMenuOrBack();
    }

    private void hideMenuOrBack() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else if (selected == Menu.BRIGHTNESS
                || selected == Menu.CONTRAST
                || selected == Menu.SATURATION
                || selected == Menu.HUE) { // more menus are opened, hide them first
            showSecondMenu(false);
        } else if (selected != Menu.NONE) {
            revertBackToPreviousState();
            showSecondMenu(false);
            // todo : unselect all the layers of the canvas
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

        myCanvas.setDrawingCacheEnabled(true);
        myCanvas.buildDrawingCache();
        final Bitmap editedBitmap = myCanvas.getDrawingCache();
        myCanvas.setDrawingCacheEnabled(false);

        final Uri uri = FileUtils.insertImage(context, getContentResolver(), editedBitmap, "Edited_Image", "edited image");
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
    }


    //----------------------------------------- F I L T E R S --------------------------------------
    private void setFiltersRecyclerView() {
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
        rvFilters.setHasFixedSize(true);
        rvFilters.setLayoutManager(filtersLayoutManager);
        rvFilters.setAdapter(filterAdapter);
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

        // to keep the filters recyclerView in center
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
            LayerModel filterLayer = new LayerModel(tempBitmap, rectF, filterType);
            myCanvas.addLayer(filterLayer);
        } else {
            final Exception exception = bitmapResult.getException();
            Toast.makeText(context, "Exception while applying filter : " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    //------------------------------------------  A D J U S T  -------------------------------------
    @SuppressLint("RestrictedApi")
    private void setSliderChangeListener() {

        slider.addOnChangeListener((slider, value, fromUser) -> {
            switch (selected) {
                case BRIGHTNESS:
                    brightness = value;
                    tvSlider.setText(((int) brightness) + "");
                    myCanvas.adjustColor(brightness, LayerModel.BRIGHTNESS);
                    break;

                case CONTRAST:
                    contrast = value;
                    tvSlider.setText(((int) contrast) + "");
                    myCanvas.adjustColor(contrast, LayerModel.CONTRAST);
                    break;

                case SATURATION:
                    saturation = value;
                    tvSlider.setText(((int) saturation) + "");
                    myCanvas.adjustColor(saturation, LayerModel.SATURATION);
                    break;

                case HUE:
                    hue = value;
                    tvSlider.setText(((int) hue) + "");
                    myCanvas.adjustColor(hue, LayerModel.WARMTH);
                    break;
            }
        });

        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                switch (selected) {
                    case BLUR:
                        final LayerModel firstLayer = myCanvas.getFirstLayer();
                        if (firstLayer != null) {
                            blurAmount = (int) slider.getValue();
                            tvSlider.setText(blurAmount + "");
                            Bitmap tempBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
                            new BlurTask(context, tempBitmap, blurAmount / 4, EditorActivity.this).execute();
                        }
                        break;
                }
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            } else {
                openBottomSheet(null, null);
            }
        }
    }


    //----------------------------------------  S T I K E R S  -------------------------------------
    private void openBottomSheet(String location, String shortLocation) {
        if (widgetsBottomSheet == null)
            widgetsBottomSheet = new WidgetsBottomSheet(location, shortLocation, this);
        widgetsBottomSheet.show(getSupportFragmentManager(), "widgets");
    }

    private void getDeviceLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location location = task.getResult();
                        if (location != null) {
                            final String address = MyUtils.getAddress(context, location.getLatitude(), location.getLongitude());
                            final String shortAddress = MyUtils.getCityCountry(context, location.getLatitude(), location.getLongitude());
                            EditorActivity.this.openBottomSheet(address, shortAddress);
                        } else {
                            EditorActivity.this.openBottomSheet(null, null);
                        }
                    } else {
                        Toast.makeText(context, "unable to get the location", Toast.LENGTH_SHORT).show();
                        EditorActivity.this.openBottomSheet(null, null);
                    }
                }
            });
        } catch (Exception e) {
            openBottomSheet(null, null);
            Toast.makeText(context, "Exception while getting device location : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * When Sticker is selected from the bottom sheet
     */
    @Override
    public void onStickerSelected(int stickerSrc) {
        try {
            // create bitmap
            Bitmap sticker = CanvasUtils.getBitmapFromVector(context, stickerSrc);
            StickerView stickerView = new StickerView(context, sticker, myCanvas.screenRect);
            // create layer and add in layers of canvas
            LayerModel filterLayer = new LayerModel(stickerView);
            myCanvas.addLayer(filterLayer);

            // close the bottom sheet
            if (stickersBottomSheet != null)
                stickersBottomSheet.dismiss();

        } catch (Exception e) {
            Toast.makeText(context, "Exception while creating Bitmap from Vector : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When Widget is selected from the bottom sheet
     */
    @Override
    public void onWidgetSelected(WidgetModel widgetModel, Bitmap bitmap) {

        try {
            // get appropriate text size for the widget
            final float textSize = MyUtils.getTextSize(widgetModel.getText()) * 2.5f;

            // create paint from the widget font
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            textPaint.setTypeface(ResourcesCompat.getFont(context, widgetModel.getFont()));
            textPaint.setTextSize(textSize);
            StickerView stickerView = new StickerView(context, widgetModel.getText(), textPaint
                    , myCanvas.screenRect, StickerView.TEXT, (int) textSize);

            // create layer and add in layers of canvas
            LayerModel filterLayer = new LayerModel(stickerView);
            myCanvas.addLayer(filterLayer);

            // close the bottom sheet
            if (widgetsBottomSheet != null)
                widgetsBottomSheet.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception while creating Bitmap from Vector : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When Emoji is selected from the bottom sheet
     */
    @Override
    public void onEmojiSelected(String emoji, Bitmap bitmap) {
//        Paint textPaint = new Paint();
//        textPaint.setAntiAlias(true);
//        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.metropolis_regular));
//        textPaint.setTextSize(100);
//        StickerView stickerView = new StickerView(context, emoji, textPaint, R.font.metropolis_regular,
//                myCanvas.screenRect, StickerView.EMOJI);

        final StickerView stickerView = new StickerView(context, bitmap, myCanvas.screenRect);
        // create layer and add in layers of canvas
        LayerModel filterLayer = new LayerModel(stickerView);
        myCanvas.addLayer(filterLayer);

        // close the bottom sheet
        if (emojisBottomSheet != null)
            emojisBottomSheet.dismiss();
    }

    private void setFontsRecyclerView() {
        // create list of fonts
        fonts = new ArrayList<>();
        fonts.add(new WidgetModel("Text", R.font.metropolis_regular, true));
        fonts.add(new WidgetModel("Text", R.font.muchomacho));
        fonts.add(new WidgetModel("Text", R.font.shake_it_off));
        fonts.add(new WidgetModel("Text", R.font.bubble_love));
        fonts.add(new WidgetModel("Text", R.font.metropolis_bold));
        fonts.add(new WidgetModel("Text", R.font.cheri));
        fonts.add(new WidgetModel("Text", R.font.metropolis_medium));

        // also assign the selected Font
        if (textInfo == null) textInfo = new TextInfo();
        textInfo.fontIndex = 0;
        textInfo.typeface = ResourcesCompat.getFont(context, R.font.metropolis_regular);

        // set recyclerView
        fontAdapter = new TextFontAdapter(context, fonts, this);
        fontsLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvFonts.setHasFixedSize(true);
        rvFonts.setLayoutManager(fontsLayoutManager);
        rvFonts.setAdapter(fontAdapter);
    }

    private void setAlignmentRecyclerView() {
        // create list of alignments
        alignments = new ArrayList<>();
        alignments.add(new AlignModel(Paint.Align.LEFT, R.drawable.ic_baseline_format_align_left_24, true));
        alignments.add(new AlignModel(Paint.Align.CENTER, R.drawable.ic_baseline_format_align_center_24));
        alignments.add(new AlignModel(Paint.Align.RIGHT, R.drawable.ic_baseline_format_align_right_24));

        // also assign the selected Font
        if (textInfo == null) textInfo = new TextInfo();
        textInfo.alignmentIndex = 0;
        textInfo.align = Paint.Align.LEFT;

        // set recyclerView
        textAlignAdapter = new TextAlignAdapter(context, alignments, this);
        alignmentsLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvAlignments.setHasFixedSize(true);
        rvAlignments.setLayoutManager(alignmentsLayoutManager);
        rvAlignments.setAdapter(textAlignAdapter);
    }


    //--------------------------------------  A D D   T E X T  -------------------------------------
    private void setTextColorPickerListeners() {

        // set default color picker values
        huePicker.setPosition(50); // to show thumb in center
        saturationPicker.setPosition(0); // to show the white color
        alphaPicker.setPosition(100); // to show the 100% opacity/alpha

        // connect pickers so the color retured will be combination of all these instead of a color from single picker
        huePicker.connect(saturationPicker, alphaPicker);
        saturationPicker.connect(huePicker, alphaPicker);
        alphaPicker.connect(huePicker, saturationPicker);


        // create color as result of all three pickers
        int defaultColor = getColorFromHSVA(huePicker.getCurrentHue(), saturationPicker.getCurrentSaturation(),
                saturationPicker.getCurrentValue(), alphaPicker.getCurrentAlpha());

        // update saturation picker and alpha picker
        saturationPicker.updateBaseColor(huePicker.getCurrentHue());
        alphaPicker.updateBaseColor(defaultColor);


        // set color info in TextInfo object
        if (textInfo == null) textInfo = new TextInfo();
        textInfo.huePosition = 50;
        textInfo.hsv[0] = huePicker.getCurrentHue();

        textInfo.saturationPosition = 0;
        textInfo.hsv[1] = saturationPicker.getCurrentSaturation();
        textInfo.hsv[2] = saturationPicker.getCurrentValue();

        textInfo.alphaPosition = 100;
        textInfo.alpha = alphaPicker.getCurrentAlpha();

        huePicker.setHueChangeListener((color, hue) -> {
            // update textInfo object
            textInfo.hsv[0] = huePicker.getCurrentHue();
            textInfo.huePosition = huePicker.getPosition();

            // update the layer
            updateTheEditableStickerLayer(color, huePicker.getCurrentHue(), saturationPicker.getCurrentSaturation(),
                    saturationPicker.getCurrentValue());

        });

        saturationPicker.setSaturationChangeListener((color, saturation, value) -> {
            // update textInfo object
            textInfo.hsv[1] = saturationPicker.getCurrentSaturation();
            textInfo.hsv[2] = saturationPicker.getCurrentValue();
            textInfo.saturationPosition = saturationPicker.getPosition();

            // update the layer
            updateTheEditableStickerLayer(color, huePicker.getCurrentHue(), saturationPicker.getCurrentSaturation(), saturationPicker.getCurrentValue());
        });

        alphaPicker.setAlphaChangeListener((color, value) -> {
            // update textInfo object
            textInfo.alpha = alphaPicker.getCurrentAlpha();
            textInfo.alphaPosition = alphaPicker.getPosition();

            // update the layer
            updateTheEditableStickerLayer(color, huePicker.getCurrentHue(), saturationPicker.getCurrentSaturation(), saturationPicker.getCurrentValue());
        });
    }

    private void updateTheEditableStickerLayer(int newColor, float currentHue, float currentSaturation, float currentValue) {
        final LayerModel lastLayer = myCanvas.getLastLayer();
        if (lastLayer != null
                && lastLayer.type == LayerModel.STICKER
                && lastLayer.sticker != null
                && lastLayer.textInfo != null
                && lastLayer.sticker.isSelected
                && lastLayer.sticker.stickerType == StickerView.EDITABLE_TEXT) {

            // update the TextInfo of Layer
            lastLayer.textInfo.hsv = new float[]{currentHue, currentSaturation, currentValue};
            lastLayer.textInfo.alpha = alphaPicker.getCurrentAlpha();
            lastLayer.textInfo.huePosition = huePicker.getPosition();
            lastLayer.textInfo.saturationPosition = saturationPicker.getPosition();
            lastLayer.textInfo.alphaPosition = alphaPicker.getPosition();

            // update the StickerView
            lastLayer.sticker.textPaint.setColor(newColor);
            myCanvas.invalidate();
        }
    }

    private int getColorFromHSVA(float hue, float saturation, float value, int alpha) {
        final float[] hsv = {hue, saturation, value};
        return Color.HSVToColor(alpha, hsv);
    }

    private void selectTextTab(int selectedTab) {
        switch (selectedTab) {

            case 0: // Fonts RecyclerView
                rvFonts.setVisibility(View.VISIBLE);
                llColorPickers.setVisibility(View.GONE);
                llAlphaPickers.setVisibility(View.GONE);
                rvAlignments.setVisibility(View.GONE);
                animateTextTopSheetTabLayout(0);
                break;

            case 1:
                rvFonts.setVisibility(View.GONE);
                llColorPickers.setVisibility(View.VISIBLE);
                llAlphaPickers.setVisibility(View.GONE);
                rvAlignments.setVisibility(View.GONE);
                animateTextTopSheetTabLayout(1);
                break;

            case 2:
                rvFonts.setVisibility(View.GONE);
                llColorPickers.setVisibility(View.GONE);
                llAlphaPickers.setVisibility(View.VISIBLE);
                rvAlignments.setVisibility(View.GONE);
                animateTextTopSheetTabLayout(2);
                break;

            case 3:
                rvFonts.setVisibility(View.GONE);
                llColorPickers.setVisibility(View.GONE);
                llAlphaPickers.setVisibility(View.GONE);
                rvAlignments.setVisibility(View.VISIBLE);
                animateTextTopSheetTabLayout(3);
                break;
        }
    }

    private void animateTextTopSheetTabLayout(int next) {
        AnimationHelper animationHelper = new AnimationHelper(200);
        if (next > selectedTextTab) {
            animationHelper.animateSlideToRight(lines, selectedTextTab, next);
        } else if (next < selectedTextTab) {
            animationHelper.animateSlideToLeft(lines, selectedTextTab, next);
        }

        // update the tab position after applying the ui changes
        selectedTextTab = next;
    }

    /**
     * When Font is selected from the fonts RecyclerView in Text Module
     */
    @Override
    public void onFontSelected(int position) {

        // updating the textInfo
        textInfo.fontIndex = position;
        final Typeface typeface = ResourcesCompat.getFont(context, fonts.get(position).getFont());
        textInfo.typeface = typeface;

        final LayerModel lastLayer = myCanvas.getLastLayer();
        if (lastLayer != null
                && lastLayer.type == LayerModel.STICKER
                && lastLayer.sticker != null
                && lastLayer.textInfo != null
                && lastLayer.sticker.isSelected
                && lastLayer.sticker.stickerType == StickerView.EDITABLE_TEXT) {

            // update the TextInfo of Layer
            lastLayer.textInfo.fontIndex = position;
            lastLayer.textInfo.typeface = typeface;

            // update the stickerView
            lastLayer.sticker.textPaint.setTypeface(typeface);
            myCanvas.invalidate();
        }
    }

    /**
     * When Text Alignment is selected in Text Module
     */
    @Override
    public void onAlignmentSelected(int position) {
        // updating the textInfo
        textInfo.alignmentIndex = position;
        final Paint.Align align = alignments.get(position).getAlign();
        textInfo.align = align;

        final LayerModel lastLayer = myCanvas.getLastLayer();
        if (lastLayer != null
                && lastLayer.type == LayerModel.STICKER
                && lastLayer.sticker != null
                && lastLayer.textInfo != null
                && lastLayer.sticker.isSelected
                && lastLayer.sticker.stickerType == StickerView.EDITABLE_TEXT) {

            // update the TextInfo of Layer
            lastLayer.textInfo.alignmentIndex = position;
            lastLayer.textInfo.align = align;

            // update the stickerView
            lastLayer.sticker.textPaint.setTextAlign(align);
            myCanvas.invalidate();
        }
    }

    public int getSelectedFont() {
        int index = -1;
        if (fonts != null) {
            for (int i = 0; i < fonts.size(); i++) {
                if (fonts.get(i).isSelected()) {
                    index = i;
                    break;
                }
            }
        }

        if (index == -1)
            return R.font.metropolis_regular;
        else
            return fonts.get(index).getFont();
    }

    public int getFontIndex(int font) {
        int index = -1;
        if (fonts != null) {
            for (int i = 0; i < fonts.size(); i++) {
                if (fonts.get(i).getFont() == font) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * Open AddTextFragment for adding new text
     */
    private void openAddTextFragment() {
        final AddTextFragment fragment = new AddTextFragment(this, this, blurredBitmap, textInfo);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_editor, fragment)
                .addToBackStack("add_text")
                .commit();
    }

    /**
     * Open AddTextFragment for editing given text
     */
    private void openEditTextFragment(String text) {
        final AddTextFragment fragment = new AddTextFragment(this, this, blurredBitmap, textInfo, text);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_editor, fragment)
                .addToBackStack("edit_text")
                .commit();
    }

    /**
     * When New Text is Added from the AddTextFragment in Text Module
     */
    @Override
    public void onTextAdded(String text) {
        Paint paint = new Paint();
        paint.setTypeface(textInfo.typeface);
        paint.setColor(textInfo.getColor());
        paint.setTextAlign(textInfo.align);
        paint.setTextSize(60);
        StickerView stickerView = new StickerView(context, text, paint, myCanvas.screenRect, StickerView.EDITABLE_TEXT, 60);
        myCanvas.addLayer(new LayerModel(stickerView, textInfo.copy()));
    }

    /**
     * When Text is Edited from the AddTextFragment in Text Module
     */
    @Override
    public void onTextEdited(String text) {
        final LayerModel lastLayer = myCanvas.getLastLayer();
        if (lastLayer != null
                && lastLayer.type == LayerModel.STICKER
                && lastLayer.sticker != null
                && lastLayer.textInfo != null
                && lastLayer.sticker.isSelected
                && lastLayer.sticker.stickerType == StickerView.EDITABLE_TEXT) {

            // update the stickerView
            lastLayer.sticker.text = text;
            myCanvas.invalidate();
        }
    }


    //--------------------------------- D R A W   with   B R U S H  --------------------------------
    private void selectDrawTab(int selectedTab) {
        switch (selectedTab) {
            case 0: // Size Pickers
                llBrushSizePickers.setVisibility(View.VISIBLE);
                llBrushColorPickers.setVisibility(View.GONE);
                llBrushAlphaPickers.setVisibility(View.GONE);
                animateDrawTopSheetTabLayout(0);
                break;

            case 1:
                llBrushSizePickers.setVisibility(View.GONE);
                llBrushColorPickers.setVisibility(View.VISIBLE);
                llBrushAlphaPickers.setVisibility(View.GONE);
                animateDrawTopSheetTabLayout(1);
                break;

            case 2:
                llBrushSizePickers.setVisibility(View.GONE);
                llBrushColorPickers.setVisibility(View.GONE);
                llBrushAlphaPickers.setVisibility(View.VISIBLE);
                animateDrawTopSheetTabLayout(2);
                break;
        }
    }

    private void animateDrawTopSheetTabLayout(int next) {
        AnimationHelper animationHelper = new AnimationHelper(200);
        if (next > selectedDrawTab) {
            animationHelper.animateSlideToRight(lines2, selectedDrawTab, next);
        } else if (next < selectedDrawTab) {
            animationHelper.animateSlideToLeft(lines2, selectedDrawTab, next);
        }

        // update the tab position after applying the ui changes
        selectedDrawTab = next;
    }

    private void setBrushPickersListeners() {

        brushHuePicker.connect(brushSaturationPicker, brushAlphaPicker);
        brushSaturationPicker.connect(brushHuePicker, brushAlphaPicker);
        brushAlphaPicker.connect(brushHuePicker, brushSaturationPicker);

        brushSizePicker.setPositionChangeListener(position -> {
            brushView.setVisibility(View.VISIBLE);
            brushView.updateSize((int) (position / 3));
            myCanvas.updateSize((int) (position / 3));
            new Handler(Looper.getMainLooper()).postDelayed(() -> brushView.setVisibility(View.GONE), 5000);
        });

        brushHuePicker.setHueChangeListener((color, hue) -> {
            brushView.setVisibility(View.VISIBLE);
            brushView.updateColor(color);
            myCanvas.updateColor(color);
            new Handler(Looper.getMainLooper()).postDelayed(() -> brushView.setVisibility(View.GONE), 5000);
        });

        brushSaturationPicker.setSaturationChangeListener((color, saturation, value) -> {
            brushView.setVisibility(View.VISIBLE);
            brushView.updateColor(color);
            myCanvas.updateColor(color);
            new Handler(Looper.getMainLooper()).postDelayed(() -> brushView.setVisibility(View.GONE), 5000);
        });

        brushAlphaPicker.setAlphaChangeListener((color, alpha) -> {
            brushView.setVisibility(View.VISIBLE);
            brushView.updateColor(color);
            myCanvas.updateColor(color);
            new Handler(Looper.getMainLooper()).postDelayed(() -> brushView.setVisibility(View.GONE), 5000);
        });


    }
}