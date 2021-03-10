package ru.sibsutis.a6keys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MenuActivity extends Activity {

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setMessage(R.string.exit_confirm)
                .setPositiveButton(R.string.no, null)
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                onBackPressed();
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
