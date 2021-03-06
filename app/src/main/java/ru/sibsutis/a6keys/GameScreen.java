package ru.sibsutis.a6keys;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameScreen extends SurfaceView implements SurfaceHolder.Callback {

    private GameManager gameThread;

    private Character character;

    public GameScreen(Context context)  {
        super(context);

        // Make Game Surface focusable so it can handle events.
        this.setFocusable(true);

        // Set callback.
        this.getHolder().addCallback(this);
    }

    public void update()  {
        this.character.update();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x=  (int)event.getX();
            int y = (int)event.getY();

            int movingVectorX =x-  this.character.x ;
            int movingVectorY =y-  this.character.y ;

            this.character.setMovingVector(movingVectorX,movingVectorY);
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);

        this.character.draw(canvas);
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap chBitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.sprite);
        this.character = new Character(this,chBitmap,100,50);
        this.gameThread = new GameManager(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry= true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= true;
        }
    }

}
