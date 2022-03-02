package com.drudotstech.customgallery;

import android.os.Bundle;

public class MoreFeaturesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_more_features);


        findViewById(R.id.iv_back).setOnClickListener(v -> close());
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close() {
        finish();
//        overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
//        overridePendingTransition(0, R.anim.activity_exit);
//        overridePendingTransition(R.anim.activity_enter, 0);
        overridePendingTransition(R.anim.anim_stay, R.anim.activity_exit);

    }
}