package ru.sibsutis.a6keys;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class EstimateActivity extends Activity {

    final private long TIME_LIMIT = 100000;
    final private float PERCENT_LIMIT = 90;

    private Bitmap ball;
    private Bitmap pipe;
    private Bitmap pipeTop;
    private boolean taskCompleted=false;
    private float userPercent=100;
    private long lastTime;
    private int stage=1;
    private int ballsCount=0;
    private int percentage;

    private TextView header;
    private EditText answer;

    private void taskUpdate(){
        if (stage < 1 || stage > 3) {
            return;
        }

        TableLayout table = findViewById(R.id.EstLayout);
        table.removeAllViewsInLayout();
        table.setShrinkAllColumns(true);
        table.setStretchAllColumns(true);

        if(stage==1){
            percentage=MathActivity.getRandomNumber(5,96);

            Bitmap water = Bitmap.createBitmap(pipe.getWidth(),
                    pipe.getHeight(),Bitmap.Config.ARGB_8888);
            water.eraseColor(android.graphics.Color.GRAY);

            Canvas canvas = new Canvas(water);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(android.graphics.Color.BLUE);

            float scaledPercentage=((float)pipe.getHeight()-2.0f*pipeTop.getHeight())/100.0f;
            scaledPercentage*=percentage;

            canvas.drawRect(0,pipeTop.getHeight()+scaledPercentage,pipe.getWidth()
                    ,pipe.getHeight()-pipeTop.getHeight(),paint);

            ImageView view = new ImageView(this);
            view.setImageBitmap(water);
            view.draw(canvas);

            ImageView pipeView = new ImageView(this);
            pipeView.setImageBitmap(pipe);

            FrameLayout layout=new FrameLayout(this);

            layout.addView(view);
            layout.addView(pipeView);
            table.addView(layout);
        }
        else if(stage==2){
            int rows=10;
            int count=0;
            for (int i = 0; i < rows; i++) {
                TableRow row = new TableRow(this);

                int cols=MathActivity.getRandomNumber(2,11);

                for (int j = 0; j < cols; j++) {
                    ImageView view = new ImageView(this);
                    view.setImageBitmap(ball);
                    count++;
                    row.addView(view);
                }
                table.addView(row);
            }
            ballsCount=count;
        }
        else{

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set No Title
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.activity_estimate);

        ball= BitmapFactory.decodeResource(getResources(),R.drawable.ball);
        pipe= BitmapFactory.decodeResource(getResources(),R.drawable.pipe);
        pipeTop=BitmapFactory.decodeResource(getResources(),R.drawable.pipe_top);

        TextView time = findViewById(R.id.EstTime);
        header = findViewById(R.id.EstHeader);
        answer = findViewById(R.id.EstEditText);
        Button bAnswer = findViewById(R.id.EstButton);

        taskUpdate();

        //Главный игровой таймер
        new CountDownTimer(TIME_LIMIT, 1000) {

            public void onTick(long msUntilFinished) {
                //запоминаем оставшееся время для формирования
                //количества очков
                if (!taskCompleted){
                    lastTime = msUntilFinished;
                    time.setText(String.valueOf(msUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (!taskCompleted) {
                    CardActivity.showDialog(false,false,
                            lastTime,0,header.getContext());
                }
            }
        }.start();

    }
}
