package ru.sibsutis.a6keys;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MathActivity extends Activity {

    final private long TIME_LIMIT = 100000;
    final private int MAX_ATTEMPTS = 3;
    final private int TASKS_TO_SOLVE = 10;
    private boolean taskCompleted = false;
    private Button submit;
    private EditText answerView;
    private TextView taskView;
    private TextView time;
    private TextView mathToast;
    private Pair<String, Integer> task;
    private long lastTime;
    private int attempts = 0;
    private int correct = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_math);

        submit = (Button) findViewById(R.id.MathButtonSubmit);
        answerView = (EditText) findViewById(R.id.MathAnswer);
        taskView = (TextView) findViewById(R.id.MathTask);
        mathToast = (TextView) findViewById(R.id.MathToast);
        time = (TextView) findViewById(R.id.MathTime);
        mathToast.setText("");
        setTask();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskCompleted)
                    return;
                if (answerView.getText().length() < 1)
                    return;
                if (answerIsCorrect(answerView.getText().toString(), task.second)) {
                    mathToast.setText(getString(R.string.correct));
                    mathToast.setTextColor(Color.GREEN);
                    correct++;
                    if (correct >= TASKS_TO_SOLVE) {
                        taskCompleted = true;
                        CardActivity.showDialog(true, false,
                                lastTime, attempts, v.getContext());
                        ru.sibsutis.a6keys.GameScreen.taskCompleted[0] = true;
                        GameScreen.changeScore(1000, attempts, (int) lastTime / 1000);
                    }
                } else {
                    mathToast.setText(getString(R.string.incorrect));
                    mathToast.setTextColor(Color.RED);
                    attempts++;
                    if (attempts > MAX_ATTEMPTS) {
                        taskCompleted = true;
                        CardActivity.showDialog(false, true,
                                lastTime, attempts, v.getContext());
                    }
                }
                mathToast.setAlpha(1f);
                mathToast.animate().alpha(0f).setDuration(800);
                setTask();
            }
        });
        attempts = 0;
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
                    CardActivity.showDialog(false, false,
                            lastTime, attempts, taskView.getContext());
                }
            }
        }.start();
    }

    //включая min, не включая max
    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static char getRandomOperation() {
        char[] operations = {'+', '-', '*', '/'};
        return operations[getRandomNumber(0, 4)];
    }

    private int solveTask(int v1, int v2, char op) {
        switch (op) {
            case '+':
                return v1 + v2;
            case '-':
                return v1 - v2;
            case '*':
                return v1 * v2;
            case '/':
                return v1 / v2;
            default:
                return 0;
        }
    }

    private Pair<String, Integer> generateTask() {
        String task;
        Integer answer;

        int first = getRandomNumber(-100, 101);
        int second = getRandomNumber(-100, 101);
        char operation = getRandomOperation();

        if (operation == '/') {
            if (second != 0)
                first = second * getRandomNumber(1, 11);
            else {
                first = 1;
                second = 1;
            }
        }
        if (operation == '*') {
            second = getRandomNumber(-10, 11);
        }

        answer = solveTask(first, second, operation);
        task = String.valueOf(first) + operation;
        if (second < 0)
            task += "(" + second + ")";
        else
            task += String.valueOf(second);

        return new Pair(task, answer);
    }

    private void setTask() {
        task = generateTask();
        answerView.setText("");
        taskView.setText(task.first);
    }

    private boolean answerIsCorrect(String userAnswer, Integer realAnswer) {
        return Integer.parseInt(userAnswer) == realAnswer;
    }

}
