package ru.sibsutis.a6keys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //this.setContentView(new GameScreen(this));
        this.setContentView(R.layout.activity_main);

        Intent StartIntent = new Intent(MenuActivity.this, GameActivity.class);
        Intent PrefsIntent = new Intent(MenuActivity.this, PrefsActivity.class);


    }
}
