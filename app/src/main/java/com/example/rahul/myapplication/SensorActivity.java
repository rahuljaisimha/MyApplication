package com.example.rahul.myapplication;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class SensorActivity extends Activity implements SensorEventListener {

    int width;
    int height;
    private SensorManager sensorManager;
    private long lastUpdate;
    Boolean bool = false, bool1 = false;

    Boolean firstTime;

    ImageView img;

    float x, y, z;
    float prevX, prevY, prevZ;
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
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_FASTEST);
        lastUpdate = System.currentTimeMillis();


        width = this.getResources().getDisplayMetrics().widthPixels;
        height = this.getResources().getDisplayMetrics().heightPixels;

        img = new ImageView(this);

        // found these online: http://developer.android.com/guide/topics/graphics/hardware-accel.html
        img.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            ObjectAnimator.ofFloat(img, "rotationY", 180).start();

        img.setId(R.id.hello_world);
        img.setImageURI(fileUri);
        img.setScaleX(scaleFactor);
        img.setScaleY(scaleFactor);

        img.setPivotX(width / 2);
        img.setPivotY(height / 2);

        if(MyActivity.debug) {
            setContentView(R.layout.activity_sensor);
        } else {
            setContentView(img);
        }
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

    float[] mGravity;
    float[] mGeomagnetic;
    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
//            mGravity = lowPass(event.values.clone(), mGravity);
//        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
//            mGeomagnetic = lowPass(event.values.clone(), mGeomagnetic);
//        if (mGravity != null && mGeomagnetic != null) {
//            float R[] = new float[9];
//            float I[] = new float[9];
//            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
//            if (success) {
//                float orientation[] = new float[3];
//                SensorManager.getOrientation(R, orientation);
//
//                // in radians
//                x = -(orientation[0]);//azi
//                y = (orientation[1]);//pit
//                z = -(orientation[2]);//rol
//
////                if(Math.abs(prevX - x) < 5)
////                    x = prevX;
////                else
////                    prevX = x;
////
////                if(Math.abs(prevY - y) < 1.5)
////                    y = prevY;
////                else
////                    prevY = y;
////
////                if(Math.abs(prevZ - z) < 4)
////                    z = prevZ;
////                else
////                    prevZ = z;
//
//                if (firstTime) {
//                    initialX = x;
//                    initialY = y;
//                    initialZ = z;
//                    firstTime = false;
//                }
//
//                new ImageSensorTask().execute();
//
//            }
//        }
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float[] values = event.values;
            // Movement
            x = (float)Math.toRadians(values[0]);
            y = (float)Math.toRadians(values[1]);
            z = (float)Math.toRadians(values[2]);

            if (firstTime) {
                initialX = x;
                initialY = y;
                initialZ = z;
                firstTime = false;
            }

            new ImageSensorTask().execute();

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

    private class ImageSensorTask extends AsyncTask<Void, Void, Long> {
        private float xSet;
        private float ySet;

        protected Long doInBackground(Void... a) {
            float deltaX = x - initialX;
            float deltaY = y - initialY;
            if(deltaX > Math.PI)    deltaX -= 2*Math.PI;
            if(deltaX < -Math.PI)   deltaX += 2*Math.PI;
            xSet = width / 2 - img.getWidth() / 2 - img.getWidth() * scaleFactor * deltaX * 3 / (float)Math.PI;
            ySet = height / 2 - img.getHeight() / 2 - img.getHeight() * scaleFactor * deltaY * 4 / (float)Math.PI;
//            System.out.println(xSet + " " + ySet);
            return new Long(0);
        }

        protected void onPostExecute(Long result) {
            TextView edit = (TextView) findViewById(R.id.sensor);
            if (edit != null) edit.setText("azi: " + Math.toDegrees(x) + "\npit: " + Math.toDegrees(y) + "\nrol: " + Math.toDegrees(z)
                                                + "\ndx: " + (x - initialX));

            img.setX(xSet);
            img.setY(ySet);

            img.setRotation((float)Math.toDegrees(z - initialZ));

//            img.setRotationX(initialY - y);
//            img.setRotationY(- initialX + x);

//            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//            float refreshRating = display.getRefreshRate();
//
//            System.out.println(refreshRating);
        }
    }


    /*
     * time smoothing constant for low-pass filter
     * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
     * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
     */
    static final float ALPHA = 0.15f;

    /**
     * see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
     * see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     */
    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    // MOVING AVERAGES ALGORITHM / SAVITZKY GOLAY ALGORITHM
}
