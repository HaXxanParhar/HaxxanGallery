package com.drudotstech.customgallery.mycanvas;

import android.graphics.PointF;

import com.drudotstech.customgallery.mycanvas.models.LayerModel;
import com.drudotstech.customgallery.utils.ThreadPool;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/9/2022 at 5:30 PM
 ******************************************************/


public class BrushStore {

    public static void curvedPen2(float currentX, float currentY, float x, float y, LayerModel layer) {
        layer.path.lineTo(x, y);
        int width = (int) layer.paint.getStrokeWidth() / 2;
        layer.path.moveTo(currentX, currentY);

        layer.path.lineTo(currentX, currentY - width);
        layer.path.lineTo(currentX, currentY + width);

        for (int i = 0; i < width; i++) {
            layer.path.moveTo(currentX, currentY + i);
            layer.path.lineTo(x, currentY + i);
            layer.path.moveTo(currentX, currentY - i);
            layer.path.lineTo(x, currentY - i);
        }

        layer.path.moveTo(currentX, currentY);
    }

    public static void straightLine(float currentX, float currentY, float x, float y, LayerModel layer) {

//        ThreadPool.getInstance().execute(() -> {
        layer.path.lineTo(x, y);
        int width = (int) layer.paint.getStrokeWidth() / 2;
//                width = 10;
        float dx = x - currentX;
        float dy = y - currentY;
        float ratio = dx / dy;
        if (ratio < 0) ratio = ratio * -1;
//                Log.d("BrushStore", " dx = " + dx + " dy : " + dy + " ratio : " + ratio);
        int k = 0;

        if (dx < 0) {
            for (float i = 0; i < (Math.abs(dx)); i++) {
                float yOffset = i / ratio;
                k++;
                if (dy < 0) {
                    layer.path.moveTo(currentX - i, currentY - width - yOffset);
                    layer.path.lineTo(currentX - i, currentY + width - yOffset);
                } else {
                    layer.path.moveTo(currentX - i, currentY - width + yOffset);
                    layer.path.lineTo(currentX - i, currentY + width + yOffset);
                }
            }
        } else {
            for (float i = 0; i < dx; i++) {
                float yOffset = i / ratio;
                k++;
                if (dy < 0) {
                    layer.path.moveTo(currentX + i, currentY - width - yOffset);
                    layer.path.lineTo(currentX + i, currentY + width - yOffset);
                } else {
                    layer.path.moveTo(currentX + i, currentY - width + yOffset);
                    layer.path.lineTo(currentX + i, currentY + width + yOffset);
                }
            }
        }
//        Log.d("BrushStore", "--> " + new String(new char[k]).replace("\0", "-"));
        layer.path.moveTo(currentX, currentY);
//        });
    }

    public static void urduBrush(float currentX, float currentY, float x, float y, LayerModel layer) {

        ThreadPool.getInstance().execute(() -> {
            layer.path.lineTo(x, y);
            int width = (int) layer.paint.getStrokeWidth() / 2;
            float dx = x - currentX;
            float dy = y - currentY;
            float ratio = dx / dy;
            ratio = ratio * 1.2f; // added this line only
            if (ratio < 0) ratio = ratio * -1;
            int k = 0;

            if (dx < 0) {
                for (float i = 0; i < (Math.abs(dx)); i++) {
                    float yOffset = i / ratio;
                    k++;
                    if (dy < 0) {
                        layer.path.moveTo(currentX - i, currentY - width - yOffset);
                        layer.path.lineTo(currentX - i, currentY + width - yOffset);
                    } else {
                        layer.path.moveTo(currentX - i, currentY - width + yOffset);
                        layer.path.lineTo(currentX - i, currentY + width + yOffset);
                    }
                }
            } else {
                for (float i = 0; i < dx; i++) {
                    float yOffset = i / ratio;
                    k++;
                    if (dy < 0) {
                        layer.path.moveTo(currentX + i, currentY - width - yOffset);
                        layer.path.lineTo(currentX + i, currentY + width - yOffset);
                    } else {
                        layer.path.moveTo(currentX + i, currentY - width + yOffset);
                        layer.path.lineTo(currentX + i, currentY + width + yOffset);
                    }
                }
            }
//            Log.d("BrushStore", "--> " + new String(new char[k]).replace("\0", "-"));
            layer.path.moveTo(currentX, currentY);
        });
    }

    /**
     * Work out the angle from the x horizontal winding anti-clockwise
     * in screen space.
     * <p>
     * The value returned from the following should be 315.
     * <pre>
     * x,y -------------
     *     |  1,1
     *     |    \
     *     |     \
     *     |     2,2
     * </pre>
     *
     * @return - a double from 0 to 360
     */
    public static double angleOf(PointF point1, PointF point2) {
        // NOTE: Remember that most math has the Y axis as positive above the X.
        // However, for screens we have Y as positive below. For this reason,
        // the Y values are inverted to get the expected results.
        final double deltaY = (point1.y - point2.y);
        final double deltaX = (point2.x - point1.x);
        final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360d + result) : result;
    }

    /* Formulas (Different from normal math formulas to handle Y-coordinate)
     *  Angle = atan( (x2 - x1) / (y1 - y2) )
     *  Distance = sqrt( pow((x1 - x2),2) + pow((y1 - y2),2) )
     *  y2 = y1 - ( Distance * sin(Angle) )
     *  x2 = x1 + ( Distance * cos(Angle) )
     * */

    //   Angle = atan( (x2 - x1) / (y1 - y2) )
    public static double angleOf(float x1, float y1, float x2, float y2) {
        // NOTE: Remember that most math has the Y axis as positive above the X.
        // However, for screens we have Y as positive below. For this reason,
        // the Y values are inverted to get the expected results.
        final double deltaY = (y1 - y2);
        final double deltaX = (x2 - x1);
        final double result = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360 + result) : result;
    }

    //  Distance = sqrt( pow((x1 - x2),2) + pow((y1 - y2),2) )
    public static double distanceBetween(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    //  x2 = x1 + ( Distance * cos(Angle) )
    public static double getX2(double angle, double distance, float x1) {
        return (x1 + (distance * Math.cos(Math.toRadians(angle))));
    }

    //  y2 = y1 - ( Distance * sin(Angle) )
    public static double getY2(double angle, double distance, float y1) {
        return (y1 - (distance * Math.sin(Math.toRadians(angle))));
    }
}
