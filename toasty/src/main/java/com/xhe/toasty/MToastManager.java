package com.xhe.toasty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class MToastManager {

    private static final String TAG = "MToastManager";

    MToastManager() {
        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = R.style.Anim_toast_anim_view;
        // define your own animation
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("MToast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        this.mParams = params;
    }

    public void enqueueToast(){

        mHandler.obtainMessage(0/*, windowToken*/).sendToTarget();
    }

    public void cancel(){}





    int mGravity;
    int mX, mY;
    float mHorizontalMargin;
    float mVerticalMargin;


    private View mView;
    View mNextView;
    long mDuration;

    private WindowManager mWM;

//    static final long SHORT_DURATION_TIMEOUT = 5000;
//    static final long LONG_DURATION_TIMEOUT = 1000;

    private final Runnable mHide = new Runnable() {
        @Override
        public void run() {
            handleHide();
            // Don't do this in handleHide() because it is also invoked by handleShow()
            mNextView = null;
        }
    };

    private WindowManager.LayoutParams mParams;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 0) {
                /*IBinder token = (IBinder) msg.obj;*/
                handleShow(/*token*/);
            } else if (what == 1) {
                handleHide();
            }
        }
    };

//    /**
//     * schedule handleShow into the right thread
//     */
//    public void show(IBinder windowToken) {
//        Log.v(TAG, "SHOW: " + this);
//        mHandler.obtainMessage(0, windowToken).sendToTarget();
//    }
//
//    /**
//     * schedule handleHide into the right thread
//     */
//    public void hide() {
//        Log.v(TAG, "HIDE: " + this);
//        mHandler.post(mHide);
//    }

    private void handleShow(/*IBinder windowToken*/) {
        Log.v(TAG, "HANDLE SHOW: " + this + " mView=" + mView + " mNextView=" + mNextView);
        if (mView != mNextView) {
            // remove the old view if necessary
            handleHide();
            mView = mNextView;
            Context context = mView.getContext().getApplicationContext();
//            String packageName = mView.getContext().getOpPackageName();
            if (context == null) {
                context = mView.getContext();
            }
            mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            // We can resolve the Gravity here by using the Locale for getting
            // the layout direction
            final Configuration config = mView.getContext().getResources().getConfiguration();
            final int gravity;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gravity = Gravity.getAbsoluteGravity(mGravity, config.getLayoutDirection());
                mParams.gravity = gravity;
                if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                    mParams.horizontalWeight = 1.0f;
                }
                if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                    mParams.verticalWeight = 1.0f;
                }
            }
//            mParams.gravity = mGravity;

            mParams.gravity = mGravity | Gravity.CENTER_HORIZONTAL;
            mParams.x = mX;
            mParams.y = mY;
            mParams.verticalMargin = mVerticalMargin;
            mParams.horizontalMargin = mHorizontalMargin;
//            mParams.packageName = packageName;
//            mParams.hideTimeoutMilliseconds = mDuration ==
//                    Toast.LENGTH_LONG ? LONG_DURATION_TIMEOUT : SHORT_DURATION_TIMEOUT;
//            mParams.token = windowToken;
            if (mView.getParent() != null) {
                Log.v(TAG, "REMOVE! " + mView + " in " + this);
                mWM.removeView(mView);
            }

            Log.v(TAG, "ADD! " + mView + " in " + this);
            Log.d(TAG,"mParams:"+mParams.toString());
            mWM.addView(mView, mParams);
//            trySendAccessibilityEvent();

            mHandler.postDelayed(mHide,mDuration);

        }
    }

    private void handleHide() {
        Log.v(TAG, "HANDLE HIDE: " + this + " mView=" + mView);
        if (mView != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mView.getParent() != null) {
                Log.v(TAG, "REMOVE! " + mView + " in " + this);
                mWM.removeViewImmediate(mView);
            }

            mView = null;
        }
    }

}
