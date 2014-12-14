package com.epam.dzmitry_slutski.gesturetest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test
 * Created by Dzmitry Slutski on 10.12.2014.
 */
public class GestureService extends Service implements GestureOverlayView.OnGesturePerformedListener, View.OnTouchListener,
        View.OnClickListener {

    public static final String TAG = GestureService.class.getSimpleName();
    boolean startDrag;
    AtomicBoolean animateShow = new AtomicBoolean(false);
    private LayoutInflater mLayoutInflater;
    private View mLeftMenu;
    private WindowManager mWindowManager;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private int screenWidth;
    private int screenHeight;
    private float deltaX;
    private float deltaY;
    private LinearLayout.LayoutParams mDrawerLayoutParams;

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        mLeftMenu = mLayoutInflater.inflate(R.layout.drawer_layout, null, false);
        mView = mLeftMenu.findViewById(R.id.drawer);
        mDrawerLayoutParams = (LinearLayout.LayoutParams) mView.getLayoutParams();
        Button button = (Button) mLeftMenu.findViewById(R.id.clicker);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMenu();
            }
        });
//        GestureDetector detector = new GestureDetector(this, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.TOP | Gravity.START;
//        mParams.horizontalMargin = -UiUtils.getPx(this, 100.0f);

        screenWidth = UiUtils.getDisplayWidth(this);
        screenHeight = UiUtils.getDisplayHeight(this);

        Log.d(TAG, "W: " + screenWidth + " H: " + screenHeight);

        mParams.x = 0;
        mParams.y = 0;
//        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        mParams.width = Math.round(UiUtils.getPx(this, 10.0f));
//        mParams.width = 20;

        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        int round = Math.round(UiUtils.getPx(this, 200.0f));
        mDrawerLayoutParams.setMargins(-round, 0, 0, 0);

        mLeftMenu.setOnTouchListener(this);
        mLeftMenu.setOnClickListener(this);
        mWindowManager.addView(mLeftMenu, mParams);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        Log.d(TAG, "onGesturePerformed");
    }

    @Override
    protected void finalize() throws Throwable {
        Log.d(TAG, "service finalize");
        super.finalize();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            deltaX = x;
            deltaY = y;
            Log.d(TAG, "Down: " + deltaX);
            startDrag = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            startDrag = false;
            Log.d(TAG, "Up: " + deltaX);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            if ((x > (screenWidth / 2)) && !animateShow.get()) {
//                animateShow.set(true);
//                runShow();
//            }
            if (!animateShow.get()) {
                Log.d(TAG, "Move: " + deltaX + " eventX: " + x);
                mParams.x += (x - deltaX);
                mParams.y += (y - deltaY);

                int round = Math.round(UiUtils.getPx(this, 200.0f)) + mParams.y;
                mDrawerLayoutParams.setMargins(-round, 0, 0, 0);
                mView.setLayoutParams(mDrawerLayoutParams);
//                mDrawerLayoutParams.width = (int) deltaX;

                mWindowManager.updateViewLayout(mLeftMenu, mParams);
                deltaX = x;
                deltaY = y;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");
        /**/
    }

    private void runShow() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 100; i > 0; i--) {
                    mParams.x -= 10;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mWindowManager.updateViewLayout(mLeftMenu, mParams);
                        }
                    });
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                animateShow.set(false);
                Log.d(TAG, "show finish");
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeMenu();
    }

    private void removeMenu() {
        mWindowManager.removeViewImmediate(mLeftMenu);
    }
}
