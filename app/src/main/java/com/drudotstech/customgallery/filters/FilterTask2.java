package com.drudotstech.customgallery.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.renderscript.Allocation;
import androidx.renderscript.RenderScript;

import com.drudotstech.filterfactory.ScriptC_filter;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/24/2022 at 4:31 PM
 ******************************************************/


public class FilterTask2 extends AsyncTask<Context, Void, FilterResult> {

    private final Bitmap bitmap;
    private final boolean isBW;
    private float y, cb, cr;
    private ApplyFilterCallback callback;

    public FilterTask2(Bitmap bitmap, boolean isBW, float y, float cb, float cr, ApplyFilterCallback callback) {
        this.bitmap = bitmap;
        this.isBW = isBW;
        this.y = y;
        this.cb = cb;
        this.cr = cr;
        this.callback = callback;
    }

    @Override
    protected FilterResult doInBackground(Context... voids) {

        try {
            Context context = voids[0];

            RenderScript renderScript = RenderScript.create(context);

            ScriptC_filter script = new ScriptC_filter(renderScript);
            script.set_dY(y);
            script.set_dCb(cb);
            script.set_dCr(cr);

            if (isBW)
                script.set_isBW(1);
            else
                script.set_isBW(0);

            final Bitmap copy = bitmap.copy(bitmap.getConfig(), true);
            Allocation allocationIn = Allocation.createFromBitmap(renderScript, copy);
            Allocation allocationOut = Allocation.createTyped(renderScript, allocationIn.getType());

            script.forEach_root(allocationIn, allocationOut);

            allocationOut.copyTo(copy);
            return new FilterResult(true, copy);
        } catch (Exception e) {
            return new FilterResult(false, e);
        }
    }

    @Override
    protected void onPostExecute(FilterResult result) {
        super.onPostExecute(result);
        callback.onFilterApplied(result);
    }

    public interface ApplyFilterCallback {
        void onFilterApplied(FilterResult result);
    }
}
