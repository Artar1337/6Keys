package ru.sibsutis.a6keys;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameScreen extends SurfaceView implements SurfaceHolder.Callback {

    final private Bitmap background;
    final private Bitmap door;
    final private Bitmap rotatedDoor;

    private Character character;
    private GameManager gameThread;
    private int W, H, doorX;

    private Bitmap rotateBitmap(Bitmap src, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public GameScreen(Context context) {
        super(context);

        // Make Game Surface focusable so it can handle events.
        this.setFocusable(true);

        // Set callback.
        this.getHolder().addCallback(this);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.backgrtile_v2);
        door = BitmapFactory.decodeResource(getResources(), R.drawable.door);
        rotatedDoor = rotateBitmap(door, 180);
    }

    public void update() {
        this.character.update();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            int movingVectorX = x - this.character.x;
            int movingVectorY = y - this.character.y;

            this.character.setMovingVector(movingVectorX, movingVectorY);
            this.character.setDestination(x, y);
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        //сначала рисуем фон
        canvas.drawBitmap(background, 0, 0, null);
        //потом рисуем двери
        canvas.drawBitmap(door, 0, 10, null);
        canvas.drawBitmap(door, 0, H / 3.0f + 10, null);
        canvas.drawBitmap(door, 0, 2.0f * H / 3.0f + 10, null);

        canvas.drawBitmap(rotatedDoor, doorX, 10, null);
        canvas.drawBitmap(rotatedDoor, doorX, H / 3.0f + 10, null);
        canvas.drawBitmap(rotatedDoor, doorX, 2.0f * H / 3.0f + 10, null);

        this.character.draw(canvas);
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap chBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprite);
        this.character = new Character(this, chBitmap, 100, 50);
        this.gameThread = new GameManager(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
        W = getWidth();
        H = getHeight();
        doorX = W - rotatedDoor.getWidth();
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = true;
        }
    }

}