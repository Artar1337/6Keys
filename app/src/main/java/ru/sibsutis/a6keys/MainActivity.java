package ru.sibsutis.a6keys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        FrameLayout game = new FrameLayout(this);
        GameScreen gameView = new GameScreen (this);
        LinearLayout gameWidgets = new LinearLayout (this);

        Button endGameButton = new Button(this);
        TextView myText = new TextView(this);

        endGameButton.setWidth(300);
        endGameButton.setText("Start Game");
        myText.setText("rIZ..i");

        gameWidgets.addView(myText);
        gameWidgets.addView(endGameButton);

        game.addView(gameView);
        game.addView(gameWidgets);

        setContentView(game);
        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"ewfwe",Toast.LENGTH_SHORT).show();

            }
        });

        /*this.setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, MathActivity.class);
        startActivity(intent);*/
    }
}