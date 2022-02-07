package com.drudotstech;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.drudotstech.customgallery.R;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView imageView = findViewById(R.id.iv_image);
        String media = getIntent().getStringExtra("image");
        Glide.with(this).load(media).into(imageView);
    }
}