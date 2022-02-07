package com.drudotstech.customgallery.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;

/********** Developed by Drudots Technology **********
 * Created by : Usman Hassan on 08/05/2021 at 9:13 AM
 ******************************************************/


public class AnimationHelper {

    private int duration = 200;


    public AnimationHelper() {
    }

    public AnimationHelper(int duration) {
        this.duration = duration;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public void animateSlideToRight(ArrayList<View> views, int from, int to) {
        int difference = to - from;
        int duration = this.duration;
        switch (difference) {
            case 1:
                hideToRight(views.get(from), duration, difference);
                moveToRight(views.get(to), duration);
                break;

            case 2:
                duration = duration / 2;
                final int d = duration;
                hideToRight(views.get(from), duration, 1);
                moveToRight(views.get(from + 1), duration);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToRight(views.get(from + 1), d, 1);
                    moveToRight(views.get(to), d);
                }, d);
                break;

            case 3:
                duration = duration / 3;
                final int dd = duration;
                hideToRight(views.get(from), duration, 1);
                moveToRight(views.get(from + 1), duration);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToRight(views.get(from + 1), dd, 1);
                    moveToRight(views.get(from + 2), dd);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        hideToRight(views.get(from + 2), dd, 1);
                        moveToRight(views.get(to), dd);
                    }, dd);
                }, dd);
                break;
        }
    }

    public void animateSlideToLeft(ArrayList<View> views, int from, int to) {
        int difference = from - to;
        int duration = this.duration;
        switch (difference) {
            case 1:
                hideToLeft(views.get(from), duration, difference);
                moveToLeft(views.get(to), duration);
                break;

            case 2:
                duration = duration / 2;
                final int d = duration;
                hideToLeft(views.get(from), duration, 1);
                moveToLeft(views.get(from - 1), duration);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToLeft(views.get(from - 1), d, 1);
                    moveToLeft(views.get(to), d);
                }, d);
                break;

            case 3:
                duration = duration / 3;
                final int dd = duration;
                hideToLeft(views.get(from), duration, 1);
                moveToLeft(views.get(from - 1), duration);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToLeft(views.get(from - 1), dd, 1);
                    moveToLeft(views.get(from - 2), dd);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        hideToLeft(views.get(from - 2), dd, 1);
                        moveToLeft(views.get(to), dd);
                    }, dd);
                }, dd);
                break;
        }
    }

    public void animateSlideToTop(ArrayList<View> views, int from, int to) {
        int difference = to - from;
        int duration = this.duration;
        switch (difference) {
            case 1:
                hideToTop(views.get(from), duration, difference);
                moveToTop(views.get(to), duration);
                break;

            case 2:
                duration = duration / 2;
                final int d = duration;
                hideToTop(views.get(from), duration, 1);
                moveToTop(views.get(from + 1), duration);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToTop(views.get(from + 1), d, 1);
                    moveToTop(views.get(to), d);
                }, d);
                break;

            case 3:
                duration = duration / 3;
                final int dd = duration;
                hideToTop(views.get(from), duration, 1);
                moveToTop(views.get(from + 1), duration);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToTop(views.get(from + 1), dd, 1);
                    moveToTop(views.get(from + 2), dd);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        hideToTop(views.get(from + 2), dd, 1);
                        moveToTop(views.get(to), dd);
                    }, dd);
                }, dd);
                break;
        }
    }

    public void animateSlideToTop(ArrayList<View> views, int from, int to, float heightFrom, float heightTo) {
        int difference = to - from;
        int duration = this.duration;
        switch (difference) {
            case 1:
                hideToTop(views.get(from), duration, heightFrom);
                moveToTop(views.get(to), duration, heightTo);
                break;

            case 2:
                duration = duration / 2;
                final int d = duration;
                hideToTop(views.get(from), duration, heightFrom);
                moveToTop(views.get(from + 1), duration, heightTo);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToTop(views.get(from + 1), d, 1);
                    moveToTop(views.get(to), d);
                }, d);
                break;

            case 3:
                duration = duration / 3;
                final int dd = duration;
                hideToTop(views.get(from), duration, heightFrom);
                moveToTop(views.get(from + 1), duration, heightTo);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToTop(views.get(from + 1), dd, heightFrom);
                    moveToTop(views.get(from + 2), dd, heightTo);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        hideToTop(views.get(from + 2), dd, heightFrom);
                        moveToTop(views.get(to), dd, heightTo);
                    }, dd);
                }, dd);
                break;
        }
    }

    public void animateSlideToBottom(ArrayList<View> views, int from, int to, float heightFrom, float heightTo) {
        int difference = from - to;
        int duration = this.duration;
        switch (difference) {
            case 1:
                hideToBottom(views.get(from), duration, heightFrom);
                moveToBottom(views.get(to), duration, heightTo);
                break;

            case 2:
                duration = duration / 2;
                final int d = duration;
                hideToBottom(views.get(from), duration, heightFrom);
                moveToBottom(views.get(from - 1), duration, heightTo);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToBottom(views.get(from - 1), d, heightFrom);
                    moveToBottom(views.get(to), d, heightTo);
                }, d);
                break;

            case 3:
                duration = duration / 3;
                final int dd = duration;
                hideToBottom(views.get(from), duration, heightFrom);
                moveToBottom(views.get(from - 1), duration, heightTo);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToBottom(views.get(from - 1), dd, heightFrom);
                    moveToBottom(views.get(from - 2), dd, heightTo);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        hideToBottom(views.get(from - 2), dd, heightFrom);
                        moveToBottom(views.get(to), dd, heightTo);
                    }, dd);
                }, dd);
                break;
        }
    }

    public void animateSlideToBottom(ArrayList<View> views, int from, int to) {
        int difference = from - to;
        int duration = this.duration;
        switch (difference) {
            case 1:
                hideToBottom(views.get(from), duration, difference);
                moveToBottom(views.get(to), duration);
                break;

            case 2:
                duration = duration / 2;
                final int d = duration;
                hideToBottom(views.get(from), duration, 1);
                moveToBottom(views.get(from - 1), duration);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToBottom(views.get(from - 1), d, 1);
                    moveToBottom(views.get(to), d);
                }, d);
                break;

            case 3:
                duration = duration / 3;
                final int dd = duration;
                hideToBottom(views.get(from), duration, 1);
                moveToBottom(views.get(from - 1), duration);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideToBottom(views.get(from - 1), dd, 1);
                    moveToBottom(views.get(from - 2), dd);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        hideToBottom(views.get(from - 2), dd, 1);
                        moveToBottom(views.get(to), dd);
                    }, dd);
                }, dd);
                break;
        }
    }

    public void animateJumpToRight(ArrayList<View> views, int from, int to) {
        int difference = to - from;
        int duration = 500;
        hideToRight(views.get(from), duration, difference);
        moveToRight(views.get(to), duration);
    }

    public void animateJumpToLeft(ArrayList<View> views, int from, int to) {
        int difference = from - to;
        int duration = 500;
        hideToLeft(views.get(from), duration, difference);
        moveToLeft(views.get(to), duration);
    }

    public void hideToRight(View view, int duration, int times) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, times * view.getWidth());
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void moveToRight(View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, view.getWidth());
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveFromLeft(View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", -view.getWidth(), 0f);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveToRight(View view1, int duration) {
        view1.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view1, "translationX", -view1.getWidth(), 0);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void hideToLeft(View view, int duration, int times) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, times * -view.getWidth());
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void moveToLeft(View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, -view.getWidth());
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveFromRight(View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", view.getWidth(), 0f);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveToLeft(View view1, int duration) {
        view1.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view1, "translationX", view1.getWidth(), 0);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveToTop(View view, int duration) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, view.getHeight());
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveToTop(View view, int duration, float height) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, height);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveToTop(View view, float heightInDp) {
        heightInDp = convertDpToPixel(heightInDp, view.getContext());
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, -heightInDp);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveFromTop(View view1, int duration, float height) {
        view1.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view1, "translationY", height, 0f);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveFromBottom(View view1, int duration, float height) {
        view1.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view1, "translationY", -height, 0f);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {

            }
        });
    }

    public void pushFromTopToBottom(View view1, View view2, int duration, float heightFrom, float heightTo) {
        //view1 is coming from Top to its current position
        if (view1.getVisibility() != View.VISIBLE) {
            view1.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view1, "translationY", -heightFrom, 0f);
            objectAnimator.setDuration(duration / 2);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.start();
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    //view2 will move from current position to give height
                    hideToTop(view2, duration / 2, heightTo);
                }
            });
        }
    }

    public void pushFromBottomToTop(View view1, View view2, int duration, float heightFrom, float heightTo) {
        //view1 is coming from Bottom to its current position
        if (view1.getVisibility() != View.VISIBLE) {
            view1.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view1, "translationY", 3 * heightFrom, 0f);
            objectAnimator.setDuration(duration / 2);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.start();
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    //view2 will move from current position to give height
                    hideToBottom(view2, duration / 2, heightTo);
                }
            });
        }
    }

    public void moveToBottom(View view, int duration) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, -view.getHeight());
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveToBottom(View view, int duration, float height) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, -height);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveToBottom(View view, float heightInDp) {
        heightInDp = convertDpToPixel(heightInDp, view.getContext());
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, heightInDp);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveFromBottomToTop(View view, float heightInDp) {
        heightInDp = convertDpToPixel(heightInDp, view.getContext());
        view.setVisibility(View.VISIBLE);
        // first value is initial value and second is the final value
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", heightInDp, 0);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void moveFromTopToBottom(View view, float heightInDp) {
        heightInDp = convertDpToPixel(heightInDp, view.getContext());
        view.setVisibility(View.VISIBLE);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", -heightInDp, 0);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    public void hideToTop(View view, int duration, int times) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, times * view.getHeight());
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void hideToTop(View view, int duration, float height) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, height);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void hideToBottom(View view, int duration, int times) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, times * -view.getHeight());
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void hideToBottom(View view, int duration, float height) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, -height);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void rotateClockwise(View view, float angle) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", angle, 0f);
//        rotate.setRepeatCount(10);
        rotate.setDuration(duration);
        rotate.start();
    }

    public void rotateAntiClockwise(View view, float angle) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, angle);
//        rotate.setRepeatCount(10);
        rotate.setDuration(duration);
        rotate.start();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
