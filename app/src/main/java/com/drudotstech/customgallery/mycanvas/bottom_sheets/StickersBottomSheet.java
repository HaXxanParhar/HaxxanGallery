package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * This is the bottom sheet fragment for showing Stickers
 */
public class StickersBottomSheet extends BottomSheetDialogFragment {

    SelectStickerCallback selectStickerCallback;
    RecyclerView recyclerView;
    StickersAdapter adapter;
    int[] array = {
            R.drawable.s1,
            R.drawable.s2,
            R.drawable.s3,
            R.drawable.s4,
            R.drawable.s5,
            R.drawable.s6,
            R.drawable.s7,
            R.drawable.s8,
            R.drawable.s9,
            R.drawable.s10,
            R.drawable.s11,
            R.drawable.s12,
            R.drawable.s13,
            R.drawable.s14,
            R.drawable.s15,
            R.drawable.s16,
            R.drawable.s17,
            R.drawable.s18,
            R.drawable.s19,
            R.drawable.s20,
            R.drawable.s21,
            R.drawable.s22,
            R.drawable.s23,
            R.drawable.s24,
            R.drawable.s25,
            R.drawable.s26,
            R.drawable.s27,
            R.drawable.s28,
            R.drawable.s29,
            R.drawable.s30
    };


    public StickersBottomSheet() {
        // Required empty public constructor
    }

    public StickersBottomSheet(SelectStickerCallback selectStickerCallback) {
        this.selectStickerCallback = selectStickerCallback;
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
        adapter = new StickersAdapter(requireContext(), array, selectStickerCallback);
        recyclerView.setAdapter(adapter);

        return view;
    }
}