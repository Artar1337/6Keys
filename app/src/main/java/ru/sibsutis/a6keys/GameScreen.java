package ru.sibsutis.a6keys;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameScreen extends SurfaceView implements SurfaceHolder.Callback {

    final static private int BAD_ATTEMPT = -100;
    final static private int TIME_MULTIPLIER = 10;

    final private Bitmap background;
    final private Bitmap door;
    final private Bitmap bigDoor;
    final private Bitmap rotatedDoor;
    final private Bitmap father;
    final private Bitmap girl;
    final private Bitmap carpet;
    final private Bitmap[] keys = new Bitmap[6];
    final private Bitmap[] knobs = new Bitmap[6];
    final private int doorRadius;
    final private int[] doorY = new int[3];
    final private int[] keyX = new int[6];


    public static SoundPool sounds;
    public static int soundDialog, soundStep, soundFailed, soundPassed, soundTime, soundDoor;
    public static float soundVolume = 1.0f;
    public static float musicVolume = 1.0f;
    public static int userScore;

    private GameManager gameThread;
    static int currentIndex;

    private int W, H, doorX, keyY, bDoorX, bDoorY;
    //public static boolean[] taskCompleted = {false, false, false, false, false, false};
    public static boolean[] taskCompleted = {true, true, true, true, true, true};

    public boolean notIsInEndRoom = true;

    public GameActivity gameActivity;
    public Character character;
    public int bigDoorH;

    public static void changeScore(int baseValue,int attempts,int time){
        userScore+=TIME_MULTIPLIER*time;
        userScore+=BAD_ATTEMPT*attempts;
        userScore+=baseValue;
    }

    private Bitmap rotateBitmap(Bitmap src, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public GameScreen(Context context, GameActivity activity) {
        super(context);

        userScore=0;

        // Make Game Surface focusable so it can handle events.
        this.setFocusable(true);

        // Set callback.
        this.getHolder().addCallback(this);

        sounds = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
        soundDialog = sounds.load(context, R.raw.dial, 1);
        soundStep = sounds.load(context, R.raw.walk, 1);
        soundFailed = sounds.load(context, R.raw.attempt_end, 1);
        soundPassed = sounds.load(context, R.raw.passed, 1);
        soundDoor = sounds.load(context, R.raw.door, 1);
        soundTime = sounds.load(context, R.raw.time_end, 1);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.backgrtile_v2);
        door = BitmapFactory.decodeResource(getResources(), R.drawable.door);
        father = BitmapFactory.decodeResource(getResources(), R.drawable.father);
        girl = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
        carpet = BitmapFactory.decodeResource(getResources(), R.drawable.carpet);
        bigDoor = BitmapFactory.decodeResource(getResources(), R.drawable.super_door);
        rotatedDoor = rotateBitmap(door, 180);
        doorRadius = door.getHeight();
        gameActivity = activity;
        Bitmap key = BitmapFactory.decodeResource(getResources(), R.drawable.key);
        Bitmap knob = BitmapFactory.decodeResource(getResources(), R.drawable.knob);
        Bitmap rotatedKnob = rotateBitmap(knob, 180);
        keys[0] = tintImage(key, Color.RED);
        keys[1] = tintImage(key, Color.YELLOW);
        keys[2] = tintImage(key, Color.GREEN);
        keys[3] = tintImage(key, Color.CYAN);
        keys[4] = tintImage(key, Color.BLUE);
        keys[5] = tintImage(key, Color.MAGENTA);
        knobs[0] = tintImage(knob, Color.RED);
        knobs[1] = tintImage(rotatedKnob, Color.YELLOW);
        knobs[2] = tintImage(knob, Color.GREEN);
        knobs[3] = tintImage(rotatedKnob, Color.CYAN);
        knobs[4] = tintImage(knob, Color.BLUE);
        knobs[5] = tintImage(rotatedKnob, Color.MAGENTA);

    }

    public void update() {
        if (notIsInEndRoom) this.character.update();
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

    public boolean closeToDoor(int x, int y, int destX, int destY) {
        return (Math.abs(x - destX) < doorRadius
                && Math.abs(y - destY) < doorRadius);
    }

    public boolean allCompleted() {
        for (int i = 0; i < 6; i++) {
            if (!taskCompleted[i])
                return false;
        }
        return true;
    }

    public int closeToAnyDoor() {
        int x = this.character.x + this.character.headCenterX;
        int y = this.character.y + this.character.headCenterY;

        if (closeToDoor(x, y, 0, doorY[0]) && !taskCompleted[0])
            return 1;
        else if (closeToDoor(x, y, doorX, doorY[0]) && !taskCompleted[1])
            return 2;
        else if (closeToDoor(x, y, 0, doorY[1]) && !taskCompleted[2])
            return 3;
        else if (closeToDoor(x, y, doorX, doorY[1]) && !taskCompleted[3])
            return 4;
        else if (closeToDoor(x, y, 0, doorY[2]) && !taskCompleted[4])
            return 5;
        else if (closeToDoor(x, y, doorX, doorY[2]) && !taskCompleted[5])
            return 6;
        else if (closeToDoor(x, y, bDoorX + door.getHeight(), bDoorY) && allCompleted())
            return 7;

        return 0;
    }

    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);


        //сначала рисуем фон
        canvas.drawBitmap(background, 0, 0, null);
        if (notIsInEndRoom) {
            //потом рисуем двери
            canvas.drawBitmap(door, 0, doorY[0], null);
            canvas.drawBitmap(knobs[0], 0, doorY[0], null);
            canvas.drawBitmap(door, 0, doorY[1], null);
            canvas.drawBitmap(knobs[2], 0, doorY[1], null);
            canvas.drawBitmap(door, 0, doorY[2], null);
            canvas.drawBitmap(knobs[4], 0, doorY[2], null);

            canvas.drawBitmap(rotatedDoor, doorX, doorY[0], null);
            canvas.drawBitmap(knobs[1], doorX, doorY[0], null);
            canvas.drawBitmap(rotatedDoor, doorX, doorY[1], null);
            canvas.drawBitmap(knobs[3], doorX, doorY[1], null);
            canvas.drawBitmap(rotatedDoor, doorX, doorY[2], null);
            canvas.drawBitmap(knobs[5], doorX, doorY[2], null);

            canvas.drawBitmap(bigDoor, bDoorX, bDoorY, null);


            //потом рисуем персонажа
            this.character.draw(canvas);
            //потом рисуем собранные ключи
            for (int i = 0; i < 6; i++) {
                if (taskCompleted[i])
                    canvas.drawBitmap(keys[i], keyX[i], keyY, null);
            }
        } else {
            canvas.drawBitmap(carpet, 0, 0, null);
            canvas.drawBitmap(carpet, carpet.getWidth(), 0, null);
            canvas.drawBitmap(father, carpet.getWidth() / 2.0f,
                    carpet.getHeight() / 2.0f, null);
            canvas.drawBitmap(girl, 3 * carpet.getWidth() / 2.0f,
                    carpet.getHeight() / 2.0f, null);
            canvas.drawBitmap(character.getCharactersBack(), carpet.getWidth(),
                    H / 2.0f, null);
            gameActivity.gotoDoorButton.setEnabled(false);
            gameActivity.gotoDoorButton.setAlpha(0.0f);
        }

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap chBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprite);
        this.gameThread = new GameManager(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
        W = getWidth();
        H = getHeight();
        doorX = W - rotatedDoor.getWidth();
        doorY[0] = 10;
        doorY[1] = H / 3 + 10;
        doorY[2] = 2 * H / 3 + 10;
        bDoorX = W / 2 - bigDoor.getWidth() / 2;
        bDoorY = H - bigDoor.getHeight();
        keyY = H - keys[0].getHeight() - 10;
        keyX[0] = 10;
        bigDoorH = bigDoor.getHeight();
        for (int i = 1; i < 6; i++)
            keyX[i] = keyX[i - 1] + keys[i].getWidth() + 12;

        this.character = new Character(this, chBitmap, W / 3, 50);
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
                retry = false;
                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startNewSpeech(final String[] sentences, final int[] who) {

        //по 50 мс на букву и 2 мин на общее чтение
        final int TIME_LIMIT = sentences[currentIndex].length() * 50 + 2000;
        String currentName = "";

        if (who[currentIndex] == 1) {
            gameActivity.finalDialog.setTextColor(Color.BLACK);
            currentName = gameActivity.getString(R.string.father);
        } else if (who[currentIndex] == 2) {
            gameActivity.finalDialog.setTextColor(Color.MAGENTA);
            currentName = gameActivity.getString(R.string.girl);
        } else if (who[currentIndex] == 3) {
            gameActivity.finalDialog.setTextColor(Color.BLUE);
            currentName = gameActivity.getString(R.string.you);
        }

        final int startIndex = currentName.length();


        new CountDownTimer(TIME_LIMIT, 50) {

            int index = 0;

            public void onTick(long msUntilFinished) {
                if (index <= sentences[currentIndex].length()) {
                    gameActivity.finalDialog.setText
                            (sentences[currentIndex].substring(0, index));
                    sounds.play(soundDialog, soundVolume, soundVolume, 0, 0, 1.5f);
                    index++;
                }

            }

            @Override
            public void onFinish() {
                currentIndex++;
                if (currentIndex < who.length) {
                    startNewSpeech(sentences, who);
                }
                else{
                    gameActivity.finalDialog.setAlpha(0.0f);
                    gameActivity.showFinalDialog();
                }
            }
        }.start();
    }

    public void startDialogEnding() {
        String[] sentences = new String[11];
        String father = gameActivity.getString(R.string.father) + " ";
        String girl = gameActivity.getString(R.string.girl) + " ";
        String you = gameActivity.getString(R.string.you) + " ";
        //1 - отец, 2 - дочь, 3 - вы
        int who[] = {1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 3};
        sentences[0] = father + gameActivity.getString(R.string.ending_part1);
        sentences[1] = father + gameActivity.getString(R.string.ending_part2);
        sentences[2] = father + gameActivity.getString(R.string.ending_part3);
        sentences[3] = father + gameActivity.getString(R.string.ending_part4);
        sentences[4] = girl + gameActivity.getString(R.string.ending_part4_g);
        sentences[5] = father + gameActivity.getString(R.string.ending_part5);
        sentences[6] = father + gameActivity.getString(R.string.ending_part6);
        sentences[7] = father + gameActivity.getString(R.string.ending_part7);
        sentences[8] = father + gameActivity.getString(R.string.ending_part8);
        sentences[9] = father + gameActivity.getString(R.string.ending_part9);
        sentences[10] = you + gameActivity.getString(R.string.ending_part9_u);
        currentIndex = 0;

        new CountDownTimer(2000, 2000) {
            public void onTick(long msUntilFinished) {
            }

            @Override
            public void onFinish() {
                gameActivity.finalDialog.setEnabled(true);
                gameActivity.finalDialog.setAlpha(1.0f);
                startNewSpeech(sentences, who);
            }
        }.start();


    }

}
