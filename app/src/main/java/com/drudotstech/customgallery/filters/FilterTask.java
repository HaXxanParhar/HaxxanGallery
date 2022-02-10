package com.drudotstech.customgallery.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.ScriptC_blue;
import com.drudotstech.customgallery.ScriptC_filter1;
import com.drudotstech.customgallery.ScriptC_histEq;
import com.drudotstech.customgallery.ScriptC_warm;
import com.drudotstech.customgallery.croppy.croppylib.main.BitmapResult;
import com.drudotstech.customgallery.utils.MyUtils;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/27/2022 at 4:29 PM
 ******************************************************/


public class FilterTask extends AsyncTask<Void, Void, BitmapResult> {

    private Context context;
    private Bitmap image;
    private FilterType filterType;
    private ApplyFilterCallback applyFilterCallback;

    public FilterTask(Context context, Bitmap image, FilterType filterType, ApplyFilterCallback applyFilterCallback) {
        this.context = context;
        this.image = image;
        this.filterType = filterType;
        this.applyFilterCallback = applyFilterCallback;
    }

    @Override
    protected BitmapResult doInBackground(Void... voids) {
        try {
            Bitmap bitmap = applyFilter();
            return new BitmapResult(true, bitmap);
        } catch (Exception e) {
            return new BitmapResult(false, e);
        }
    }

    @Override
    protected void onPostExecute(BitmapResult bitmapResult) {
        super.onPostExecute(bitmapResult);
        applyFilterCallback.onFilterApplied(bitmapResult);
    }

    public Bitmap applyFilter() {

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);

        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, res);

        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());

        switch (filterType) {

            case NO_FILTER:
                return image;

            case DEEP_BLUE:
            case WINTER:
                return deepBlue(res, rs, allocationA, allocationB);

            case SUMMER:
                return summer(res, rs, allocationA, allocationB);

            case HDR:
                return histogramEqualization(res, rs, allocationA, allocationB);

            case ANTHONY:
                return anthony(context, res, rs, allocationA, allocationB);

            case RAINBOW:
                return rainbow(context, res, rs, allocationA, allocationB);

            case MARK:
                return mark(context, res, rs, allocationA, allocationB);

            case FILTER1:
                return filter1(context, res, rs, allocationA, allocationB);

            case FILTER2:
                return filter2(context, res, rs, allocationA, allocationB);

        }

        return image;
    }

    private Bitmap filter1(Context context, Bitmap res, RenderScript rs, Allocation allocationA, Allocation allocationB) {

        // creating Filter Bitmap
        Bitmap filterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.karachi);
        filterBitmap = MyUtils.getResizedBitmap(filterBitmap, res.getHeight(), res.getWidth());

        // create allocation for the filter
        Allocation allocationF = Allocation.createFromBitmap(rs, filterBitmap);


        //Create script from rs file.
        ScriptC_filter1 histEqScript = new ScriptC_filter1(rs);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationF, allocationB);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
        return res;
    }

    private Bitmap filter2(Context context, Bitmap res, RenderScript rs, Allocation allocationA, Allocation allocationB) {

        // creating Filter Bitmap
        Bitmap filterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.quetta);
        filterBitmap = MyUtils.getResizedBitmap(filterBitmap, res.getHeight(), res.getWidth());

        // create allocation for the filter
        Allocation allocationF = Allocation.createFromBitmap(rs, filterBitmap);


        //Create script from rs file.
        ScriptC_filter1 histEqScript = new ScriptC_filter1(rs);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationF, allocationB);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
        return res;
    }

    private Bitmap rainbow(Context context, Bitmap res, RenderScript rs, Allocation allocationA, Allocation allocationB) {

        // creating Filter Bitmap
        Bitmap filterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_png);
        filterBitmap = MyUtils.getResizedBitmap(filterBitmap, res.getHeight(), res.getWidth());

        // create allocation for the filter
        Allocation allocationF = Allocation.createFromBitmap(rs, filterBitmap);


        //Create script from rs file.
        ScriptC_filter1 histEqScript = new ScriptC_filter1(rs);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationF, allocationB);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
        return res;
    }

    private Bitmap anthony(Context context, Bitmap res, RenderScript rs, Allocation allocationA, Allocation allocationB) {

        // creating Filter Bitmap
        Bitmap filterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_anthony);
        filterBitmap = MyUtils.getResizedBitmap(filterBitmap, res.getHeight(), res.getWidth());

        // create allocation for the filter
        Allocation allocationF = Allocation.createFromBitmap(rs, filterBitmap);


        //Create script from rs file.
        ScriptC_filter1 histEqScript = new ScriptC_filter1(rs);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationF, allocationB);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
        return res;
    }

    private Bitmap mark(Context context, Bitmap res, RenderScript rs, Allocation allocationA, Allocation allocationB) {

        // creating Filter Bitmap
        Bitmap filterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.filter_mark);
        filterBitmap = MyUtils.getResizedBitmap(filterBitmap, res.getHeight(), res.getWidth());

        // create allocation for the filter
        Allocation allocationF = Allocation.createFromBitmap(rs, filterBitmap);


        //Create script from rs file.
        ScriptC_filter1 histEqScript = new ScriptC_filter1(rs);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationF, allocationB);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
        return res;
    }

    private Bitmap deepBlue(Bitmap res, RenderScript rs, Allocation allocationA, Allocation allocationB) {
        //Create script from rs file.
        ScriptC_blue histEqScript = new ScriptC_blue(rs);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
        return res;
    }

    private Bitmap summer(Bitmap res, RenderScript rs, Allocation allocationA, Allocation allocationB) {
        //Create script from rs file.
        ScriptC_warm histEqScript = new ScriptC_warm(rs);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
        return res;
    }

    private Bitmap histogramEqualization(Bitmap res, RenderScript rs, Allocation allocationA, Allocation allocationB) {
        //Create script from rs file.
        ScriptC_histEq histEqScript = new ScriptC_histEq(rs);

        //Get image size
        int width = res.getWidth();
        int height = res.getHeight();

        //Set size in script
        histEqScript.set_size(width * height);

        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB);

        //Call the rs method to compute the remap array
        histEqScript.invoke_createRemapArray();
//
//        //Call the second kernel
        histEqScript.forEach_remaptoRGB(allocationB, allocationA);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();
        return res;
    }


    public interface ApplyFilterCallback {
        void onFilterApplied(BitmapResult bitmap);
    }

}
