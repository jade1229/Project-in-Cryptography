package com.example.aes_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class Splash extends AppCompatActivity {
    private MediaPlayer splashSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        splashSound = MediaPlayer.create(this, R.raw.splash_sound);
        splashSound.start();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(Splash.this, MainActivity.class);
            startActivity(intent);
            finish();
        },2500);
    }
    protected void onDestroy() {
        super.onDestroy();

        // Release the MediaPlayer resources when the activity is destroyed
        if (splashSound != null) {
            splashSound.release();
            splashSound = null;
        }
    }
}