package com.epam.dzmitry_slutski.gesturetest;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test
 * Created by Dzmitry Slutski on 10.12.2014.
 */
public class GestureService extends Service implements GestureOverlayView.OnGesturePerformedListener, View.OnTouchListener,
        View.OnClickListener {

    public static final String TAG = GestureService.class.getSimpleName();
    boolean isOpening;
    AtomicBoolean animateShow = new AtomicBoolean(false);
    private LayoutInflater mLayoutInflater;
    private View mShower;
    private View mDrawer;
    private View mDrawerBody;
    private WindowManager.LayoutParams mDrawerParams;
    private RelativeLayout.LayoutParams mDrawerBodyLayoutParams;
    private RelativeLayout.LayoutParams mShowerLayoutParams;


    private WindowManager mWindowManager;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mDeltaX;
    private int mDeltaY;
    private int mMaxWidth;
    private int mMinWidth;

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mDrawer = mLayoutInflater.inflate(R.layout.drawer_layout, null, false);
        mShower = mDrawer.findViewById(R.id.shower);
        mDrawerBody = mDrawer.findViewById(R.id.drawer_body);

        mDrawerBodyLayoutParams = (RelativeLayout.LayoutParams) mDrawerBody.getLayoutParams();
        mShowerLayoutParams = (RelativeLayout.LayoutParams) mShower.getLayoutParams();
        mMaxWidth = mShowerLayoutParams.width + mDrawerBodyLayoutParams.width;
        mMinWidth = mShowerLayoutParams.width;

        Button button = (Button) mDrawer.findViewById(R.id.clicker);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDrawer();
            }
        });
//        GestureDetector detector = new GestureDetector(this, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        prepare();

        return START_NOT_STICKY;
    }

    void prepare() {
        mDrawerParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mDrawerParams.gravity = Gravity.TOP | Gravity.START;

        mScreenWidth = UiUtils.getDisplayWidth(this);
        mScreenHeight = UiUtils.getDisplayHeight(this);

        Log.d(TAG, "W: " + mScreenWidth + " H: " + mScreenHeight);

        mDrawerParams.x = 0;
        mDrawerParams.y = 0;

        mDrawerBodyLayoutParams.setMargins(- mDrawerBodyLayoutParams.width, 0, 0, 0);
        mDrawerBody.setLayoutParams(mDrawerBodyLayoutParams);

        mDrawer.setOnTouchListener(GestureService.this);
        mDrawer.setOnClickListener(GestureService.this);
        mDrawerParams.width = mShowerLayoutParams.width;

        Log.d(TAG, "mDrawerParams:" + mDrawerParams + " width: " + mDrawerParams.width);
        mWindowManager.addView(mDrawer, mDrawerParams);
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
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());
        int currentAction = event.getAction();
        if (currentAction == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Down: eventX: " + x);
            mDeltaX = x;
            mDeltaY = y;

        } else if (currentAction == MotionEvent.ACTION_UP) {
            Log.d(TAG, "Up: mDeltaX: " + mDeltaX + " eventX: " + x);
            if (isOpening/* && mDrawerParams.width > mMaxWidth / 2*/) {
                runShow();
            } else {
                runHide();
            }

        } else if (currentAction == MotionEvent.ACTION_MOVE) {
            if (! animateShow.get()) {
                Log.d(TAG, "width: " + mDrawerParams.width + " mDeltaX: " + mDeltaX + " eventX: " + x);

                int direction = x - mDeltaX;
                isOpening = direction > 0;

                int newWidth = mDrawerParams.width + direction;

                if ((mMaxWidth > newWidth) &&
                        (newWidth > mShowerLayoutParams.width)) {

                    int margin = mDrawerBodyLayoutParams.leftMargin + direction;
                    mDrawerBodyLayoutParams.setMargins(margin, 0, 0, 0);
//                    mDrawerBody.setLayoutParams(mDrawerBodyLayoutParams);

                    mDrawerParams.width = newWidth;

                    mWindowManager.updateViewLayout(mDrawer, mDrawerParams);
                }
                mDeltaX = x;
                mDeltaY = y;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        //
    }

    private void runShow() {
        animateMove(mDrawerParams.width, mMaxWidth);
    }

    private void runHide() {
        animateMove(mDrawerParams.width, mMinWidth);
    }

    private void animateMove(int from, int to) {
        ValueAnimator animation = ValueAnimator.ofInt(from, to);
        animation.setDuration(700);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();

                mDrawerParams.width = value;
                mWindowManager.updateViewLayout(mDrawer, mDrawerParams);

                int margin = value - mShowerLayoutParams.width - mDrawerBodyLayoutParams.width;
                mDrawerBodyLayoutParams.setMargins(margin, 0, 0, 0);
            }
        });
        animation.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeDrawer();
    }

    private void removeDrawer() {
        mWindowManager.removeViewImmediate(mDrawer);
    }
}
