package ru.sibsutis.a6keys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.setContentView(new GameScreen(this));
        this.setContentView(R.layout.activity_menu);

        Intent StartIntent = new Intent(MenuActivity.this, GameActivity.class);
        Intent PrefsIntent = new Intent(MenuActivity.this, PrefsActivity.class);
        PrefsActivity.loadPrefs(this);

        Button bStart = findViewById(R.id.MenuButtonStart);
        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(StartIntent);
            }
        });
        Button bExit = findViewById(R.id.MenuButtonExit);
        bExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button bPref = findViewById(R.id.MenuButtonPref);
        bPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PrefsIntent);
            }
        });


    }
}
