package ru.sibsutis.a6keys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements View.OnClickListener {

    public Button gotoDoorButton;
    public TextView finalDialog;
    private GameScreen gameView;

    public void briefDialog(Context context, Class classToSummon, String briefString) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.task_brief)
                .setMessage(briefString)
                .setPositiveButton(R.string.not_ready, null)
                .setNegativeButton(R.string.ready, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GameScreen.setStartPoint(gameView.character.x, gameView.character.y);
                        Intent intent = new Intent(GameActivity.this, classToSummon);
                        startActivity(intent);
                        GameScreen.sounds.play(GameScreen.soundDoor, GameScreen.soundVolume,
                                GameScreen.soundVolume, 0, 0, 1.5f);
                    }
                })
                .show();
    }

    public void startDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.hello)
                .setMessage(R.string.description)
                .setPositiveButton(R.string.got_it, null)
                .show();
    }

    public void showFinalDialog() {
        String score = getString(R.string.end_game_score) + " " + (GameScreen.userScore)
                + "\n" + getString(R.string.score_saved);
        new AlertDialog.Builder(this)
                .setTitle(R.string.end_game)
                .setMessage(score)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
        GameScreen.userScore = 0;
        GameScreen.setStartPoint(-1, -1);
        for (int i = 0; i < 6; i++)
            GameScreen.taskCompleted[i] = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout game = new FrameLayout(this);

        TableLayout gameWidgets = new TableLayout(this);
        gameWidgets.setShrinkAllColumns(true);
        gameWidgets.setStretchAllColumns(true);
        gameWidgets.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        gameWidgets.setGravity(Gravity.CENTER | Gravity.TOP);

        gotoDoorButton = new Button(this);
        gotoDoorButton.setWidth(600);
        gotoDoorButton.setEnabled(false);
        gotoDoorButton.setText(getString(R.string.startTask));
        gotoDoorButton.setOnClickListener(this);

        finalDialog = new TextView(this);
        finalDialog.setTextSize(32.0f);
        finalDialog.setGravity(Gravity.CENTER_HORIZONTAL);
        finalDialog.setBackgroundResource(R.drawable.background_d);
        finalDialog.setEnabled(false);
        finalDialog.setAlpha(0.0f);

        TableRow row1 = new TableRow(this);
        TableRow row2 = new TableRow(this);
        row1.addView(gotoDoorButton);
        row2.addView(finalDialog);
        gameWidgets.addView(row1);
        gameWidgets.addView(row2);
        gameView = new GameScreen(this, this);
        game.addView(gameView);
        game.addView(gameWidgets);
        setContentView(game);
        startDialog(this);
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(v.getContext(), "Starting task...", Toast.LENGTH_SHORT).show();
        switch (gameView.character.doorNumber) {
            case 1://MATH
                briefDialog(this, MathActivity.class, getString(R.string.task_1));
                break;
            case 2://оценочные вопросы
                briefDialog(this, EstimateActivity.class, getString(R.string.task_2));
                break;
            case 3://логич задачка
                briefDialog(this, LogicActivity.class, getString(R.string.task_3));
                break;
            case 4://задачка с картинкой
                briefDialog(this, PicActivity.class, getString(R.string.task_4));
                break;
            case 5://карточки
                briefDialog(this, CardActivity.class, getString(R.string.task_5));
                break;
            case 6://3 хытрых вопроса
                briefDialog(this, QuestionsActivity.class, getString(R.string.task_6));
                break;
            case 7://идем в главную дверцу
                Toast.makeText(v.getContext(), getString(R.string.entered_end), Toast.LENGTH_LONG).show();
                gameView.notIsInEndRoom = false;
                gameView.startDialogEnding();
                break;
            default:
                break;
        }
    }
}
