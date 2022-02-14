package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drudotstech.customgallery.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This is the bottom sheet fragment for showing Stickers
 */
public class WidgetsBottomSheet extends BottomSheetDialogFragment {

    private SelectWidgetCallback selectStickerCallback;
    private RecyclerView recyclerView;
    private WidgetsAdapter adapter;
    private List<WidgetModel> list;
    private String location;
    private String shortLocation;

    public WidgetsBottomSheet() {
        // Required empty public constructor
    }

    public WidgetsBottomSheet(String location, String shortLocation, SelectWidgetCallback selectStickerCallback) {
        this.location = location;
        this.shortLocation = shortLocation;
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

        setRecyclerView();


        return view;
    }

    private void setRecyclerView() {
        list = new ArrayList<>();

        // time
        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
        list.add(new WidgetModel(time, R.font.time_font));

        // location
        if (shortLocation != null)
            list.add(new WidgetModel(shortLocation, R.font.metropolis_medium));

        if (location != null)
            list.add(new WidgetModel(location, R.font.metropolis_medium));

        // temperature
        list.add(new WidgetModel("26.0 °C", R.font.metropolis_bold));

        // others
        list.add(new WidgetModel("What's Up?", R.font.cheri));
        list.add(new WidgetModel("Good Morning", R.font.muchomacho));
        list.add(new WidgetModel("Hello", R.font.cheri));
        list.add(new WidgetModel("I Love U", R.font.bubble_love));
        list.add(new WidgetModel("Good Bye", R.font.muchomacho));
        list.add(new WidgetModel("Good Night", R.font.shake_it_off));

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        adapter = new WidgetsAdapter(requireContext(), list, selectStickerCallback);
        recyclerView.setAdapter(adapter);
    }

}