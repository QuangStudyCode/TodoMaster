package com.example.todomaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.btnStartSplash)
    Button btnStartSplash;

    @BindView(R.id.btnLoginSplash)
    Button btnLoginSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //code that displays the content in full screen mode
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);
        btnStartSplash.setOnClickListener(this);
        btnLoginSplash.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartSplash:
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                break;

            case R.id.btnLoginSplash:
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                break;
        }
    }
}