package ru.sibsutis.a6keys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class PrefsActivity extends Activity {

    public static int maxScore = 0;
    public static boolean sound, music;

    public static void savePrefs(Context context) {
        SharedPreferences sPref = context.getSharedPreferences("6KeysPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("6KeysScore", PrefsActivity.maxScore);
        ed.putBoolean("6KeysSounds", PrefsActivity.sound);
        ed.putBoolean("6KeysMusic", PrefsActivity.music);
        ed.commit();
    }

    public static void loadPrefs(Context context) {
        SharedPreferences sPref = context.getSharedPreferences("6KeysPref", MODE_PRIVATE);
        maxScore = sPref.getInt("6KeysScore", 0);
        sound = sPref.getBoolean("6KeysSounds", true);
        music = sPref.getBoolean("6KeysMusic", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_preferences);

        PrefsActivity.loadPrefs(this);

        Button bBack = findViewById(R.id.PrefButtonExit);
        Button bReset = findViewById(R.id.PrefButtonRESET);
        TextView record = findViewById(R.id.PrefRecord);
        record.setText(getString(R.string.record) + " " + maxScore);

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefsActivity.savePrefs(v.getContext());
                finish();
            }
        });
        bReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxScore = 0;
                PrefsActivity.savePrefs(v.getContext());
                record.setText(getString(R.string.record) + " 0");
            }
        });

        CheckBox cbSound = findViewById(R.id.cbSound);
        cbSound.setChecked(sound);
        cbSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sound = isChecked;
                PrefsActivity.savePrefs(buttonView.getContext());
            }
        });

    }
}