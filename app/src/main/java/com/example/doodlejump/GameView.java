package com.example.doodlejump;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    Bitmap platform;
    double platformX,platformY;
    public GameView(Context context)
    {
        super(context);
        handler=new Handler();
        runnable=new Runnable() {

            @Override
            public void run() {
                invalidate(); //calls onDraw
            }
        };
        background= BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        leftArrow=BitmapFactory.decodeResource(getResources(),R.drawable.leftarrow);
        rightArrow=BitmapFactory.decodeResource(getResources(),R.drawable.rightarrow);
        platform=BitmapFactory.decodeResource(getResources(),R.drawable.platform);
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        dWidth=point.x;
        dHeight=point.y;
        rect= new Rect(0,0,dWidth,dHeight);
        doodles=new Bitmap[2];
        doodles[0] =  BitmapFactory.decodeResource(getResources(),R.drawable.guy);
        doodles[1] =  BitmapFactory.decodeResource(getResources(),R.drawable.guy2); //for another image, CHANGE LATER FOR ANIMATION
        guyX=dWidth/2;
        guyY=dHeight/2;
        platformX=dWidth/2;
        platformY=dHeight-300;
    }

    @Override
    protected void onDraw(final Canvas canvas) { //draw views here
        super.onDraw(canvas);

        canvas.drawBitmap(background,null,rect,null);
        canvas.drawBitmap(leftArrow,0,dHeight-200,null);
        canvas.drawBitmap(rightArrow,dWidth-200,dHeight-200,null);
        canvas.drawBitmap(platform,(float)platformX,(float)platformY,null);
        handler.postDelayed(runnable,tick);
        canvas.drawBitmap(doodles[0],(float)guyX,(float)guyY,null); // animation.....
       // canvas.drawBitmap(doodles[0],(float)guyX,(float)guyY,null);
        guyY+=velocity+gravity;
        Log.d("Info:","GuyY: " + guyY + "Platform Y: "+(platformY-doodles[0].getHeight()));
        boolean oneTime=false;
        if (guyY>=platformY-doodles[0].getHeight()&&guyX>=platformX-doodles[0].getWidth()&&guyX<=platformX+platform.getWidth()&&oneTime==false)
        {
            canvas.drawBitmap(doodles[1],(float)guyX,(float)guyY,null);
            MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.jump);
            mp.start();
            velocity=0;
            velocity-=50;
            platformX=new Random().nextInt(dWidth);
          //  platformY=new Random().nextInt(dHeight/2)+dHeight/2-200;
            oneTime=true;
        }
        velocity= velocity + gravity;
        if (guyY>dHeight) //reset game here
        {
            final Paint p = new Paint();
            p.setColor(Color.BLACK);
            p.setTextSize(50);
            canvas.drawText("You lost",dWidth/2,dHeight/2,p);
            Bitmap reset = BitmapFactory.decodeResource(getResources(),R.drawable.reset);
            canvas.drawBitmap(reset,dWidth/2,dHeight/2,null);
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


