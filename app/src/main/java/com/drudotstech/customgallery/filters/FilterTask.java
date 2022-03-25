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
import com.drudotstech.customgallery.croppy.croppylib.main.BitmapResult;
import com.drudotstech.customgallery.utils.MyUtils;
import com.drudotstech.filterfactory.ScriptC_filter;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/27/2022 at 4:29 PM
 ******************************************************/


public class FilterTask extends AsyncTask<Context, Void, BitmapResult> {

    private final Bitmap image;
    private final FilterType filterType;
    private ApplyFilterCallback applyFilterCallback;

    private float y, cb, cr;
    private boolean isBW;


    public FilterTask(Bitmap image, FilterModel filterModel, ApplyFilterCallback applyFilterCallback) {
        this.image = image;
        this.filterType = filterModel.getFilterType();
        this.y = filterModel.getY();
        this.cb = filterModel.getCb();
        this.cr = filterModel.getCr();
        this.isBW = filterModel.isBlackWhite();
        this.applyFilterCallback = applyFilterCallback;
    }

    public FilterTask(Bitmap image, FilterModel filterModel) {
        this.image = image;
        this.filterType = filterModel.getFilterType();
        this.y = filterModel.getY();
        this.cb = filterModel.getCb();
        this.cr = filterModel.getCr();
        this.isBW = filterModel.isBlackWhite();
    }

    public FilterTask(Bitmap image, FilterType filterType, ApplyFilterCallback applyFilterCallback) {
        this.image = image;
        this.filterType = filterType;
        this.applyFilterCallback = applyFilterCallback;
    }

    public FilterTask(Bitmap image, ApplyFilterCallback applyFilterCallback, float y, float cb, float cr, boolean isBW) {
        this.image = image;
        this.filterType = FilterType.YCBCR;
        this.applyFilterCallback = applyFilterCallback;
        this.y = y;
        this.cb = cb;
        this.cr = cr;
        this.isBW = isBW;
    }

    @Override
    protected BitmapResult doInBackground(Context... params) {
        try {
            Context context = params[0];
            Bitmap bitmap = applyFilter(context);
            return new BitmapResult(true, bitmap);
        } catch (Exception e) {
            return new BitmapResult(false, e);
        }
    }

    @Override
    protected void onPostExecute(BitmapResult bitmapResult) {
        super.onPostExecute(bitmapResult);
        if (applyFilterCallback != null)
            applyFilterCallback.onFilterApplied(bitmapResult);
    }

    public Bitmap applyFilter(Context context) {

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

            case YCBCR:
                return filterWithYcbcr(res, rs, allocationA, allocationB);

            case DEEP_BLUE:
            case WINTER:
                return deepBlue(res, rs, allocationA, allocationB);

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

    private Bitmap filterWithYcbcr(Bitmap bitmap, RenderScript renderScript, Allocation allocationIn, Allocation allocationOut) {

        ScriptC_filter script = new ScriptC_filter(renderScript);
        script.set_dY(y);
        script.set_dCb(cb);
        script.set_dCr(cr);

        if (isBW)
            script.set_isBW(1);
        else
            script.set_isBW(0);

        final Bitmap copy = bitmap.copy(bitmap.getConfig(), true);
        script.forEach_root(allocationIn, allocationOut);
        allocationOut.copyTo(copy);
//        bitmap.recycle(); // todo: uncomment this to see if we need to recycle or not

        return copy;
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
        ScriptC_filter1 scriptC_filter1 = new ScriptC_filter1(rs);

        //Call the first kernel.
        scriptC_filter1.forEach_root(allocationA, allocationF, allocationB);

        //Copy script result into bitmap
        allocationB.copyTo(res);

        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        scriptC_filter1.destroy();
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
