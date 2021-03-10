package ru.sibsutis.a6keys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class PrefsActivity extends Activity {

    public static int maxScore=0;

    public static void savePrefs(Context context){
        SharedPreferences sPref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("6KeysScore",PrefsActivity.maxScore);
        ed.putFloat("6KeysSounds",GameScreen.soundVolume);
        ed.putFloat("6KeysMusic",GameScreen.soundVolume);
        ed.commit();
    }

    public static void loadPrefs(Context context){
        SharedPreferences sPref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("6KeysScore",GameScreen.userScore);
        ed.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_preferences);
    }
}