package com.drudotstech;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.drudotstech.customgallery.R;

public class TestGifActivity extends AppCompatActivity {

    MyGifView myGifView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gif);
        myGifView = findViewById(R.id.my_gif_view);

//        myGifView.setImageResource(R.drawable.illusion);
    }
}