package com.example.rahul.myapplication;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class SensorActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private long lastUpdate;
    Boolean bool = false, bool1 = false;

    Boolean firstTime;

    ImageView img;

    float x; float y; float z;
    float initialX, initialY, initialZ;

    float scaleFactor = 2;

    Uri fileUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstTime = true;

        // Get the message from the intent
        Intent intent = getIntent();
        fileUri = intent.getData();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();


        int width = this.getResources().getDisplayMetrics().widthPixels;
        int height = this.getResources().getDisplayMetrics().heightPixels;

        img = new ImageView(this);
        img.setId(R.id.hello_world);
        img.setImageURI(fileUri);
        img.setScaleX(scaleFactor);
        img.setScaleY(scaleFactor);

        img.setPivotX(width / 2);
        img.setPivotY(height / 2);

        setContentView(img);
    }

    @Override
    public void onStart() {
        super.onStart();

        firstTime = true;
    }

    protected void onResume() {
        super.onResume();

        firstTime = true;
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float[] values = event.values;
            // Movement
            x = values[0];
            y = values[1];
            z = values[2];

            if(firstTime){
                initialX = x;
                initialY = y;
                initialZ = z;
                firstTime = false;
            }

            TextView edit = (TextView) findViewById(R.id.sensor);
            if(edit != null) edit.setText("azi: " + x + "\npit: " + y + "\nrol: " + z);

            int width = this.getResources().getDisplayMetrics().widthPixels;
            int height = this.getResources().getDisplayMetrics().heightPixels;

            // found these online: http://developer.android.com/guide/topics/graphics/hardware-accel.html
            img.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            ObjectAnimator.ofFloat(img, "rotationY", 180).start();

            img.setX(width / 2 - img.getWidth() / 2 - img.getWidth() * scaleFactor * (x - initialX) / 60);
            img.setY(height / 2 - img.getHeight() / 2 - img.getHeight() * scaleFactor * (y - initialY) / 45);

            img.setRotation(z - initialZ);
//            img.setRotationX(initialY - y);
//            img.setRotationY(- initialX + x);

            //setContentView(R.layout.activity_sensor);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    public void scale(View view) {
        scaleFactor += 0.1;

        img.setScaleX(scaleFactor);
        img.setScaleY(scaleFactor);
    }
}
