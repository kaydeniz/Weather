package com.kaydeniz.weather;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SettingsActivity extends AppCompatActivity {

    private ImageView ivBackSettings;
    private RelativeLayout rlManageCity,rlAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        ivBackSettings= findViewById(R.id.ivBackSettings);
        rlManageCity=findViewById(R.id.rlManageCity);
        rlAboutUs=findViewById(R.id.rlAboutUs);

        ivBackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        rlAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SettingsActivity.this,AboutUsActivity.class);
                startActivity(i);
            }
        });

        rlManageCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SettingsActivity.this,ManageCityActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}
