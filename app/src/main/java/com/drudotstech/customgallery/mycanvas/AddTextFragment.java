package com.drudotstech.customgallery.mycanvas;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.models.TextInfo;
import com.drudotstech.customgallery.utils.MyUtils;


public class AddTextFragment extends Fragment {

    private Activity context;

    /**
     * Callback to get the added/edited text on click of 'Done'
     */
    private AddTextCallback callback;

    /**
     * The background image of the fragment
     */
    private Bitmap blurredBitmap;

    /**
     * Text that should be passed in the constructor for edit
     */
    private String text;

    /**
     * This model contains necessary information about the text i.e. font colors
     */
    private TextInfo textInfo;

    /**
     * to indicate if user is editing  or adding new text
     */
    private boolean isEditing;

    public AddTextFragment() {
        // Required empty public constructor
    }

    /**
     * Use this constructor when user will type the text and not editing the text
     */
    public AddTextFragment(AddTextCallback callback, Activity context, Bitmap blurredBitmap, TextInfo textInfo) {
        this.callback = callback;
        this.context = context;
        this.blurredBitmap = blurredBitmap;
        this.text = "";
        this.textInfo = textInfo;
        isEditing = false;

    }

    /**
     * Use this constructor when user is editing the tex, pass the textt
     */
    public AddTextFragment(AddTextCallback callback, Activity context, Bitmap blurredBitmap, TextInfo textInfo, String text) {
        this.callback = callback;
        this.context = context;
        this.blurredBitmap = blurredBitmap;
        this.text = text;
        this.textInfo = textInfo;
        isEditing = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_text, container, false);

        EditText editText = view.findViewById(R.id.et_text);
        ImageView ivBlurred = view.findViewById(R.id.iv_blurred_image);

        View ivClose = view.findViewById(R.id.iv_close);
        View llDone = view.findViewById(R.id.ll_done);
        ImageView ivDone = view.findViewById(R.id.iv_done);
        TextView tvDone = view.findViewById(R.id.tv_done);

        ivBlurred.setImageBitmap(blurredBitmap);

        // set typeface & color
        if (textInfo == null) {
            editText.setTypeface(ResourcesCompat.getFont(context, R.font.metropolis_medium));
            editText.setTextColor(Color.WHITE);
        } else {
            editText.setTypeface(textInfo.typeface);
            editText.setTextColor(textInfo.getColor());
        }

        editText.setText(text);
        editText.requestFocus();

        // enable done button if text is not empty otherwise disable
        enableDisableDone(ivDone, tvDone, text);

        llDone.setOnClickListener(v -> {
            final String newText = editText.getText().toString();
            if (!TextUtils.isEmpty(newText)) {

                // return the text
                if (isEditing)
                    callback.onTextEdited(newText);
                else
                    callback.onTextAdded(newText);

                // hide keyboard and close
                closeFragment(editText);
            }
        });

        ivClose.setOnClickListener(v -> {
            closeFragment(editText);
        });


        // enable disable the done button on text change
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableDisableDone(ivDone, tvDone, s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // to close the fragment on the back pressed within the fragment
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                closeFragment(editText);
                return true;
            }
            return false;
        });

        return view;
    }

    private void enableDisableDone(ImageView ivDone, TextView tvDone, CharSequence text) {
        if (text.length() == 0) {
            tvDone.setTextColor(ContextCompat.getColor(context, R.color.greyer));
            ivDone.setImageResource(R.drawable.ic_check_grey);
        } else {
            tvDone.setTextColor(Color.WHITE);
            ivDone.setImageResource(R.drawable.ic_check);
        }
    }

    private void closeFragment(EditText editText) {
        MyUtils.hideKeyboard(editText, context);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public TextInfo getTextInfo() {
        return textInfo;
    }

    public void setTextInfo(TextInfo textInfo) {
        this.textInfo = textInfo;
    }

    public interface AddTextCallback {
        void onTextAdded(String text);

        void onTextEdited(String text);
    }
}