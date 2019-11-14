package com.example.doodlejump;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

public class startGame extends Activity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.bg.stop();
        gameView.middle.stop();
        for (MediaPlayer p : gameView.deathSound)
            p.stop();

    }
}
