package com.example.doodlejump;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class GameView extends View { //custom view class
//add platform sizes

    Handler handler;
    Runnable runnable;
    final long tick=20; //ms
    Bitmap background;
    Bitmap leftArrow,rightArrow;
    Display display;
    Point point;
    int dWidth,dHeight; //width and height of device
    Rect rect;
    Bitmap[] doodles;
    int guyFrame=0;
    double velocity=0;
    double gravity=2;
    double guyX,guyY;
    Bitmap[] platforms = new Bitmap[4];
    double platformX,platformY;
    boolean lose=false;
    int score = 0;
    int maxScore;
    Random rand = new Random();
    final Paint sc = new Paint();
    int platformnum=rand.nextInt(4);
    MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.jump);
    SharedPreferences sp = getContext().getSharedPreferences("maxScore",0);
    MediaPlayer[] deathSound = new MediaPlayer[2];
    double hardness=0;
    MediaPlayer bg = MediaPlayer.create(getContext(),R.raw.bg);

    public GameView(Context context)
    {
        super(context);
        handler=new Handler();
        runnable=new Runnable() {

            @Override
            public void run() {
                if (!lose)
                    invalidate(); //calls onDraw

                else
                {
                    Intent i = new Intent(getContext(),startGame.class);
                    ((Activity)getContext()).recreate(); //reopening app
                   // getContext().startActivity(i);
                    lose=false;
                }
            }
        };
        bg.start();
        maxScore = sp.getInt("maxScore",0);
        background= BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        leftArrow=BitmapFactory.decodeResource(getResources(),R.drawable.leftarrow);
        rightArrow=BitmapFactory.decodeResource(getResources(),R.drawable.rightarrow);
        platforms[0]=BitmapFactory.decodeResource(getResources(),R.drawable.platform1);
        platforms[1]=BitmapFactory.decodeResource(getResources(),R.drawable.platform2);
        platforms[2]=BitmapFactory.decodeResource(getResources(),R.drawable.platform3);
        platforms[3]=BitmapFactory.decodeResource(getResources(),R.drawable.platform4);
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth=point.x;
        dHeight=point.y;
        rect= new Rect(0,0,dWidth,dHeight);
        doodles=new Bitmap[2];
        doodles[0] =  BitmapFactory.decodeResource(getResources(),R.drawable.guy);
        doodles[1] =  BitmapFactory.decodeResource(getResources(),R.drawable.guy2); //jumping anim
        guyX=dWidth/2;
        guyY=dHeight/2;
        platformX=dWidth/2;
        platformY=dHeight-300;
        deathSound[0]=MediaPlayer.create(getContext(),R.raw.die);
        deathSound[1]=MediaPlayer.create(getContext(),R.raw.die2);
    }

    @Override
    protected void onDraw(final Canvas canvas) { //draw views here
        super.onDraw(canvas);

        canvas.drawBitmap(background,null,rect,null);
        canvas.drawBitmap(leftArrow,0,dHeight-200,null);
        canvas.drawBitmap(rightArrow,dWidth-200,dHeight-200,null);
        canvas.drawBitmap(platforms[platformnum],(float)platformX,(float)platformY,null);
        handler.postDelayed(runnable,tick);
        sc.setColor(Color.BLACK);
        sc.setTextSize(50);
        canvas.drawText("Score: "+score,100,100,sc);
        sc.setColor(Color.BLACK);
        sc.setTextSize(50);
        canvas.drawText("Your best: "+maxScore,100,170,sc);
        canvas.drawBitmap(doodles[0],(float)guyX,(float)guyY,null); // animation.....
       // canvas.drawBitmap(doodles[0],(float)guyX,(float)guyY,null);
        guyY+=velocity+gravity;
        Log.d("Info:","GuyY: " + guyY + "Platform Y: "+(platformY-doodles[0].getHeight()));
        boolean oneTime=false;
        if (guyY>=platformY-doodles[0].getHeight()&&guyX>=platformX-doodles[0].getWidth()&&guyY<platformY-doodles[0].getHeight()+60&&guyX<=platformX+platforms[platformnum].getWidth()&&oneTime==false)
        {
            canvas.drawBitmap(doodles[1],(float)guyX,(float)guyY,null);
            mp.start();
            velocity=0;
            velocity-=50;
            hardness+=0.3;
            platformX=new Random().nextInt(dWidth-platforms[platformnum].getWidth()-platforms[platformnum].getWidth())+platforms[platformnum].getWidth();
          //  platformY=new Random().nextInt(dHeight/2)+dHeight/2-200;
            oneTime=true;
            score+=10;
            platformnum=rand.nextInt(4);
            if (maxScore<score) { maxScore=score; sp.edit().putInt("maxScore",maxScore).commit(); //SAVE MAXSCORE ON DEVICE
            }
        }
        velocity= velocity + gravity+hardness;
        if (guyY>dHeight) //reset game here
        {
           /* final Paint p = new Paint();
            p.setColor(Color.BLACK);
            p.setTextSize(50);
            canvas.drawText("You lost",dWidth/2,dHeight/2,p);
            Bitmap reset = BitmapFactory.decodeResource(getResources(),R.drawable.reset);
            canvas.drawBitmap(reset,dWidth/2,dHeight/2,null);*/
           deathSound[new Random().nextInt(2)].start();
            score=0;
            lose=true;

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getX()>0&&event.getX()< leftArrow.getWidth())
        {
            guyX-=10;
        }
        if (event.getX()>dWidth-200&&event.getX()< dWidth)
        {
            guyX+=10;
        }
        return true;
    }

}


