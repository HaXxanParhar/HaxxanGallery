package com.drudotstech.customgallery;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.drudotstech.customgallery.editor.photoediting.EmojiBSFragment;
import com.drudotstech.customgallery.editor.photoediting.StickerBSFragment;
import com.drudotstech.customgallery.editor.photoeditor.OnPhotoEditorListener;
import com.drudotstech.customgallery.editor.photoeditor.OnSaveBitmap;
import com.drudotstech.customgallery.editor.photoeditor.PhotoEditor;
import com.drudotstech.customgallery.editor.photoeditor.PhotoEditorView;
import com.drudotstech.customgallery.editor.photoeditor.ViewType;
import com.drudotstech.customgallery.utils.AnimationHelper;
import com.drudotstech.customgallery.utils.MyUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class StickersActivity extends BaseActivity implements OnPhotoEditorListener,
        EmojiBSFragment.EmojiListener, StickerBSFragment.StickerListener {


    // ---------------------------------------- V I E W S ------------------------------------------
    private View ivDoodle, ivEmoji, ivStickers;
    private ImageView ivOriginal, ivBlurred;
    private PhotoEditorView photoEditorView;


    private View mainToolbar, secondToolbar, rlBottom;
    private View ivBack, ivClose, tvSave, tvDone;
    private TextView tvToolbarName;


    private EmojiBSFragment mEmojiBSFragment = null;
    private StickerBSFragment mStickerBSFragment = null;

    // -------------------------------------- V A R I A B L E S ------------------------------------
    private AnimationHelper animationHelper = null;
    private Bitmap originalBitmap;

    // All the bitmap changes will be applied to this except the Filter, which will be applied when
    // user saves the image.
    private Bitmap unfilteredBitmap;

    // this bitmap will be shown to the user. When user select the filter, copy the unfiltered bitmap into this one;
    // and apply filter and show.
    private Bitmap filteredBitmap;

    private Bitmap blurredBitmap, tempBitmap;
    private String media;
    private int selected = -1; // 0 , 1, 2, 3, 4 -> for bottom options
    private PhotoEditor photoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers);


        //--------------------------------- I N I T   V I E W S ------------------------------------
        mainToolbar = findViewById(R.id.main_action_bar);
        ivBack = findViewById(R.id.iv_back);
        tvSave = findViewById(R.id.tv_next);

        secondToolbar = findViewById(R.id.second_action_bar);
        ivClose = findViewById(R.id.iv_close);
        tvToolbarName = findViewById(R.id.tv_actionbar_name);
        tvDone = findViewById(R.id.tv_done);

        ivOriginal = findViewById(R.id.iv_original_image);
        photoEditorView = findViewById(R.id.photoEditorView);
        ivBlurred = findViewById(R.id.iv_blurred_image);

        rlBottom = findViewById(R.id.rl_bottom);
        ivDoodle = findViewById(R.id.iv_doodle);
        ivEmoji = findViewById(R.id.iv_emoji);
        ivStickers = findViewById(R.id.iv_stickers);


        //------------------------------  S E T U P  &   L O A D  ----------------------------------

        hideLoading();
        // init the Animation helper class
        animationHelper = new AnimationHelper(300);

        // animate bottom option from bottom to top
        animationHelper.moveFromBottomToTop(rlBottom, 66f);
        // animate the second action bar
        animationHelper.moveFromTopToBottom(secondToolbar, 48f);


        // get Bitmaps from intent
//        originalBitmap = getIntent().getParcelableExtra(GalleryConstants.ORIGINAL_BITMAP);
//        blurredBitmap = getIntent().getParcelableExtra(GalleryConstants.BLURRED_BITMAP);
//        filteredBitmap = getIntent().getParcelableExtra(GalleryConstants.FILTERED_BITMAP);
//        unfilteredBitmap = getIntent().getParcelableExtra(GalleryConstants.BITMAP);

        originalBitmap = MyUtils.copyBitmap(EditMainActivity.originalBitmap);
        blurredBitmap = MyUtils.copyBitmap(EditMainActivity.blurredBitmap);
        filteredBitmap = MyUtils.copyBitmap(EditMainActivity.filteredBitmap);
        unfilteredBitmap = MyUtils.copyBitmap(EditMainActivity.unfilteredBitmap);

        // load them in image view
        ivOriginal.setImageBitmap(originalBitmap);
        ivBlurred.setImageBitmap(blurredBitmap);
        photoEditorView.getSource().setImageBitmap(unfilteredBitmap);

        // init Photo Editor
        photoEditor = new PhotoEditor.Builder(context, photoEditorView)
                .setPinchTextScalable(true)
                .build();
        photoEditor.setOnPhotoEditorListener(this);


        // init and setup listener for the Dialog Fragments
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment = new EmojiBSFragment();
        mEmojiBSFragment.setEmojiListener(this);


        //------------------------------------ A C T I O N S ---------------------------------------
        ivStickers.setOnClickListener(view -> showBottomSheetDialogFragment(mStickerBSFragment));
        ivEmoji.setOnClickListener(view -> showBottomSheetDialogFragment(mEmojiBSFragment));

        View btnShow = findViewById(R.id.btn_show);
        btnShow.setOnClickListener(view -> {
            if (photoEditorView.getVisibility() == View.VISIBLE) {
                photoEditorView.setVisibility(View.GONE);
            } else {
                photoEditorView.setVisibility(View.VISIBLE);
            }
        });

        tvDone.setOnClickListener(view -> {
            showLoading();
            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(@Nullable Bitmap saveBitmap) {
                    hideLoading();
                    if (saveBitmap != null) {
                        EditMainActivity.unfilteredBitmap = saveBitmap.copy(saveBitmap.getConfig(), true);
                    } else {
                        Toast.makeText(activity, "Save Bitmap is null", Toast.LENGTH_SHORT).show();
                    }

                    setResult(RESULT_OK);
                    closeWithAnimation();
                }


                @Override
                public void onFailure(@Nullable Exception e) {
                    hideLoading();
                    Toast.makeText(activity, "Exception creating Bitmap : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        ivClose.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            closeWithAnimation();
        });

    }

    private void closeWithAnimation() {
        // animation
        animationHelper.moveToBottom(rlBottom, 100f);
        // hide the second action bar
        animationHelper.moveToTop(secondToolbar, 48f);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            finish();
            overridePendingTransition(0, 0);

        }, 300);
    }


    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment) {
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

    @Override
    public void onEmojiClick(@Nullable String emojiUnicode) {
        photoEditor.addEmoji(emojiUnicode);
    }


    @Override
    public void onStickerClick(@Nullable Bitmap bitmap) {
        photoEditor.addImage(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        originalBitmap.recycle();
        blurredBitmap.recycle();
        filteredBitmap.recycle();
        unfilteredBitmap.recycle();
    }

    @Override
    public void onBackPressed() {
        closeWithAnimation();
    }
}