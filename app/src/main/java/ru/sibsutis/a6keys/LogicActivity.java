package ru.sibsutis.a6keys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class LogicActivity extends Activity {

    final private int MAX_ATTEMPTS = 3;
    final private long TIME_LIMIT = 60000;
    private boolean taskCompleted = false;

    //10 вопросов в формате вопрос\n ответ 1_ответ 2\n комментарий
    private String[] qArray;
    private String[] ansArray;
    private String comment;
    private int attempts = 0;
    private EditText ansEdit;
    private TextView head;
    private long lastTime;

    public static String readQuestions(int ID, Context context) {

        StringBuilder string = new StringBuilder();

        try {
            InputStream is = context.getResources().openRawResource(ID);
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            int ch = ' ';
            while (ch != -1) {
                string.append((char) ch);
                ch = reader.read();
            }

            is.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return string.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_logical);

        attempts = 0;
        taskCompleted = false;
        qArray = readQuestions(R.raw.t3, this).split("\n");
        TextView qView = findViewById(R.id.LogicTask);
        Button aButton = findViewById(R.id.LogicButton);
        TextView time = findViewById(R.id.LogicTime);
        head = findViewById(R.id.LogicHeader);
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
                            lastTime, attempts, v.getContext());
                    ru.sibsutis.a6keys.GameScreen.taskCompleted[2] = true;
                    GameScreen.changeScore(1000, attempts, (int) lastTime / 1000);
                } else {
                    head.setText(getString(R.string.incorrect));
                    head.setTextColor(Color.RED);
                    attempts++;
                    if (attempts > MAX_ATTEMPTS) {
                        taskCompleted = true;
                        CardActivity.showDialog(false, true,
                                lastTime, attempts, v.getContext());
                    }
                }
            }
        });

        ansEdit = findViewById(R.id.LogicEditText);
        int index = MathActivity.getRandomNumber(0, 10);
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
                            , lastTime, attempts, qView.getContext());
                }
            }
        }.start();

    }
}
