package com.drudotstech.customgallery.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.ScriptC_blue;
import com.drudotstech.customgallery.editor.photoediting.EmojiBSFragment;
import com.drudotstech.customgallery.editor.photoediting.FileSaveHelper;
import com.drudotstech.customgallery.editor.photoediting.PropertiesBSFragment;
import com.drudotstech.customgallery.editor.photoediting.ShapeBSFragment;
import com.drudotstech.customgallery.editor.photoediting.StickerBSFragment;
import com.drudotstech.customgallery.editor.photoediting.base.BaseActivity;
import com.drudotstech.customgallery.editor.photoediting.filters.FilterListener;
import com.drudotstech.customgallery.editor.photoediting.tools.EditingToolsAdapter;
import com.drudotstech.customgallery.editor.photoediting.tools.ToolType;
import com.drudotstech.customgallery.editor.photoeditor.OnPhotoEditorListener;
import com.drudotstech.customgallery.editor.photoeditor.PhotoEditor;
import com.drudotstech.customgallery.editor.photoeditor.PhotoEditorView;
import com.drudotstech.customgallery.editor.photoeditor.PhotoFilter;
import com.drudotstech.customgallery.editor.photoeditor.ViewType;
import com.drudotstech.customgallery.editor.photoeditor.shape.ShapeBuilder;
import com.drudotstech.customgallery.editor.photoeditor.shape.ShapeType;
import com.drudotstech.customgallery.utils.MyUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterActivity extends BaseActivity implements OnPhotoEditorListener, View.OnClickListener,
        PropertiesBSFragment.Properties, ShapeBSFragment.Properties, EmojiBSFragment.EmojiListener, StickerBSFragment.StickerListener,
        EditingToolsAdapter.OnItemSelected, FilterListener {

    private final Context context = FilterActivity.this;

    private ImageView ivOriginal, ivImageView;

    private Bitmap originalBitmap, bitmap, filterBitmap;
    private String media;
    private Button btnShow;

    private int part = 1;


    // --------------- Stickers ----------------------------
    private PhotoEditor mPhotoEditor = null;
    private PhotoEditorView mPhotoEditorView = null;
    private PropertiesBSFragment mPropertiesBSFragment = null;
    private ShapeBSFragment mShapeBSFragment = null;
    private ShapeBuilder mShapeBuilder = null;
    private EmojiBSFragment mEmojiBSFragment = null;
    private StickerBSFragment mStickerBSFragment = null;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible = false;

    @VisibleForTesting
    private Uri mSaveImageUri = null;
    private FileSaveHelper mSaveFileHelper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        try {
            ivOriginal = findViewById(R.id.iv_original_image);
            ivImageView = findViewById(R.id.iv_image);
            mPhotoEditorView = findViewById(R.id.photoEditorView);

//          ---------------------------  Stickers views --------------------------------------------
            mStickerBSFragment = new StickerBSFragment();
            mStickerBSFragment.setStickerListener(this);
            mEmojiBSFragment = new EmojiBSFragment();
            mEmojiBSFragment.setEmojiListener(this);
            mPropertiesBSFragment = new PropertiesBSFragment();
            mPropertiesBSFragment.setPropertiesChangeListener(this);
            mShapeBSFragment = new ShapeBSFragment();
            mShapeBSFragment.setPropertiesChangeListener(this);

            mPhotoEditor = new PhotoEditor.Builder(context, mPhotoEditorView)
                    .setPinchTextScalable(true)
                    .build();
            mPhotoEditor.setOnPhotoEditorListener(this);
            mSaveFileHelper = new FileSaveHelper(this);

//          ---------------------------  Stickers views --------------------------------------------

            originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tree);
            if (originalBitmap == null) {
                Toast.makeText(context, "resource bitmap is null", Toast.LENGTH_SHORT).show();
                return;
            }

            bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
            mPhotoEditorView.getSource().setImageBitmap(bitmap);


            filterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_anthony);
            filterBitmap = MyUtils.getResizedBitmap(filterBitmap, originalBitmap.getHeight(), originalBitmap.getWidth());


            ivOriginal.setImageBitmap(originalBitmap);
            ivImageView.setImageBitmap(bitmap);


