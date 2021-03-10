package ru.sibsutis.a6keys;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

public class QuestionsActivity extends Activity implements View.OnClickListener {

    final private int MAX_ATTEMPTS = 3;
    final private long TIME_LIMIT = 75000;
    private boolean taskCompleted = false;

    //30 вопросов в формате вопрос\n ответ 1_ответ 2\n номер правильного ответа (1..4)
    private String[] qArray;
    private String[] ansArray;
    private int correctAnswer;
    private int[] used = {-1, -1, -1};
    private TextView head;
    private TextView qView;
    private long lastTime;

    private RadioButton rb1, rb2, rb3, rb4;

    public void setTask() {

        int index;
        if (used[0] < 0) {
            index = MathActivity.getRandomNumber(0, 30);
            used[0] = index;
        } else if (used[1] < 0) {
            index = MathActivity.getRandomNumber(0, 30);
            while (index == used[0]) {
                index = MathActivity.getRandomNumber(0, 30);
            }
            used[1] = index;
        } else if (used[2] < 0) {
            index = MathActivity.getRandomNumber(0, 30);
            while (index == used[0] || index == used[1]) {
                index = MathActivity.getRandomNumber(0, 30);
            }
            used[2] = index;
        } else index = 0;

        qView.setText(qArray[3 * index]);
        ansArray = qArray[3 * index + 1].split("_");
        ansArray[ansArray.length - 1] = ansArray[ansArray.length - 1].
                substring(0, ansArray[ansArray.length - 1].length() - 1);
        qArray[3 * index + 2] = qArray[3 * index + 2].substring(0, 1);
        correctAnswer = Integer.parseInt(qArray[3 * index + 2]) - 1;
        rb1.setChecked(false);
        rb2.setChecked(false);
        rb3.setChecked(false);
        rb4.setChecked(false);
        rb1.setText(ansArray[0]);
        rb2.setText(ansArray[1]);
        rb3.setText(ansArray[2]);
        rb4.setText(ansArray[3]);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_questions);

        taskCompleted = false;
        qArray = LogicActivity.readQuestions(R.raw.t6, this).split("\n");
        qView = findViewById(R.id.QuestionsTask);
        TextView time = findViewById(R.id.QuestionsTime);
        head = findViewById(R.id.QuestionsHeader);
        rb1 = findViewById(R.id.radio_1);
        rb1.setTag("0");
        rb2 = findViewById(R.id.radio_2);
        rb2.setTag("1");
        rb3 = findViewById(R.id.radio_3);
        rb3.setTag("2");
        rb4 = findViewById(R.id.radio_4);
        rb4.setTag("3");
        rb1.setOnClickListener(this::onClick);
        rb2.setOnClickListener(this::onClick);
        rb3.setOnClickListener(this::onClick);
        rb4.setOnClickListener(this::onClick);

        setTask();

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

    @Override
    public void onClick(View v) {

        if (taskCompleted)
            return;
        boolean found = (Integer.parseInt(v.getTag().toString()) == correctAnswer);
        if (found) {
            head.setText(getString(R.string.correct));
            head.setTextColor(Color.GREEN);

            new CountDownTimer(1200, 1200) {

                public void onTick(long msUntilFinished) {
                }

                @Override
                public void onFinish() {
                    head.setText(getString(R.string.guessWriteOption));
                    head.setTextColor(Color.BLACK);
                }
            }.start();

            if (used[2] == -1)
                setTask();
            else {
                taskCompleted = true;
                CardActivity.showDialog(true, false,
                        lastTime, 0, v.getContext());
                ru.sibsutis.a6keys.GameScreen.taskCompleted[5] = true;
                GameScreen.changeScore(1500, 0, (int) lastTime / 1000);
            }
        } else {
            head.setText(getString(R.string.incorrect));
            head.setTextColor(Color.RED);
            taskCompleted = true;
            CardActivity.showDialog(false, true,
                    lastTime, 1, v.getContext());
        }
    }
}

