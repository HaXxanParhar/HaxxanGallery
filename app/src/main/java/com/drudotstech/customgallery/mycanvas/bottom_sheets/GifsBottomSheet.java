package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.app.Dialog;
import android.graphics.Movie;
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
public class GifsBottomSheet extends BottomSheetDialogFragment implements GetGifMoviesListTask.GifReceivedCallback {

    SelectGifCallback selectGifCallback;
    RecyclerView recyclerView;
    GifsAdapter adapter;
    List<Movie> movies;
    View loading;


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
        loading = view.findViewById(R.id.rl_loading);
        recyclerView = view.findViewById(R.id.rv_stickers);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        loading.setVisibility(View.VISIBLE);

        // get gif movies
        movies = new ArrayList<>();
        new GetGifMoviesListTask(movies, this).execute(view.getContext());

        return view;
    }

    @Override
    public void onGifReceived() {
        // add in the adapter
        loading.setVisibility(View.GONE);
        adapter = new GifsAdapter(requireContext(), movies, selectGifCallback);
        recyclerView.setAdapter(adapter);
    }
}