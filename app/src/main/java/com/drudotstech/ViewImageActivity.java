package com.drudotstech;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.drudotstech.customgallery.R;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView imageView = findViewById(R.id.iv_image);
        TextView textView = findViewById(R.id.tv_path);

        String media = getIntent().getStringExtra("image");

        textView.setText(media);
        Glide.with(this).load(media).into(imageView);
    }
}