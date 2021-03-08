package ru.sibsutis.a6keys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class CardActivity extends Activity implements View.OnClickListener {

    final private Bitmap [] cards = new Bitmap[5];
    private int [] cardIndexes= new int[16];
    private ImageView [][] views = new ImageView[4][4];

    boolean taskStarted=false;
    int stage=1;

    private void loadCards()
    {
        cards[0]= BitmapFactory.decodeResource(getResources(),R.drawable.card1);
        cards[1]= BitmapFactory.decodeResource(getResources(),R.drawable.card2);
        cards[2]= BitmapFactory.decodeResource(getResources(),R.drawable.card3);
        cards[3]= BitmapFactory.decodeResource(getResources(),R.drawable.card4);
        cards[4]= BitmapFactory.decodeResource(getResources(),R.drawable.card_back);
    }

    private void setTable(){

        int rows=1,cols=1;
        if(stage>0&&stage<4)
        {
            rows+=stage;
            cols+=stage;
        }


        TableLayout table = findViewById(R.id.CardTable);
        table.removeAllViewsInLayout();
        table.setShrinkAllColumns(true);
        table.setStretchAllColumns(true);

        for (int i=0; i < rows; i++) {
            TableRow row = new TableRow(this);

            for (int j=0; j < cols; j++) {
                views[i][j] = new ImageView(this);
                cardIndexes[cols*i+j]=MathActivity.getRandomNumber(0,4);
                views[i][j].setImageBitmap(cards[cardIndexes[cols*i+j]]);
                views[i][j].setTag(cols*i+j);
                row.addView(views[i][j]);
            }
            table.addView(row);
        }
        new CountDownTimer(1000*rows, 1000) {
            public void onTick(long msUntilFinished) {
                taskStarted=false;
            }

            @Override
            public void onFinish() {
                taskStarted=true;
                Log.wtf("Starting task","xd");
                int rows=1,cols=1;
                if(stage>0&&stage<4)
                {
                    rows+=stage;
                    cols+=stage;
                }
                for (int i=0; i < rows; i++) {
                    for (int j=0; j < cols; j++) {
                        views[i][j].setImageBitmap(cards[4]);
                        views[i][j].setOnClickListener(CardActivity.this);
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
    }

    @Override
    public void onClick(View v) {
        if(!taskStarted)
            return;
        int tag = Integer.parseInt(v.getTag().toString());

        if(cardIndexes[tag]>0)
        {
            Log.wtf("tag","is"+ tag);
            //TODO обработка нажатия
            cardIndexes[tag]=-1;
        }
        else
            Log.wtf("tag","is less 0");
    }
}
