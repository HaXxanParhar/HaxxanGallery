package com.drudotstech.customgallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.drudotstech.customgallery.utils.MyUtils;

import java.io.FileInputStream;

public class RotateActivity extends BaseActivity {


    // ---------------------------------------- V I E W S ------------------------------------------
    private ImageView imageView;
    private View llFilpHorizontal, llFlipVertical, llRotate;


    // -------------------------------------- V A R I A B L E S ------------------------------------
    private Bitmap bitmap = null;
    private Matrix matrix;
    private int rotation;
    private int screenWidth, screenHeight, cx, cy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);
        imageView = findViewById(R.id.iv_background);
        llRotate = findViewById(R.id.ll_rotate);
        llFilpHorizontal = findViewById(R.id.ll_flip_horizontal);
        llFlipVertical = findViewById(R.id.ll_flip_vertical);

        screenWidth = MyUtils.getScreenWidth(context);
        screenHeight = MyUtils.getScreenHeight(context);
        cx = screenWidth / 2;
        cy = screenHeight / 2;
        matrix = new Matrix();

        getBitmap();

        llRotate.setOnClickListener(v -> {
            llRotate.setEnabled(false);
            imageView.animate().rotationBy(90).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    llRotate.setEnabled(true);
                }
            });
        });

        llFilpHorizontal.setOnClickListener(v -> {
            matrix.postScale(-1, 1, cx, cy);
            Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            imageView.setImageBitmap(temp);
        });

        llFlipVertical.setOnClickListener(v -> {
            matrix.postScale(1, -1, cx, cy);
            Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            imageView.setImageBitmap(temp);
        });
    }

    private void getBitmap() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String filename = getIntent().getStringExtra("image");
                try {
                    FileInputStream is = context.openFileInput(filename);
                    bitmap = BitmapFactory.decodeStream(is);
                    imageView.setImageBitmap(bitmap);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                hideLoading();
            }
        }.execute();
    }
}