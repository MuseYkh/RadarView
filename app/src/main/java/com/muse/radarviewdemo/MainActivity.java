package com.muse.radarviewdemo;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadarView radarView = findViewById(R.id.radarView);
        radarView.setData(new String[]{"精通", "力量", "敏捷", "智力", "暴击"}, new float[]{50, 12, 30, 45, 50}, 100);
        radarView.setTextSize(12);
        radarView.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }
}
