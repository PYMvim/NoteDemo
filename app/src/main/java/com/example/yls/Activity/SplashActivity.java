package com.example.yls.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.yls.notepaddemo.R;

public class SplashActivity extends Activity {
    private Handler mhandler = new Handler();
    private Handler handler;
    private Runnable Splash_runnable;
    private Runnable runnable;
    private static final int DELAY = 3000;
    private int num = 3;
    private TextView txtJump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FindViews();
        initHandler();
        navigataToLogin();
        handler.post(runnable);
    }

    private void navigataToMain() {
        Splash_runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        mhandler.postDelayed(Splash_runnable,DELAY);
    }

    private void initHandler() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                txtJump.setText("("+num+"s)"+"跳过");
                num --;
                if(num>=0){
                    handler.postDelayed(runnable,1000);
                }
            }
        };
    }

    private void FindViews() {
        txtJump = (TextView) findViewById(R.id.txt_Jump);
        txtJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mhandler.removeCallbacks(Splash_runnable);
                navigataToLogin();
                txtJump.setClickable(false);
            }
        });
    }

    private void navigataToLogin() {
        Splash_runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        mhandler.postDelayed(Splash_runnable,DELAY);
    }
}
