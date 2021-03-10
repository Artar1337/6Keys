package ru.sibsutis.a6keys;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PicActivity extends Activity {

    final private long TIME_LIMIT = 60000;
    private boolean taskCompleted = false;

    //10 вопросов в формате вопрос\n ответ 1_ответ 2\n комментарий
    private String[] qArray;
    private String[] ansArray;
    private String comment;
    private EditText ansEdit;
    private TextView head;
    private long lastTime;

    private Bitmap loadQBitmap(int index) {
        switch (index) {
            case 0:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
            case 1:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic2);
            case 2:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic3);
            case 3:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic4);
            case 4:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic5);
            case 5:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic6);
            case 6:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic7);
            case 7:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic8);
            case 8:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic9);
            case 9:
                return BitmapFactory.decodeResource(getResources(), R.drawable.pic10);
            default:
                return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_picture);
        taskCompleted = false;
        qArray = LogicActivity.readQuestions(R.raw.t4, this).split("\n");
        TextView qView = findViewById(R.id.PicTask);
        Button aButton = findViewById(R.id.PicButton);
        TextView time = findViewById(R.id.PicTime);
        head = findViewById(R.id.PicHeader);
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskCompleted)
                    return;
                if (ansEdit.getText().length() == 0)
                    return;
                String answer = ansEdit.getText().toString().toLowerCase();
                boolean found = false;

                for (int i = 0; i < ansArray.length; i++) {
                    Log.wtf("answers:", ansArray[i]);
                    if (answer.equals(ansArray[i])) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    head.setText(getString(R.string.correct));
                    head.setTextColor(Color.GREEN);
                    taskCompleted = true;
                    qView.setText(comment);
                    CardActivity.showDialog(true, false,
                            lastTime, 0, v.getContext());
                    ru.sibsutis.a6keys.GameScreen.taskCompleted[3] = true;
                    GameScreen.changeScore(1000, 0, (int) lastTime / 1000);
                } else {
                    head.setText(getString(R.string.incorrect));
                    head.setTextColor(Color.RED);

                    taskCompleted = true;
                    CardActivity.showDialog(false, true,
                            lastTime, 1, v.getContext());
                }
            }
        });

        ansEdit = findViewById(R.id.PicEditText);
        int index = MathActivity.getRandomNumber(0, 10);
        ImageView img = findViewById(R.id.PicImage);
        img.setImageBitmap(loadQBitmap(index));
        qView.setText(qArray[3 * index]);
        ansArray = qArray[3 * index + 1].split("_");
        ansArray[ansArray.length - 1] = ansArray[ansArray.length - 1].
                substring(0, ansArray[ansArray.length - 1].length() - 1);
        comment = qArray[3 * index + 2];

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
                    CardActivity.showDialog(false, false
                            , lastTime, 0, qView.getContext());
                }
            }
        }.start();

    }
}
