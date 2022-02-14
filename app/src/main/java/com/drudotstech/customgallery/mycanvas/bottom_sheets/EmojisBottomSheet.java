package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the bottom sheet fragment for showing Stickers
 */
public class EmojisBottomSheet extends BottomSheetDialogFragment {

    SelectEmojiCallback selectStickerCallback;
    RecyclerView recyclerView;
    EmojisAdapter adapter;
    List<String> list;


    public EmojisBottomSheet() {
        // Required empty public constructor
    }

    public EmojisBottomSheet(SelectEmojiCallback selectStickerCallback) {
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

        // get emojis list
        String[] emojis = getResources().getStringArray(R.array.photo_editor_emoji);

        // convert them into String
        list = new ArrayList<>();
        for (String string : emojis) {
            String emoji = convertEmoji(string);
            if (!emoji.isEmpty())
                list.add(emoji);
        }

        // add in the adapter
        adapter = new EmojisAdapter(requireContext(), list, selectStickerCallback);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private String convertEmoji(String emoji) {
        try {
            final int num = Integer.parseInt(emoji.substring(2), 16);
            final char[] chars = Character.toChars(num);
            return new String(chars);
        } catch (Exception e) {
            return "";
        }
    }
}