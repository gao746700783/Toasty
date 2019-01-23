package com.xhe.toasty;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.LinkedList;
import java.util.Queue;

public class MToastHandler extends Handler {

    private static final String TAG = "MToastHandler";

    @SuppressLint("StaticFieldLeak")
    private static MToastHandler mToastHandler;

    private final Queue<MToast> mQueue;

    private final static int SHOW_TOAST = 0x123;
    private final static int HIDE_TOAST = 0x456;
    private final static int SHOW_TOAST_NEXT = 0x789;

    //    private ViewGroup.MarginLayoutParams mParams;
//    private RelativeLayout.LayoutParams mllParams;
    private FrameLayout.LayoutParams mFlParams;

    private MToastHandler() {
        mQueue = new LinkedList<>();

//        mParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        mllParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT);

        mFlParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL);
    }

    public synchronized static MToastHandler getInstance() {
        if (mToastHandler == null) {
            mToastHandler = new MToastHandler();
        }
        return mToastHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        int what = msg.what;
        /*if (what == SHOW_TOAST) {
            *//*IBinder token = (IBinder) msg.obj;*//*
            handleShow(*//*token*//*);
        } else */
        if (what == HIDE_TOAST) {
            handleHide();
        } else if (what == SHOW_TOAST_NEXT) {
            handleShowNext();
        }
    }

    public void enqueue(MToast xToast) {
        // in queue
        mQueue.offer(xToast);
        //
        sendEmptyMessage(SHOW_TOAST_NEXT);
    }

    private void handleShowNext() {
        if (mQueue.isEmpty()) return;
        //获取队列头部的XToast
        MToast xToast = mQueue.peek();
        handleShow(xToast);
    }

    private void handleShow(final MToast xToast) {

        mNextView = xToast.getToastView();

        if (null != mView) {
            Log.v(TAG, "HANDLE SHOW: " + this + " mView=" + mView + " isShowing...");
            return;
        }

//        Log.v(TAG, "HANDLE SHOW: " + this + " mView=" + mView + " mNextView=" + mNextView);
//        if (mView != mNextView) {
//        // remove the old view if necessary
//        handleHide(xToast);
        mView = mNextView;

        // note: checking parent() just to make sure the view has
        // been added...  i have seen cases where we get here when
        // the view isn't yet added, so let's try not to crash.
        if (mContainer != null) {
            Log.v(TAG, "ADD! " + mView + " in " + this);

            mFlParams.gravity |= mGravity;

            if (mGravity == MToast.GRAVITY_BOTTOM) {
                mFlParams.bottomMargin = mY;
            } else if (mGravity == MToast.GRAVITY_TOP) {
                mFlParams.topMargin = mY;
            }

            mFlParams.leftMargin = mX;

            mContainer.addView(mView, mFlParams);
        }
//        }

        // hide at delay time
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                handleHide(xToast);
            }
        }, this.mDuration);
    }

    private void handleHide(final MToast xToast) {
        if (xToast.getToastView() != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mContainer != null) {
                Log.v(TAG, "REMOVE! " + xToast.getToastView() + " in " + this);
                mContainer.removeView(xToast.getToastView());
            }

            mView = null;

            mQueue.poll();
        }

        // show next
        sendEmptyMessage(SHOW_TOAST_NEXT);
    }


    //    public void cancel() {
    //        handleHide();
    //    }

    int mGravity;
    int mX, mY;
    float mHorizontalMargin;
    float mVerticalMargin;

    long mDuration;
    ViewGroup mContainer;

    private View mView;
    private View mNextView;

    //    private WindowManager mWM;

    private void handleHide() {
        if (mView != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mContainer != null) {
                Log.v(TAG, "REMOVE! " + mView + " in " + this);
                mContainer.removeView(mView);
            }

            mView = null;
        }

        mQueue.poll();
        sendEmptyMessage(SHOW_TOAST_NEXT);
    }

}
