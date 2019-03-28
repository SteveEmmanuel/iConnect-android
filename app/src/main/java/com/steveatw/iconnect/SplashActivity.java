package com.steveatw.iconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences prefs = null;
    Intent intent;
    String uuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("com.steveatw.iconnect", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            // First run
            prefs.edit().putBoolean("firstrun", false).commit();

            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        else{
            // Not first run , uuid generated
            intent = new Intent(SplashActivity.this, QRCodeDisplayActivity.class);

            uuid = prefs.getString("uuid", null);

            intent.putExtra("uuid", uuid);

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        },1000);

    }

}