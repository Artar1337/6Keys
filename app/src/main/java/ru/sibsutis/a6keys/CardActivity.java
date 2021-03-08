package ru.sibsutis.a6keys;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


//наследуем от appCompat чтобы работал dialog класс
public class CardActivity extends AppCompatActivity {

    final private long TIME_LIMIT = 100000;
    final private int ATTEMPT_LIMIT = 29;

    final private Bitmap[] cards = new Bitmap[5];
    private int[] cardIndexes = new int[16];
    private ImageView[] views = new ImageView[16];

    private static String[] cardNames = new String[5];
    private static int currentCard;
    private static int currentCardTag;

    private int attempts=0;
    public static long lastTime;


    //устанавливается в true после фазы запоминания
    boolean taskStarted = false;
    //устанавливается в true после решения третьей фазы
    boolean taskCompleted = false;
    int userScore;
    int stage = 1;

    //класс выбора карточки (список из фигур)
    public static class CardDialog extends DialogFragment {

        public CardDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.pickCard)
                    .setItems(cardNames, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == currentCard)
                                currentCard = -1;
                            else if (which == 4)
                                currentCard = 4;
                            Log.wtf("card", "is " + currentCard);
                            ((CardActivity) getActivity()).parseDialogResult();
                        }
                    });
            return builder.create();
        }
    }

    private void loadCards() {
        cards[0] = BitmapFactory.decodeResource(getResources(), R.drawable.card1);
        cards[1] = BitmapFactory.decodeResource(getResources(), R.drawable.card2);
        cards[2] = BitmapFactory.decodeResource(getResources(), R.drawable.card3);
        cards[3] = BitmapFactory.decodeResource(getResources(), R.drawable.card4);
        cards[4] = BitmapFactory.decodeResource(getResources(), R.drawable.card_back);

        cardNames[0] = getString(R.string.card1);
        cardNames[1] = getString(R.string.card2);
        cardNames[2] = getString(R.string.card3);
        cardNames[3] = getString(R.string.card4);
        cardNames[4] = getString(R.string.back);
    }

    private void setTable() {

        int rows = 1, cols = 1;
        if (stage > 0 && stage < 4) {
            rows += stage;
            cols += stage;
        }

        TableLayout table = findViewById(R.id.CardTable);
        table.removeAllViewsInLayout();
        table.setShrinkAllColumns(true);
        table.setStretchAllColumns(true);

        for (int i = 0; i < rows; i++) {
            TableRow row = new TableRow(this);

            for (int j = 0; j < cols; j++) {
                views[cols * i + j] = new ImageView(this);
                cardIndexes[cols * i + j] = MathActivity.getRandomNumber(0, 4);
                views[cols * i + j].setImageBitmap(cards[cardIndexes[cols * i + j]]);
                views[cols * i + j].setTag(cols * i + j);
                row.addView(views[cols * i + j]);
            }
            table.addView(row);
        }
        TextView header = findViewById(R.id.CardHeader);
        header.setText(getString(R.string.knowCard));
        new CountDownTimer(1500 * rows, 1000) {
            public void onTick(long msUntilFinished) {
                taskStarted = false;
            }

            @Override
            public void onFinish() {
                taskStarted = true;
                TextView header = findViewById(R.id.CardHeader);
                header.setText(getString(R.string.solveCard));
                int rows = 1, cols = 1;
                if (stage > 0 && stage < 4) {
                    rows += stage;
                    cols += stage;
                }
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        views[cols * i + j].setImageBitmap(cards[4]);
                        views[cols * i + j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!taskStarted)
                                    return;
                                int tag = Integer.parseInt(v.getTag().toString());
                                //Log.wtf("tag","is "+ tag);
                                if (cardIndexes[tag] > -1) {
                                    currentCardTag = tag;
                                    chooseCardDialog(cardIndexes[tag]);
                                }
                            }
                        });
                    }
                }
            }
        }.start();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_card);

        loadCards();
        setTable();
        userScore = 0;
        attempts=0;

        TextView time = findViewById(R.id.CardTimer);

        //Главный игровой таймер
        new CountDownTimer(TIME_LIMIT, 1000) {

            public void onTick(long msUntilFinished) {
                //запоминаем оставшееся время для формирования
                //количества очков
                if (!taskCompleted){
                    lastTime = msUntilFinished;
                    time.setText(String.valueOf(msUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (!taskCompleted) {
                    showDialog(false);
                }
            }
        }.start();

    }

    public void showDialog(boolean won,boolean mistaken) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            if(mistaken){
                builder.setMessage(getString(R.string.tooMuchAttempts));
            }
        } else {
            builder.setTitle(getString(R.string.gameOver))
                    .setIcon(R.drawable.correct)
                    .setMessage(getString(R.string.taskPassed)+"\n"
                            +getString(R.string.time)+(lastTime/1000)+"\n"
                            +getString(R.string.attempts)+attempts)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showDialog(boolean won) {
        showDialog(won,false);
    }

    public boolean taskDone() {
        for (int i = 0; i < (stage + 1) * (stage + 1); i++) {
            if (cardIndexes[i] > -1)
                return false;
        }
        return true;
    }

    public void chooseCardDialog(int answer) {
        currentCard = answer;
        CardDialog dialog = new CardDialog();
        dialog.show(getSupportFragmentManager(), "card_chooser");
    }

    public void parseDialogResult() {
        //нажали "отмена"
        if (currentCard == 4)
            return;
            //правильный ответ
        else if (currentCard == -1) {
            int tag = currentCardTag;
            views[tag].setImageBitmap(cards[cardIndexes[tag]]);
            cardIndexes[tag] = -1;
            if (taskDone()) {
                if (stage < 3) {
                    stage++;
                    setTable();
                } else {
                    showDialog(true);
                    taskCompleted = true;
                    taskStarted = false;
                }
            }
        }
        //неверный ответ
        else {
            attempts++;
            if(attempts>ATTEMPT_LIMIT){
                showDialog(false,true);
                taskCompleted = true;
                taskStarted = false;
            }
        }
    }
}
