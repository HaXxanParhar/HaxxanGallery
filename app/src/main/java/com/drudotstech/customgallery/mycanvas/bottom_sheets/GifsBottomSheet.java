package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the bottom sheet fragment for showing Gifs
 */
public class GifsBottomSheet extends BottomSheetDialogFragment {

    SelectGifCallback selectGifCallback;
    RecyclerView recyclerView;
    GifsAdapter adapter;
    List<GifModel> movies;


    public GifsBottomSheet() {
        // Required empty public constructor
    }

    public GifsBottomSheet(SelectGifCallback selectGifCallback) {
        this.selectGifCallback = selectGifCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NO_FRAME, R.style.BottomSheetTheme);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stickers_bottom_sheet, container, false);
        recyclerView = view.findViewById(R.id.rv_stickers);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        movies = new ArrayList<>();
        final List<Integer> gifsList = getGifsList();
        for (int gif : gifsList) {
            movies.add(new GifModel(gif));
        }

        adapter = new GifsAdapter(requireContext(), movies, selectGifCallback);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Integer> getGifsList() {
        // get list list
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.gif_hi1);
        list.add(R.drawable.gif_hi6);
        list.add(R.drawable.gif_5);

        list.add(R.drawable.gif_hi4);
        list.add(R.drawable.gif_3);
        list.add(R.drawable.gif_night2);

        list.add(R.drawable.gif_1);
        list.add(R.drawable.gif_moring4);
        list.add(R.drawable.gif_2);

        list.add(R.drawable.gif_night1);
        list.add(R.drawable.gif_hi5);
        list.add(R.drawable.gif_hi7);

        list.add(R.drawable.gif_7);
        list.add(R.drawable.gif_4);
        list.add(R.drawable.gif_hi3);

        list.add(R.drawable.gif_night);
        list.add(R.drawable.gif_6);
        list.add(R.drawable.gif_night4);

        list.add(R.drawable.gif_8);
        list.add(R.drawable.gif_9);
        list.add(R.drawable.gif_night3);

        list.add(R.drawable.gif_10);
        list.add(R.drawable.gif_11);
        list.add(R.drawable.gif_night);

        list.add(R.drawable.gif_12);
        list.add(R.drawable.gif_13);
        list.add(R.drawable.gif_night5);

        return list;
    }

}