package io.nkmr.httpiida.koara;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ImageView imageView;

    SharedPreferences data;
    int count=0;
    boolean started=false;

    float degree=0;
    double speed=0;

    SensorManager sensorManager;
    Sensor stepDetectorSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        stepDetectorSensor=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this,stepDetectorSensor,sensorManager.SENSOR_DELAY_NORMAL);

        data=getSharedPreferences("Data",MODE_PRIVATE);
        started=data.getBoolean("started",false);
        count=data.getInt("count",0);
        speed=count*0.01;

        Button startButton=findViewById(R.id.startButton);
        if(!started) {
            startButton.setText("START");
        }else{
            startButton.setText("STOP");
        }
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!started) {
                    ((TextView) v).setText("STOP");
                    Intent intent=new Intent(getApplication(),CountService.class);
                    startForegroundService(intent);
                }else{
                    ((TextView) v).setText("START");
                    Intent intent=new Intent(getApplication(),CountService.class);
                    stopService(intent);
                }
                started=!started;
                SharedPreferences.Editor editor=data.edit();
                editor.putBoolean("started",started);
                editor.apply();
            }
        });

        Button resetButton=findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=data.edit();
                editor.putInt("count",0);
                editor.apply();
            }
        });

        TextView countView=findViewById(R.id.countView);
        countView.setText(String.valueOf(count));
        TextView speedView=findViewById(R.id.speedView);
        speedView.setText(String.valueOf(speed));

        handler.post(rRotate);
        handler.post(rUpdate);
    }

    Handler handler=new Handler();
    Runnable rRotate=new Runnable() {
        @Override
        public void run() {
            imageView=findViewById(R.id.imageView);
            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.koara);

            Matrix matrix=new Matrix();
            matrix.setRotate(degree,bitmap.getWidth()/2,bitmap.getHeight()/2);
            Bitmap rotatedBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            imageView.setImageBitmap(rotatedBitmap);

            degree+=speed;

            handler.postDelayed(this,1);
        }
    };
    Runnable rUpdate=new Runnable() {
        @Override
        public void run() {
            count=data.getInt("count",0);
            speed=count*0.01;

            TextView countView=findViewById(R.id.countView);
            countView.setText(String.valueOf(count));
            TextView speedView=findViewById(R.id.speedView);
            speedView.setText(String.valueOf(speed));

            handler.postDelayed(this,500);
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType()==Sensor.TYPE_STEP_DETECTOR){
        }
    }
}
