package com.example.hedefse;

import android.view.animation.Animation;
import android.widget.ImageView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Logoyu tanımlayın
        ImageView logoImageView = findViewById(R.id.logoImageView);

        // Animasyonu yükleyin
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logoImageView.startAnimation(fadeIn);

        // Splash ekranını belirli bir süre gösterin ve ardından ana aktiviteye geçin
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000); // 3000 milisaniye = 3 saniye
    }
}
