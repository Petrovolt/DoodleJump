package com.example.doodlejump;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaPlayer mp = MediaPlayer.create(this,R.raw.intro);
        mp.start();
        setContentView(R.layout.activity_main);
    }

    public void startGame(View view)
    {
        Intent intent = new Intent(this,startGame.class);
        startActivity(intent);
        finish();
    }
}
