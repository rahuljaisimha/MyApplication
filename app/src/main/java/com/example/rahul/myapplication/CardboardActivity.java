package com.example.rahul.myapplication;

import com.example.rahul.myapplication.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class CardboardActivity extends Activity implements SensorEventListener {

    int width;
    int height;
    private SensorManager sensorManager;
    private long lastUpdate;
    Boolean bool = false, bool1 = false;

    Boolean firstTime;

    ImageView img1;
    ImageView img2;

    float x, y, z;
    float prevX, prevY, prevZ;
    float initialX, initialY, initialZ;

    float scaleFactor = 1.12f;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstTime = true;

        // Get the message from the intent
        Intent intent = getIntent();
        Uri fileUri = intent.getData();

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

        setContentView(R.layout.activity_cardboard);

        final View contentView = findViewById(R.id.fullscreen_content);

        img1 = (ImageView)findViewById(R.id.imageView2);
        img1.setImageURI(fileUri);

        img1.setScaleX(scaleFactor);
        img1.setScaleY(scaleFactor);
//
//        img1.setPivotX(width / 4);
//        img1.setPivotY(height / 4);

        img2 = (ImageView)findViewById(R.id.imageView3);
        img2.setImageURI(fileUri);

        img2.setScaleX(scaleFactor);
        img2.setScaleY(scaleFactor);
//
//        img2.setPivotX(width * 3 / 4);
//        img2.setPivotY(height * 3 / 4);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = 0;
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                        } else {
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.fullscreen_content).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float[] values = event.values;
            // Movement
            x = (float)Math.toRadians(values[1]);
            y = (float)Math.toRadians(values[0]);
            z = (float)Math.toRadians(values[2]);

            if (firstTime) {
                initialX = x;
                initialY = y;
                initialZ = z;
                firstTime = false;
            }

//            new ImageSensorTask().execute();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private class ImageSensorTask extends AsyncTask<Void, Void, Long> {
        private float xSet;
        private float ySet;

        protected Long doInBackground(Void... a) {
            float deltaX = x - initialX;
            float deltaY = y - initialY;
            if(deltaX > Math.PI)    deltaX -= 2*Math.PI;
            if(deltaX < -Math.PI)   deltaX += 2*Math.PI;
            xSet = width / 2 - img1.getWidth() / 2 - img1.getWidth() * scaleFactor * deltaX * 3 / (float)Math.PI;
            ySet = height / 2 - img1.getHeight() / 2 - img1.getHeight() * scaleFactor * deltaY * 4 / (float)Math.PI;
//            System.out.println(xSet + " " + ySet);
            return new Long(0);
        }

        protected void onPostExecute(Long result) {
            TextView edit = (TextView) findViewById(R.id.sensor);
            if (edit != null) edit.setText("azi: " + Math.toDegrees(x) + "\npit: " + Math.toDegrees(y) + "\nrol: " + Math.toDegrees(z)
                    + "\ndx: " + (x - initialX));

            img1.setX(xSet);
            img1.setY(ySet);

            //img1.setRotation((float)Math.toDegrees(z - initialZ));

            img2.setX(xSet);
            img2.setY(ySet);

            //img2.setRotation((float)Math.toDegrees(z - initialZ));

//            img.setRotationX(initialY - y);
//            img.setRotationY(- initialX + x);

//            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//            float refreshRating = display.getRefreshRate();
//
//            System.out.println(refreshRating);
        }
    }
}
