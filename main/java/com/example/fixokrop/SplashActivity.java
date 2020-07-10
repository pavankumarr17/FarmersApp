package com.example.fixokrop;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;

public class SplashActivity extends Activity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isFirstRun=getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isFirstRun",true);

        if(!isFirstRun){
            intent=new Intent(SplashActivity.this,MainActivity.class);
        }
        else{
            intent=new Intent(SplashActivity.this,User.class);
        }

        setContentView(R.layout.activity_splash);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        },3000);
    }
}