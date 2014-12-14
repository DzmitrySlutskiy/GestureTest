package com.epam.dzmitry_slutski.gesturetest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Test
 * Created by Dzmitry Slutski on 10.12.2014.
 */
public class GestureService2 extends Service implements GestureOverlayView.OnGesturePerformedListener, View.OnTouchListener,
        View.OnClickListener {

    public static final String TAG = GestureService2.class.getSimpleName();
    /**
     * TODO need optimize score detection value.
     */
    public static final double GESTURE_SCORE_DETECTION = 3.0;
    /**
     * Gesture name with symbol "Z"
     */
    public static final String GESTURE_NAME = "ZGesture";
    private LayoutInflater mLayoutInflater;
    private GestureLibrary mGestureLib;
    private WindowManager mWindowManager;
    private GestureOverlayView mGestureOverlayView;

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        GestureDetector detector = new GestureDetector(this, this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        mGestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        mGestureLib.load();

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

//WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,//TYPE_SYSTEM_OVERLAY,
//                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//                PixelFormat.TRANSLUCENT);

//        ViewGroup mTopView = (ViewGroup) mLayoutInflater.inflate(R.layout.activity_main, null);
//        mGestureOverlayView = (GestureOverlayView) mTopView.findViewById(R.id.gestures);

//        mGestureOverlayView = new GestureOverlayView(this);
//        mGestureOverlayView.setBackgroundResource(R.color.background_gesture);
//        mGestureOverlayView.addOnGesturePerformedListener(this);
//        mGestureOverlayView.setOnTouchListener(this);

//        mWindowManager.addView(mGestureOverlayView, params);

        View view = new TView(this);
//        view.setOnTouchListener(this);
        view.setBackgroundResource(R.color.background_gesture);

        view.setOnClickListener(this);
        mWindowManager.addView(view, params);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        Log.d(TAG, "onGesturePerformed");
        ArrayList<Prediction> predictions = mGestureLib.recognize(gesture);
        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            Log.d(TAG, "prediction.name: " + prediction.name + " score: " + prediction.score);

            if (prediction.score > GESTURE_SCORE_DETECTION) {

                //check gesture name
                if (prediction.name.equals(GESTURE_NAME)) {
                    Toast.makeText(this, "Gesture Z detected", Toast.LENGTH_SHORT).show();
                    //TODO need logic for remove gestureOverlayView
//                    mWindowManager.removeViewImmediate(mGestureOverlayView);
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        Log.d(TAG, "service finalize");
        super.finalize();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: " + event);
        return false;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
    }

    private class TView extends View {

        public TView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.d(TAG, "onTouch: " + event);
            return super.onTouchEvent(event);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            Log.d(TAG, "dispatchTouchEvent: " + event);
            return super.dispatchTouchEvent(event);
        }
    }
}
