package ru.sibsutis.a6keys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements View.OnClickListener {

    public Button gotoDoorButton;
    public TextView finalDialog;
    private GameScreen gameView;

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
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "Starting task...", Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (gameView.character.doorNumber) {
            case 1://MATH
                intent = new Intent(GameActivity.this, MathActivity.class);
                startActivity(intent);
                break;
            case 2://оценочные вопросы
                intent = new Intent(GameActivity.this, EstimateActivity.class);
                startActivity(intent);
                break;
            case 3://логич задачка
                intent = new Intent(GameActivity.this, LogicActivity.class);
                startActivity(intent);
                break;
            case 4://задачка с картинкой
                intent = new Intent(GameActivity.this, PicActivity.class);
                startActivity(intent);
                break;
            case 5://карточки
                intent = new Intent(GameActivity.this, CardActivity.class);
                startActivity(intent);
                break;
            case 6://3 хытрых вопроса
                intent = new Intent(GameActivity.this, QuestionsActivity.class);
                startActivity(intent);
                break;
            case 7://идем в главную дверцу
                gameView.notIsInEndRoom = false;
                gameView.startDialogEnding();
                break;
            default:
                break;
        }
    }
}
