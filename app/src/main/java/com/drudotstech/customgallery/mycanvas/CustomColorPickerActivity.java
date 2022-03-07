package com.drudotstech.customgallery.mycanvas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.my_color_picker.AlphaPicker;
import com.drudotstech.customgallery.mycanvas.my_color_picker.HuePicker;
import com.drudotstech.customgallery.mycanvas.my_color_picker.SaturationPicker;

public class CustomColorPickerActivity extends AppCompatActivity {

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_color_picker);
        HuePicker huePicker = findViewById(R.id.hue_picker);
        SaturationPicker saturationPicker = findViewById(R.id.sv_picker);
        AlphaPicker alphaPicker = findViewById(R.id.alpha_picker);
        view = findViewById(R.id.color_view);

        huePicker.setHueChangeListener(new HuePicker.HueChangeListener() {
            @Override
            public void onHueChanged(int color, float hue) {
                saturationPicker.updateBaseColor(huePicker.getCurrentHue());

                int newColor = showColor(huePicker.getCurrentHue(), saturationPicker.getCurrentSaturation(),
                        saturationPicker.getCurrentValue(), alphaPicker.getCurrentAlpha());

                alphaPicker.updateBaseColor(newColor);
            }
        });

        saturationPicker.setSaturationChangeListener(new SaturationPicker.SaturationChangeListener() {
            @Override
            public void onSaturationChanged(int color, float saturation, float value) {

                int newColor = showColor(huePicker.getCurrentHue(), saturationPicker.getCurrentSaturation(),
                        saturationPicker.getCurrentValue(), alphaPicker.getCurrentAlpha());

                alphaPicker.updateBaseColor(newColor);
            }
        });

        alphaPicker.setAlphaChangeListener(new AlphaPicker.AlphaChangeListener() {
            @Override
            public void onValueChanged(int color, int value) {
                showColor(huePicker.getCurrentHue(), saturationPicker.getCurrentSaturation(),
                        saturationPicker.getCurrentValue(), alphaPicker.getCurrentAlpha());
            }
        });
    }

    private int showColor(float hue, float saturation, float value, int alpha) {
        final float[] hsv = {hue, saturation, value};
        int color = Color.HSVToColor(alpha, hsv);
        view.setBackgroundColor(color);
        return color;
    }
}