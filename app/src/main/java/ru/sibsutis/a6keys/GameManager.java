package ru.sibsutis.a6keys;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameManager extends Thread {

    private boolean running;
    private GameScreen gameSurface;
    private SurfaceHolder surfaceHolder;

    public GameManager(GameScreen gameSurface, SurfaceHolder surfaceHolder) {
        this.gameSurface = gameSurface;
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run() {

        long startTime = System.nanoTime();

        while (running) {
            Canvas canvas = null;
            try {
                // Get Canvas from Holder and lock it.
                canvas = this.surfaceHolder.lockCanvas();

                // Synchronized
                synchronized (canvas) {
                    this.gameSurface.update();
                    this.gameSurface.draw(canvas);
                }
            } catch (Exception e) {
                // Do nothing.
            } finally {
                if (canvas != null) {
                    // Unlock Canvas.
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            long now = System.nanoTime();
            // Interval to redraw game
            // (Change nanoseconds to milliseconds)
            long waitTime = (now - startTime) / 1000000;
            if (waitTime < 100) {
                waitTime = 100; // Millisecond.
            }
            try {
                // Sleep.
                this.sleep(waitTime);
            } catch (InterruptedException e) {

            }
            startTime = System.nanoTime();
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
