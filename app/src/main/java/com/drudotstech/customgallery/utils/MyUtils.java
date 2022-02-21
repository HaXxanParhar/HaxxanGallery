package com.drudotstech.customgallery.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.drudotstech.customgallery.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/28/2022 at 12:17 PM
 ******************************************************/


public class MyUtils {
    /**
     * <p>
     * This method will show a Yes & Cancel options popup. When user presses cancel, popup will dismiss
     * But the functionality upon the click of Yes is to be implemented in the activity/fragment/adapter from where is it called.
     * TextView is being returned so the a onTouchListener can be implemented
     * <br><br>
     * Note: onClickListener can not implemented on the returned TextView because it is already being implemented in the function
     * therefore you can mock the onClick behavior by using onTouchListener. In onTouchListener check when action is ACTION_UP.
     * <br>
     * Example of what to write in the OnTouchListener:<br>
     * if (event.getAction() == MotionEvent.ACTION_UP) {<br>
     * //your code here<br>
     * }<br>
     * return false;  //remember to return false<br>
     * </p>
     */
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public static TextView getYesNoPopup(Activity context, String title, String areYouSureTo) {
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
        tvTitle.setText(title);
        tvMessage.setText("Are you sure to " + areYouSureTo + " ?");
        tvCancel.setText("Cancel");
        tvYes.setText("Yes");
        //functionality upon clicks
        tvCancel.setOnClickListener(v -> alertDialog.dismiss());
        tvYes.setOnClickListener(v -> alertDialog.dismiss());
        return tvYes;
    }

    public static Bitmap overlayer(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmOverlay;
    }

    public static Bitmap copyBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        return Bitmap.createBitmap(bm, 0, 0, width, height, null, false);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static void vibrate(Context context, int milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(milliseconds);
        }
    }

    public static void setViewSize(Context context, View view, int widthDivision) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        float ratio = 1;
        final int widthPixels = displayMetrics.widthPixels / widthDivision;

        //ratio = w/h -->  h = w/r
        final float h = widthPixels / ratio;
        layoutParams.height = (int) h;
        view.setLayoutParams(layoutParams);
    }

    public static float getTextSize(String text) {
        float minTextSize = 10;
        float maxTextSize = 60;
        float ratio = 6;
        int count = text.length();

        return Math.max(minTextSize, maxTextSize - ((count - 1) * ratio));
    }

    public static String getAddress(Context context, double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!addresses.isEmpty())
            return addresses.get(0).getAddressLine(0);
        else
            return "";
    }

    public static String getCityCountry(Context context, double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!addresses.isEmpty()) {
            String country = addresses.get(0).getCountryName();
            String city = addresses.get(0).getLocality();
            return city + ", " + country;
        } else
            return "";
    }

    public static void hideKeyboard(View view, Context context) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
