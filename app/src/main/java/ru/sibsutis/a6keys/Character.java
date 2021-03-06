package ru.sibsutis.a6keys;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class Character {

    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;

    // Используемые индексы изображения для спрайта
    private int rowUsing = ROW_LEFT_TO_RIGHT;
    private int colUsing;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    // Скорость (pixel/millisecond)
    public static final float VELOCITY = 0.15f;

    private int movingVectorX = 10;
    private int movingVectorY = 10;

    private long lastDrawNanoTime = -1;

    private GameScreen gameSurface;

    private Bitmap image;

    private final int rowCount = 4;
    private final int colCount = 3;

    //ширина и высота куска спрайта, который режем
    private final int width;
    private final int height;

    public int destX;
    public int destY;
    public int x;
    public int y;
    public final int TapRadius=100;


    public Character(GameScreen gameSurface, Bitmap image, int x, int y) {

        this.image = image;
        this.x = x;
        this.y = y;
        destX=x;
        destY=y;
        this.width = image.getWidth() / colCount;
        this.height = image.getHeight() / rowCount;

        this.gameSurface = gameSurface;

        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3

        for (int col = 0; col < this.colCount; col++) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col] = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col] = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }
    }

    public Bitmap[] getMoveBitmaps() {
        switch (rowUsing) {
            case ROW_BOTTOM_TO_TOP:
                return this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    public Bitmap createSubImageAt(int row, int col) {
        // createBitmap(bitmap, x, y, width, height).
        Bitmap subImage = Bitmap.createBitmap(image, col * width, row * height, width, height);
        return subImage;
    }

    public Bitmap getCurrentMoveBitmap() {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }


    public boolean shouldRun()
    {
        Log.wtf("niggers",Math.abs(x-destX)+","+Math.abs(y-destY));
        return (Math.abs(x-destX)<TapRadius
                && Math.abs(y-destY)<TapRadius);
    }

    public void update() {

        if(shouldRun())
        {
            this.colUsing=1;
            this.rowUsing=ROW_TOP_TO_BOTTOM;
            return;
        }
        Log.wtf("what1","is this");
        long now = System.nanoTime();

        // Первый раз рисуем спрайт
        if (lastDrawNanoTime == -1) {
            lastDrawNanoTime = now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime) / 1000000);

        float distance = VELOCITY * deltaTime;

        double movingVectorLength = Math.sqrt(movingVectorX * movingVectorX + movingVectorY * movingVectorY);
        if(Double.compare(movingVectorLength,0.0)==0){
            return;
        }
        Log.wtf("what2","is this");


        this.x = x + (int) (distance * movingVectorX / movingVectorLength);
        this.y = y + (int) (distance * movingVectorY / movingVectorLength);

        if (this.x < 0) {
            this.x = 0;
            this.movingVectorX = -this.movingVectorX;
        } else if (this.x > this.gameSurface.getWidth() - width) {
            this.x = this.gameSurface.getWidth() - width;
            this.movingVectorX = -this.movingVectorX;
        }

        if (this.y < 0) {
            this.y = 0;
            this.movingVectorY = -this.movingVectorY;
        } else if (this.y > this.gameSurface.getHeight() - height) {
            this.y = this.gameSurface.getHeight() - height;
            this.movingVectorY = -this.movingVectorY;
        }

        // устанавливаем колонку спрайтов в зависимости от направления движения
        this.colUsing++;
        if (colUsing >= this.colCount) {
            this.colUsing = 0;
        }

        if (movingVectorX > 0) {
            if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            } else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            } else {
                this.rowUsing = ROW_LEFT_TO_RIGHT;
            }
        } else {
            if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            } else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            } else {
                this.rowUsing = ROW_RIGHT_TO_LEFT;
            }
        }
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap, x, y, null);
        // Записываем последнее время отрисовки
        this.lastDrawNanoTime = System.nanoTime();
        Log.wtf("what",""+x+","+y+";"+destX+","+destY);
    }

    public void setMovingVector(int movingVectorX, int movingVectorY) {
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
    }

    public void setDestination(int X,int Y)
    {
        destX=X;
        destY=Y;
    }
}
