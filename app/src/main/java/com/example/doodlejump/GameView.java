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
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class GameView extends View { //custom view class
//add platform sizes

    Handler handler;
    Runnable runnable;
    final long tick=20; //ms
    Bitmap background,leftArrow,rightArrow;
    Display display;
    Point point;
    int dWidth,dHeight; //width and height of device
    Rect rect;
    Bitmap[] doodles;
    int guyFrame=0;
    double velocity=0,gravity=2,guyX,guyY,hardness=0;
    Bitmap[] platforms = new Bitmap[4];
    double platformX,platformY;
    boolean lose=false;
    int score = 0,maxScore;
    Random rand = new Random();
    final Paint sc = new Paint();
    int platformnum=rand.nextInt(4);
    MediaPlayer jump = MediaPlayer.create(getContext(),R.raw.jump);
    SharedPreferences sp = getContext().getSharedPreferences("maxScore",0);
    MediaPlayer[] deathSound = new MediaPlayer[2];
    MediaPlayer bg = MediaPlayer.create(getContext(),R.raw.bg);
    MediaPlayer middle = MediaPlayer.create(getContext(),R.raw.middle);
    float guyMiddleX;
    float platformMiddleX;

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
        /*
        --------Game initialization----------
         */
        bg.setVolume((float)0.2,(float)0.2);
        bg.setLooping(true);
        bg.start();
        maxScore = sp.getInt("maxScore",0); //reading maxscore from files
        background= BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        leftArrow=BitmapFactory.decodeResource(getResources(),R.drawable.leftarrow);
        rightArrow=BitmapFactory.decodeResource(getResources(),R.drawable.rightarrow);
        platforms[0]=BitmapFactory.decodeResource(getResources(),R.drawable.platform1);
        platforms[1]=BitmapFactory.decodeResource(getResources(),R.drawable.platform2);
        platforms[2]=BitmapFactory.decodeResource(getResources(),R.drawable.platform3);
        platforms[3]=BitmapFactory.decodeResource(getResources(),R.drawable.platform4);
        /*
                Getting device dimensions
         */
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth=point.x;
        dHeight=point.y;
        /*
               --------------------------
         */
        rect= new Rect(0,0,dWidth,dHeight);
        doodles=new Bitmap[3]; //3 animation moves
        doodles[0] =  BitmapFactory.decodeResource(getResources(),R.drawable.up);
        doodles[1] = BitmapFactory.decodeResource(getResources(),R.drawable.down);
        doodles[2] =  BitmapFactory.decodeResource(getResources(),R.drawable.guy2); //jumping anim
        guyX=dWidth/2;
        guyY=dHeight/2;
        platformX=dWidth/2;
        platformY=dHeight-300;
        deathSound[0]=MediaPlayer.create(getContext(),R.raw.die);
        deathSound[1]=MediaPlayer.create(getContext(),R.raw.die2);
        /*
        --------Game initialization----------
         */
    }

    @Override
    protected void onDraw(final Canvas canvas) { //draw views here
        super.onDraw(canvas);
        canvas.drawBitmap(background,null,rect,null);
        canvas.drawBitmap(leftArrow,0,dHeight-200,null);
        canvas.drawBitmap(rightArrow,dWidth-200,dHeight-200,null);
        canvas.drawBitmap(platforms[platformnum],(float)platformX,(float)platformY,null);
        canvas.drawLine((float)platformX+platforms[platformnum].getWidth()/2,0,(float)platformX+platforms[platformnum].getWidth()/2,dHeight,new Paint());
        canvas.drawLine((float)guyX+doodles[0].getWidth()/2,0,(float)guyX+doodles[0].getWidth()/2,dHeight,new Paint());
        handler.postDelayed(runnable,tick);
        sc.setColor(Color.BLACK);
        sc.setTextSize(50);
        canvas.drawText("Score: "+score,100,100,sc);
        canvas.drawText("Your best: "+maxScore,100,170,sc);
        boolean oneTime=false; //to hit platform only 1 time
        //-------draw movement up/down-------
        if (velocity>0)
            canvas.drawBitmap(doodles[1],(float)guyX,(float)guyY,null); //going down

        else
            canvas.drawBitmap(doodles[0],(float)guyX,(float)guyY,null); //going up
        //----------------------------------
        guyY+=velocity+gravity; //calculating guy vertical position
        guyMiddleX=(float)guyX+doodles[0].getWidth()/2; //guy middle
        platformMiddleX = (float)platformX+platforms[platformnum].getWidth()/2;

        if (guyY>=platformY-doodles[0].getHeight()&&guyX>=platformX-doodles[0].getWidth()&&
                guyY<platformY-doodles[0].getHeight()+60&&guyX<=platformX+platforms[platformnum].getWidth()&&oneTime==false) //if got to platform position and +60 for ondraw callsz
        {
            if (guyMiddleX>=platformMiddleX-20&&guyMiddleX<platformMiddleX+20)
            {
                middle.start();
                score+=100;
            }
            canvas.drawBitmap(doodles[1],(float)guyX,(float)guyY,null); //animation
            jump.start();
            velocity=0;
            velocity-=55; //jumping
            hardness+=0.05; //increasing hardness with each jump
            platformX=new Random().nextInt(dWidth-platforms[platformnum].getWidth()-platforms[platformnum].getWidth())+platforms[platformnum].getWidth(); //getting random next plat position
            //platformY=new Random().nextInt((int)(dHeight-300-guyY))+(guyY); //getting new y position (optional)
            oneTime=true;
            score+=10;
            platformnum=rand.nextInt(4); //next platform size will be randomized
            if (maxScore<score) { maxScore=score; sp.edit().putInt("maxScore",maxScore).commit(); //SAVE MAXSCORE ON DEVICE
            }
        }
        velocity= velocity + gravity+hardness;
        if (guyY>dHeight) //reset game here
        {
           deathSound[new Random().nextInt(2)].start(); //choose random death sound
            score=0;
            lose=true;

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { //for arrow keys
        if (event.getX()>0&&event.getX()< leftArrow.getWidth())
        {
            guyX-=dWidth/40;
            if (guyX<0) guyX=dWidth; //change size when going off wall
        }
        if (event.getX()>dWidth-200&&event.getX()< dWidth)
        {
            guyX+=dWidth/40;
            if (guyX>dWidth) guyX=0; //change size when going off wall
        }
        return true;
    }



}


