package com.drudotstech;

import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.CanvasUtils;
import com.drudotstech.customgallery.mycanvas.MyCanvas;
import com.drudotstech.customgallery.mycanvas.models.LayerModel;

public class TestGifActivity extends AppCompatActivity {

    Context context = TestGifActivity.this;
    MyGifView myGifView;
    MyCanvas myCanvas;
    RelativeLayout rlCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gif);
        myGifView = findViewById(R.id.my_gif_view);
        rlCanvas = findViewById(R.id.canvas_view);

//        myGifView.setImageResource(R.drawable.gif_3);

        // create canvas
//        myCanvas = new MyCanvas(context);
//        // add canvas to the relative layout
//        rlCanvas.addView(myCanvas);
//        float screenWidth = CanvasUtils.getScreenWidth(context);
//        float screenHeight = CanvasUtils.getScreenHeight(context);
//        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) myCanvas.getLayoutParams();
//        layoutParams.height = (int) screenHeight;
//        layoutParams.width = (int) screenWidth;
//        myCanvas.setLayoutParams(layoutParams);
//        myCanvas.init();

        findViewById(R.id.add_gif).setOnClickListener(v -> addGif());
    }

    private void addGif() {
        MyGifView myGifView = new MyGifView(context, 100, 1000, R.drawable.default_gif);
        // create layer and add in layers of canvas
        LayerModel layer = new LayerModel(myGifView);
        myCanvas.addLayer(layer);

    }
}