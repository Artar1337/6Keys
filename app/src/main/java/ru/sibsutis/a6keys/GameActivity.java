package ru.sibsutis.a6keys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {

    public Button gotoDoorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout game = new FrameLayout(this);

        LinearLayout gameWidgets = new LinearLayout (this);
        gotoDoorButton = new Button(this);

        gameWidgets.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        gameWidgets.setGravity(Gravity.CENTER | Gravity.TOP);

        gotoDoorButton.setWidth(400);
        //gotoDoorButton.setAlpha(1.0f);
        gotoDoorButton.setEnabled(false);
        gotoDoorButton.setText(getString(R.string.startTask));
        gotoDoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"ewfwe",Toast.LENGTH_SHORT).show();

            }
        });

        gameWidgets.addView(gotoDoorButton);
        GameScreen gameView = new GameScreen (this,this);
        game.addView(gameView);
        game.addView(gameWidgets);
        setContentView(game);

    }
}
