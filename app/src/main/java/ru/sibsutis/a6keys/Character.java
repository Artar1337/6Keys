package ru.sibsutis.a6keys;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;

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

    private int movingVectorX = 0;
    private int movingVectorY = 0;

    private long lastDrawNanoTime = -1;

    private GameScreen gameSurface;

    private Bitmap image;

    private final int rowCount = 4;
    private final int colCount = 3;

    //ширина и высота куска спрайта, который режем
    private final int width;
    private final int height;

    //нужны для того, чтобы персоваж шел примерно к центру головы,
    //а не к левому верхнему углу
    public final int headCenterX;
    public final int headCenterY;

    public int destX;
    public int destY;
    public int x;
    public int y;
    public final int TapRadius;
    public int doorNumber = 0;

    public Bitmap getCharactersBack() {
        return bottomToTops[1];
    }


    public Character(GameScreen gameSurface, Bitmap image, int x, int y) {

        this.image = image;
        this.x = x;
        this.y = y;

        width = image.getWidth() / colCount;
        height = image.getHeight() / rowCount;

        headCenterX = width / 2;
        headCenterY = height / 3;
        TapRadius = Math.max(headCenterX, headCenterY);

        destX = x + headCenterX;
        destY = y + headCenterY;

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
        Bitmap subImage = Bitmap.createBitmap
                (image, col * width, row * height, width, height);
        return subImage;
    }

    public Bitmap getCurrentMoveBitmap() {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }

    public boolean shouldNotRun() {
        return (Math.abs(x - destX + headCenterX) < TapRadius
                && Math.abs(y - destY + headCenterY) < TapRadius);
    }

    public void spriteIndexUpdate() {
        // устанавливаем следующий номер спрайта по столбцу
        this.colUsing++;
        if (colUsing >= this.colCount) {
            this.colUsing = 0;
        }
        // устанавливаем строку спрайтов в зависимости от направления движения
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

    public void update() {

        //если стоим на месте - ставим спрайт
        //стоящего лицом вперед парня
        //проверяем, находится ли герой около двери

        Button doorButton = gameSurface.gameActivity.gotoDoorButton;
        doorNumber = 0;

        if (shouldNotRun()) {
            this.colUsing = 1;
            this.rowUsing = ROW_TOP_TO_BOTTOM;

            doorNumber = this.gameSurface.closeToAnyDoor();
            doorButton.setBackgroundColor(Color.WHITE);
            doorButton.setTextColor(Color.BLACK);
            if (doorNumber != 0) {
                doorButton.setAlpha(1.0f);
                doorButton.setEnabled(true);
                doorButton.setClickable(true);
            } else {
                doorButton.setAlpha(0.0f);
                doorButton.setEnabled(false);
                doorButton.setClickable(false);
            }

            return;
        }

        doorButton.setAlpha(0.0f);
        doorButton.setEnabled(false);

        long now = System.nanoTime();

        // Первый раз рисуем спрайт
        if (lastDrawNanoTime == -1) {
            lastDrawNanoTime = now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime) / 1000000);

        float distance = VELOCITY * deltaTime;

        double movingVectorLength = Math.sqrt
                (movingVectorX * movingVectorX + movingVectorY * movingVectorY);
        if (Double.compare(movingVectorLength, 0.0) == 0) {
            return;
        }

        this.x = x + (int) (distance * movingVectorX / movingVectorLength);
        this.y = y + (int) (distance * movingVectorY / movingVectorLength);
        int heightPlusDoor = height + gameSurface.bigDoorH;

        //если вышли за границы экрана, то откатываем изменения x и y
        if ((this.x + this.headCenterX < -40) || (this.x > this.gameSurface.getWidth() - width) ||
                (this.y + this.headCenterY < -40) || (this.y > this.gameSurface.getHeight() - heightPlusDoor)) {
            this.x = x - (int) (distance * movingVectorX / movingVectorLength);
            this.y = y - (int) (distance * movingVectorY / movingVectorLength);
        }

        spriteIndexUpdate();
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap, x, y, null);

        // Записываем последнее время отрисовки
        this.lastDrawNanoTime = System.nanoTime();
        if (!shouldNotRun()) {
            gameSurface.sounds.play(gameSurface.soundStep, gameSurface.soundVolume
                    , gameSurface.soundVolume, 0, 0, 1.5f);
        }
    }

    public void setMovingVector(int movingVectorX, int movingVectorY) {
        this.movingVectorX = movingVectorX;
        this.movingVectorY = movingVectorY;
    }

    public void setDestination(int X, int Y) {
        this.destX = X;
        this.destY = Y;
    }
}
