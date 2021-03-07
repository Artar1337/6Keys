package ru.sibsutis.a6keys;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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

    private Button submit;
    private EditText answerView;
    private TextView taskView;
    private TextView mathToast;
    private Pair<String,Integer> task;
    private int userScore;

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

        userScore=0;
        mathToast.setText("");
        setTask();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answerView.getText().length()<1)
                    return;
                if(answerIsCorrect(answerView.getText().toString(),task.second)) {
                    scoreChange(true);
                            mathToast.setText(getString(R.string.correct));
                            mathToast.setTextColor(Color.GREEN);
                }
                else {
                    scoreChange(false);
                    mathToast.setText(getString(R.string.incorrect));
                    mathToast.setTextColor(Color.RED);
                }
                mathToast.setAlpha(1f);
                mathToast.animate().alpha(0f).setDuration(800);
                setTask();
            }
        });
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

    private void scoreChange(boolean correct)
    {
        int correctBonus = 10;
        int incorrectFine = -10;
        if(correct){
            userScore+=correctBonus;
            return;
        }
        userScore+=incorrectFine;
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
            second = getRandomNumber(-10,11);
        }

        answer = solveTask(first, second, operation);
        task = String.valueOf(first) + operation;
        if (second < 0)
            task += "(" + second + ")";
        else
            task += String.valueOf(second);

        return new Pair(task, answer);
    }

    private void setTask()
    {
        task = generateTask();
        answerView.setText("");
        taskView.setText(task.first);
    }

    private boolean answerIsCorrect(String userAnswer, Integer realAnswer) {
        return Integer.parseInt(userAnswer) == realAnswer;
    }

}