package ru.sibsutis.a6keys;

import android.util.Pair;

import java.util.Random;

public class MathActivity {

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

        int first = getRandomNumber(-100, 100);
        int second = getRandomNumber(-100, 100);
        char operation = getRandomOperation();

        if (operation == '/') {
            if (first > 0)
                second = first * getRandomNumber(1, 11);
            else {
                first = 1;
                second = 1;
            }
        }

        answer = solveTask(first, second, operation);
        task = String.valueOf(first) + operation;
        if (second < 0)
            task += "(" + second + ")";
        else
            task += String.valueOf(second);

        return new Pair(task, answer);
    }

    private boolean answerIsCorrect(String userAnswer, Integer realAnswer) {
        return Integer.parseInt(userAnswer) == realAnswer;
    }

}
