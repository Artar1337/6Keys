package ru.sibsutis.a6keys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static ru.sibsutis.a6keys.GameScreen.soundFailed;
import static ru.sibsutis.a6keys.GameScreen.soundPassed;
import static ru.sibsutis.a6keys.GameScreen.soundTime;
import static ru.sibsutis.a6keys.GameScreen.soundVolume;
import static ru.sibsutis.a6keys.GameScreen.sounds;

public class EstimateActivity extends Activity {

    final private long TIME_LIMIT = 45000;
    final private float PERCENT_LIMIT = 85;

    private Bitmap ball;
    private Bitmap pipe;
    private Bitmap pipeTop;
    private boolean taskCompleted = false;
    //текущий процент попадания "в яблочко"
    private float userPercent = 100;
    private long lastTime;
    private int stage = 1;
    //количество шаров для 2 задания
    private int ballsCount = 0;
    //истинный процент для 1 задания
    private int percentage;
    //истинный угол для 3 задания
    private int angle;

    private TextView header;
    private EditText answer;

    public void showDialog(boolean won, boolean mistaken, long lastTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int soundID;
        if (!won) {
            builder.setTitle(getString(R.string.gameOver))
                    .setIcon(R.drawable.wrong)
                    .setMessage(getString(R.string.timeIsUp))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            if (mistaken) {
                builder.setMessage(getString(R.string.tooLowPercentage));
                soundID=soundFailed;
            }
            else
            {
                soundID=soundTime;
            }
        } else {
            builder.setTitle(getString(R.string.gameOver))
                    .setIcon(R.drawable.correct)
                    .setMessage(getString(R.string.taskPassed) + "\n"
                            + getString(R.string.time) + (lastTime / 1000) + "\n"
                            + getString(R.string.guessPercentage) + (int) userPercent)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            soundID=soundPassed;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        sounds.play(soundID,soundVolume,soundVolume,0,0,1.5f);
    }

    private void taskUpdate() {
        if (stage < 1 || stage > 3) {
            return;
        }

        TableLayout table = findViewById(R.id.EstLayout);
        table.removeAllViewsInLayout();
        table.setShrinkAllColumns(true);
        table.setStretchAllColumns(true);

        if (stage == 1) {
            percentage = MathActivity.getRandomNumber(5, 96);

            Bitmap water = Bitmap.createBitmap(pipe.getWidth(),
                    pipe.getHeight(), Bitmap.Config.ARGB_8888);
            water.eraseColor(android.graphics.Color.GRAY);

            Canvas canvas = new Canvas(water);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(android.graphics.Color.BLUE);

            float scaledPercentage = ((float) pipe.getHeight() - 2.0f * pipeTop.getHeight()) / 100.0f;
            scaledPercentage *= percentage;

            canvas.drawRect(0, pipeTop.getHeight() + scaledPercentage, pipe.getWidth()
                    , pipe.getHeight() - pipeTop.getHeight(), paint);

            ImageView view = new ImageView(this);
            view.setImageBitmap(water);
            view.draw(canvas);

            ImageView pipeView = new ImageView(this);
            pipeView.setImageBitmap(pipe);

            FrameLayout layout = new FrameLayout(this);

            layout.addView(view);
            layout.addView(pipeView);
            table.addView(layout);
            header.setText(getString(R.string.whatPercent));
            percentage = 100 - percentage;
            Log.wtf("percent", "is " + percentage);
        } else if (stage == 2) {
            int rows = 10;
            int count = 0;
            for (int i = 0; i < rows; i++) {
                TableRow row = new TableRow(this);

                int cols = MathActivity.getRandomNumber(2, 11);

                for (int j = 0; j < cols; j++) {
                    ImageView view = new ImageView(this);
                    view.setImageBitmap(ball);
                    count++;
                    row.addView(view);
                }
                table.addView(row);
            }
            ballsCount = count;
            header.setText(getString(R.string.howManyBalls));
            Log.wtf("balls count", "is " + ballsCount);
        } else {
            angle = MathActivity.getRandomNumber(15, 176);
            //берем тот же размер, что и у pipe, для удобства
            Bitmap angleImage = Bitmap.createBitmap(pipe.getWidth(),
                    pipe.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(angleImage);
            Paint paint = new Paint();
            paint.setStrokeWidth(5.0f);
            paint.setColor(android.graphics.Color.BLUE);

            float lineY = angleImage.getHeight() / 2.0f + pipeTop.getHeight();

            canvas.drawLine(angleImage.getWidth() / 2.0f, lineY,
                    angleImage.getWidth(), lineY, paint);

            double angleR = angle * Math.PI / 180;
            float endX = angleImage.getWidth() / 2.0f + angleImage.getWidth() / 2.0f * (float) Math.cos(angleR);
            float endY = lineY - angleImage.getWidth() / 2.0f * (float) Math.sin(angleR);

            canvas.drawLine(angleImage.getWidth() / 2.0f, lineY,
                    endX, endY, paint);

            ImageView view = new ImageView(this);
            view.setImageBitmap(angleImage);
            view.draw(canvas);
            table.addView(view);
            header.setText(getString(R.string.whatAngle));
            Log.wtf("angle", "is " + angle);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_estimate);

        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        pipe = BitmapFactory.decodeResource(getResources(), R.drawable.pipe);
        pipeTop = BitmapFactory.decodeResource(getResources(), R.drawable.pipe_top);

        TextView time = findViewById(R.id.EstTime);
        header = findViewById(R.id.EstHeader);
        answer = findViewById(R.id.EstEditText);
        Button bAnswer = findViewById(R.id.EstButton);
        bAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer.getText().length() < 1 || taskCompleted)
                    return;
                int ans = Integer.parseInt(answer.getText().toString());
                if (stage == 1) {
                    userPercent = 100.0f - (Math.abs(percentage - ans) * 100.0f) / percentage;
                    if (userPercent < PERCENT_LIMIT) {
                        showDialog(false, true, lastTime);
                        taskCompleted = true;
                        return;
                    }
                    stage++;
                    taskUpdate();
                } else if (stage == 2) {
                    float tempPercent = 100.0f - (Math.abs(ballsCount - ans) * 100.0f) / ballsCount;
                    userPercent = (userPercent + tempPercent) / 2.0f;
                    if (userPercent < PERCENT_LIMIT) {
                        showDialog(false, true, lastTime);
                        taskCompleted = true;
                        return;
                    }
                    stage++;
                    taskUpdate();
                } else if (stage == 3) {
                    float tempPercent;
                    tempPercent = 100.0f - (Math.abs(angle - ans) * 100.0f) / angle;
                    userPercent = (userPercent + tempPercent) / 2.0f;
                    taskCompleted = true;
                    if (userPercent < PERCENT_LIMIT) {
                        showDialog(false, true, lastTime);
                        return;
                    }
                    showDialog(true, false, lastTime);
                    ru.sibsutis.a6keys.GameScreen.taskCompleted[1]=true;
                    GameScreen.changeScore(3000,(int)(100.0f-userPercent),(int)lastTime/1000);
                }
                answer.setText("");
            }
        });

        taskUpdate();

        //Главный игровой таймер
        new CountDownTimer(TIME_LIMIT, 1000) {

            public void onTick(long msUntilFinished) {
                //запоминаем оставшееся время для формирования
                //количества очков
                if (!taskCompleted) {
                    lastTime = msUntilFinished;
                    time.setText(String.valueOf(msUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (!taskCompleted) {
                    showDialog(false, false, lastTime);
                    taskCompleted = true;
                }
            }
        }.start();

    }
}
