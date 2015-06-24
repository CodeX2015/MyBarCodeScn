package com.android.app.mybarcodescn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;





/**
 * Created by CodeX on 09.06.2015.
 */

public class ActivitySplash extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

      new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ActivitySplash.this,ActivityCodeScanner.class));
                finish();
            }
        },1000);
    }
}
