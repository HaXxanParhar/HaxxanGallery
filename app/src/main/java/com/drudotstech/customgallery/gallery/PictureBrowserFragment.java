package com.drudotstech.customgallery.gallery;

import static androidx.core.view.ViewCompat.setTransitionName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.gallery.interfaces.OnPictureSelectedCallBack;
import com.drudotstech.customgallery.gallery.interfaces.imageIndicatorListener;
import com.drudotstech.customgallery.gallery.models.GalleryMediaModel;
import com.drudotstech.customgallery.gallery.recyclerview.RecyclerViewPagerImageIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

public class PictureBrowserFragment extends Fragment implements imageIndicatorListener {


    // ---------------------------------------- V I E W S ------------------------------------------
    private ImageView image;
    private View ivSelectPhoto, ivCancel;
    private LinearLayout upload_lin;


    // ------------------------------------ R E C Y C L E R V I E W --------------------------------
    private ViewPager imagePager;
    private ImagesPagerAdapter pagerAdapter;
    private ArrayList<GalleryMediaModel> list = new ArrayList<>();

    private RecyclerView indicatorRecycler;


    // -------------------------------------- V A R I A B L E S ------------------------------------
    private Context context;
    private OnPictureSelectedCallBack onPictureSelectedCallBack;
    private Bitmap selected_img_bitmap;
    private String input = "";
    private String imageEncoded;
    private int viewVisibilityController;
    private int viewVisibilitylooper;
    private int previousSelected = -1;
    private int currentPosition;
    private int position;


    public PictureBrowserFragment() {

    }

    public PictureBrowserFragment(ArrayList<GalleryMediaModel> list, int position, Context context, OnPictureSelectedCallBack onPictureSelectedCallBack) {
        this.list = list;
        this.position = position;
        currentPosition = position;
        this.context = context;
        this.onPictureSelectedCallBack = onPictureSelectedCallBack;
    }

    public static PictureBrowserFragment newInstance(ArrayList<GalleryMediaModel> allImages, int imagePosition,
                                                     Context anim, OnPictureSelectedCallBack onPictureSelectedCallBack) {
        return new PictureBrowserFragment(allImages, imagePosition, anim, onPictureSelectedCallBack);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.picture_browser, container, false);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivSelectPhoto = view.findViewById(R.id.tv_select_picture);
        ivCancel = view.findViewById(R.id.tv_cancel);

        ivSelectPhoto.setOnClickListener(v -> {
            onPictureSelectedCallBack.onPictureSelected(currentPosition);
        });


        ivCancel.setOnClickListener(v -> ((FragmentActivity) context).getSupportFragmentManager().popBackStack());


        /**
         * initialisation of the recyclerView visibility control integers
         */
        viewVisibilityController = 0;
        viewVisibilitylooper = 0;

        /**
         * setting up the viewPager with images
         */
        imagePager = view.findViewById(R.id.imagePager);
        pagerAdapter = new ImagesPagerAdapter();
        imagePager.setAdapter(pagerAdapter);
        imagePager.setOffscreenPageLimit(3);
        imagePager.setCurrentItem(position);//displaying the image at the current position passed by the ImageDisplay Activity

        /**
         * setting up the recycler view indicator for the viewPager
         */
        indicatorRecycler = view.findViewById(R.id.indicatorRecycler);
        indicatorRecycler.hasFixedSize();
        indicatorRecycler.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.HORIZONTAL, false));
        RecyclerViewPagerImageIndicator indicatorAdapter = new RecyclerViewPagerImageIndicator(list, getContext(), this);
        indicatorRecycler.setAdapter(indicatorAdapter);

        //adjusting the recyclerView indicator to the current position of the viewPager, also highlights the image in recyclerView with respect to the
        //viewPager's position
        list.get(position).setVisible(true);
        previousSelected = position;
        indicatorAdapter.notifyDataSetChanged();
        indicatorRecycler.scrollToPosition(position);


        /**
         * this listener controls the visibility of the recyclerView
         * indication and it current position in respect to the image ViewPager
         */
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (previousSelected != -1) {
                    list.get(previousSelected).setVisible(false);
                    previousSelected = position;
                    list.get(position).setVisible(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                    indicatorRecycler.scrollToPosition(position);

                    currentPosition = position;

                } else {

                    previousSelected = position;
                    list.get(position).setVisible(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                    indicatorRecycler.scrollToPosition(position);

                    currentPosition = position;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        indicatorRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 *  uncomment the below condition to control recyclerView visibility automatically
                 *  when image is clicked also uncomment the condition set on the image's onClickListener in the ImagesPagerAdapter adapter
                 */
                /*if(viewVisibilityController == 0){
                    indicatorRecycler.setVisibility(View.VISIBLE);
                    visibiling();
                }else{
                    viewVisibilitylooper++;
                }*/
                return false;
            }
        });
    }

    /**
     * this method of the imageIndicatorListerner interface helps in communication between the fragment and the recyclerView Adapter
     * each time an iten in the adapter is clicked the position of that item is communicated in the fragment and the position of the
     * viewPager is adjusted as follows
     *
     * @param ImagePosition The position of an image item in the RecyclerView Adapter
     */
    @Override
    public void onImageIndicatorClicked(int ImagePosition) {

        //the below lines of code highlights the currently select image in  the indicatorRecycler with respect to the viewPager position
        if (previousSelected != -1) {
            list.get(previousSelected).setVisible(false);
            previousSelected = ImagePosition;
            indicatorRecycler.getAdapter().notifyDataSetChanged();
        } else {
            previousSelected = ImagePosition;
        }

        imagePager.setCurrentItem(ImagePosition);
    }

    /**
     * function for controlling the visibility of the recyclerView indicator
     */
    private void visibiling() {
        viewVisibilityController = 1;
        final int checker = viewVisibilitylooper;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (viewVisibilitylooper > checker) {
                    visibiling();
                } else {
                    indicatorRecycler.setVisibility(View.GONE);
                    viewVisibilityController = 0;

                    viewVisibilitylooper = 0;
                }
            }
        }, 4000);
    }

    /**
     * the imageViewPager's adapter
     */
    private class ImagesPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup containerCollection, int position) {
            LayoutInflater layoutinflater = (LayoutInflater) containerCollection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutinflater.inflate(R.layout.picture_browser_pager, null);
            image = view.findViewById(R.id.image);

            setTransitionName(image, String.valueOf(position) + "picture");

            GalleryMediaModel pic = list.get(position);
            Glide.with(context)
                    .load(pic.getMediaPath())
                    .apply(new RequestOptions().fitCenter())
                    .into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if(indicatorRecycler.getVisibility() == View.GONE){
//                        indicatorRecycler.setVisibility(View.VISIBLE);
//                    }else{
//                        indicatorRecycler.setVisibility(View.GONE);
//                    }
//

                }
            });


            ((ViewPager) containerCollection).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup containerCollection, int position, Object view) {
            ((ViewPager) containerCollection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View) object);
        }
    }
}