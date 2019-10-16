package com.example.doodlejump;

import android.app.Activity;
import android.content.Intent;
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
    }
}
