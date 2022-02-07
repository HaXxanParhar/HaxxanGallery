package com.drudotstech.customgallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/********** Developed by Usman Hassan Parhar **********
 * Created by : Usman Hassan Parhar on 08/07/2021 at 11:23 PM
 ******************************************************/


public abstract class BaseActivity extends AppCompatActivity {

    public Activity activity = this;
    public Context context = this;
    View loading;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //initialize the loading in the activity
    public boolean initLoading() {
        try {
            loading = findViewById(R.id.rl_loading);
            loading.setVisibility(View.VISIBLE);
            return true;
        } catch (Exception e) {
            Log.e("YAM", "Exception : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void showLoading() {
        if (loading == null) {
            if (initLoading()) {
                loading.setVisibility(View.VISIBLE);
            }
        } else {
            loading.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if (loading == null) {
            if (initLoading()) {
                loading.setVisibility(View.GONE);
            }
        } else {
            loading.setVisibility(View.GONE);
        }
    }

    public void startActivityAndTransition(Class className) {
        startActivityWithoutTransition(className);
        overrideWithOpeningTransition();
    }

    public void startActivityWithoutTransition(Class className, boolean shouldFinish) {
        startActivity(new Intent(this, className));
        if (shouldFinish)
            finish();
    }

    public void startActivityWithoutTransition(Class className) {
        startActivity(new Intent(this, className));
    }

    public void startActivityAndTransition(Class className, boolean shouldFinish) {
        startActivityWithoutTransition(className);
        overrideWithOpeningTransition();
        if (shouldFinish)
            finish();
    }

    public void startActivityAndTransition(Class className, String[] key, String[] value) {
        startActivityWithoutTransition(className, key, value);
        overrideWithOpeningTransition();
    }

    public void startActivityWithoutTransition(Class className, String[] key, String[] value) {
        final Intent intent = new Intent(this, className);
        int size = Math.min(key.length, value.length);
        for (int i = 0; i < size; i++) {
            intent.putExtra(key[i], value[i]);
        }
        startActivity(intent);
    }

    public void startActivityAndTransition(Class className, String[] key, String[] value,
                                           boolean shouldFinish) {
        startActivityWithoutTransition(className, key, value);
        overrideWithOpeningTransition();
        if (shouldFinish)
            finish();
    }

    public void startActivityAndTransition(Class className, String key, String value) {
        startActivityWithoutTransition(className, key, value);
        overrideWithOpeningTransition();
    }

    public void startActivityWithoutTransition(Class className, String key, String value) {
        final Intent intent = new Intent(this, className);
        intent.putExtra(key, value);
        startActivity(intent);
    }


    public void startActivityAndTransition(Class className, String key, String value,
                                           boolean shouldFinish) {
        startActivityWithoutTransition(className, key, value);
        overrideWithOpeningTransition();
        if (shouldFinish)
            finish();
    }


    public void startActivityAndTransition(Intent intent) {
        startActivity(intent);
        overrideWithOpeningTransition();
    }


    public void startActivityAndTransition(Intent intent, boolean shouldFinish) {
        startActivity(intent);
        overrideWithOpeningTransition();
        if (shouldFinish) finish();
    }


    public void startActivityAndFinishAffinity(Class className) {
        startActivityWithoutTransition(className);
        overrideWithOpeningTransition();
        new Handler(Looper.getMainLooper()).postDelayed(this::finishAffinity, 2000);
    }

    public void startActivityAndFinishAffinity(Class className, String key, String value) {
        startActivityWithoutTransition(className, key, value);
        overrideWithOpeningTransition();
        new Handler(Looper.getMainLooper()).postDelayed(this::finishAffinity, 2000);
    }

    public void overrideWithOpeningTransition() {
        overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
    }

    public void finishAffinityAndBack(Class className) {
        startActivityWithoutTransition(className);
        overrideWithClosingTransition();
        finishAffinity();
    }

    public void back() {
        finish();
        overrideWithClosingTransition();
    }

    public void overrideWithClosingTransition() {
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onBackPressed() {
        back();
    }
}


