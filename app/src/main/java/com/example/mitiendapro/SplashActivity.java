package com.example.mitiendapro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    private boolean splashScreenActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!splashScreenActive){
        setContentView(R.layout.activity_splash);
        TextView splashText = findViewById(R.id.txt_splash);

            splashText.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this,R.anim.splaash_text_animation));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        },2000);
        splashScreenActive=true;


    }else {
           // Toast.makeText(this, "AAA", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
}
}