//            media = getIntent().getStringExtra("media");
//            Glide.with(context)
//                    .asBitmap()
//                    .load(media)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            originalBitmap = resource;
//                            bitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
//                            ivOriginal.setImageBitmap(resource);
//                            ivImageView.setImageBitmap(bitmap);
//                        }
//                    });

            findViewById(R.id.btn_filter).setOnClickListener(view -> showBottomSheetDialogFragment(mStickerBSFragment));
            findViewById(R.id.btn_emoji).setOnClickListener(view -> showBottomSheetDialogFragment(mEmojiBSFragment));

            btnShow = findViewById(R.id.btn_show);
            btnShow.setOnClickListener(view -> {
                if (ivImageView.getVisibility() == View.VISIBLE) {
                    ivImageView.setVisibility(View.GONE);
                    btnShow.setText("Hide Original");
                } else {
                    ivImageView.setVisibility(View.VISIBLE);
                    btnShow.setText("Show Original");
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void applyHistoFilter() {
        bitmap = histogramEqualization(originalBitmap);
        ivImageView.setImageBitmap(bitmap);
        ivImageView.animate().setDuration(1000).alpha(1);
    }

    private void filter() {
        if (bitmap != null && originalBitmap != null && filterBitmap != null) {
            Toast.makeText(context, "filtering...", Toast.LENGTH_SHORT).show();
            final int height = originalBitmap.getHeight();
            final int width = originalBitmap.getWidth();
            final int height1 = filterBitmap.getHeight();
            final int width1 = filterBitmap.getWidth();

            final int H = Math.min(height1, height);
            final int W = Math.min(width1, width);

            for (int x = 0; x < W; x++) {
                for (int y = 0; y < H; y++) {

                    final int pixel = originalBitmap.getPixel(x, y);
                    int originalRed = Color.red(pixel);
                    int originalGreen = Color.green(pixel);
                    int originalBlue = Color.blue(pixel);

                    final int filterPixel = filterBitmap.getPixel(x, y);
                    int filterRed = Color.red(filterPixel);
                    int filterGreen = Color.green(filterPixel);
                    int filterBlue = Color.blue(filterPixel);

                    int red = (part * originalRed + filterRed) / part + 1;
                    int green = (part * originalGreen + filterGreen) / part + 1;
                    int blue = (part * originalBlue + filterBlue) / part + 1;
                    red = Math.min(255, red);
                    green = Math.min(255, green);
                    blue = Math.min(255, blue);
                    int newColor = Color.rgb(red, green, blue);

                    bitmap.setPixel(x, y, newColor);
                }
            }

            Toast.makeText(context, "success!", Toast.LENGTH_SHORT).show();
            ivImageView.setImageBitmap(bitmap);
            ivImageView.setVisibility(View.VISIBLE);
        } else {
            if (bitmap == null)
                Toast.makeText(context, "bitmap is null", Toast.LENGTH_SHORT).show();
            if (originalBitmap == null)
                Toast.makeText(context, "original bitmap is null", Toast.LENGTH_SHORT).show();
            if (filterBitmap == null)
                Toast.makeText(context, "filtered bitmap is null", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap histogramEqualization(Bitmap image) {
        //Get image size
        int width = image.getWidth();
        int height = image.getHeight();

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);

        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, res);

        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());

        //Create script from rs file.
        ScriptC_blue histEqScript = new ScriptC_blue(rs);

        //Set size in script
//        histEqScript.set_size(width * height);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB);

        //Call the rs method to compute the remap array
//        histEqScript.invoke_createRemapArray();
//
//        //Call the second kernel
//        histEqScript.forEach_remaptoRGB(allocationB, allocationA);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();

        return res;
    }


    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment) {
        Toast.makeText(context, "clicked!", Toast.LENGTH_SHORT).show();
        if (fragment == null) {
            Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
            return;
        } else if (fragment.isAdded()) {
            Toast.makeText(context, "is added", Toast.LENGTH_SHORT).show();
            return;
        }
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onEmojiClick(@Nullable String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeColor(colorCode));
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity));
    }

    @Override
    public void onShapeSizeChanged(int shapeSize) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeSize((float) shapeSize));
    }

    @Override
    public void onShapePicked(@Nullable ShapeType shapeType) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeType(shapeType));
    }

    @Override
    public void onStickerClick(@Nullable Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
    }

    @Override
    public void onFilterSelected(@Nullable PhotoFilter photoFilter) {

    }

    @Override
    public void onToolSelected(@Nullable ToolType toolType) {

    }

    @Override
    public void onEditTextChangeListener(@Nullable View rootView, @Nullable String text, int colorCode) {

    }

    @Override
    public void onAddViewListener(@Nullable ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(@Nullable ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(@Nullable ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(@Nullable ViewType viewType) {

    }

    @Override
    public void onTouchSourceImage(@Nullable MotionEvent event) {

    }
}