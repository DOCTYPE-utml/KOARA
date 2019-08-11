package io.nkmr.httpiida.koara;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class CountService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepDetectorSensor;
    SharedPreferences data;

    @Override
    public void onCreate(){
        data=getSharedPreferences("Data",MODE_PRIVATE);

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        stepDetectorSensor=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this,stepDetectorSensor,sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Context context=getApplicationContext();
        String channelId="default";
        String title=context.getString(R.string.app_name);

        NotificationManager notificationManager=
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel=new NotificationChannel(
                channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
        if(notificationManager!=null){
            notificationManager.createNotificationChannel(channel);
            Notification notification=new Notification.Builder(context,channelId)
                    .setContentTitle(title)
                    .setSmallIcon(android.R.drawable.ic_menu_agenda)
                    .setContentText("KOARA")
                    .build();
            startForeground(1,notification);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType()==Sensor.TYPE_STEP_DETECTOR){
            SharedPreferences.Editor editor=data.edit();
            editor.putInt("count",data.getInt("count",0)+1);
            editor.apply();
        }
    }
}
