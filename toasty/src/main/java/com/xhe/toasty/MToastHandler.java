package com.xhe.toasty;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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

    private ViewGroup.LayoutParams mParams;

    private MToastHandler() {
        mQueue = new LinkedList<>();

        mParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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
        } else */if (what == HIDE_TOAST) {
            handleHide();
        } else if (what == SHOW_TOAST_NEXT) {
            handleShowNext();
        }
    }

    public void add(MToast xToast) {
        mQueue.offer(xToast);

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

        Log.v(TAG, "HANDLE SHOW: " + this + " mView=" + mView + " mNextView=" + mNextView);
        if (mView != mNextView) {
            // remove the old view if necessary
            handleHide(xToast);
            mView = mNextView;

            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (xToast.getContainer() != null) {
                Log.v(TAG, "ADD! " + mView + " in " + this);
                xToast.getContainer().addView(mView, mParams);
            }

        }

        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                handleHide(xToast);
            }
        }, xToast.getDuration());
    }

    private void handleHide(final MToast xToast) {
        if (xToast.getToastView() != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (xToast.getContainer() != null) {
                Log.v(TAG, "REMOVE! " + xToast.getToastView() + " in " + this);
                xToast.getContainer().removeView(xToast.getToastView());
            }

            mView = null;
        }

        mQueue.poll();
        sendEmptyMessage(SHOW_TOAST_NEXT);

    }


    public void cancel() {

    }


    int mGravity;
    int mX, mY;
    float mHorizontalMargin;
    float mVerticalMargin;


    private View mView;
    View mNextView;
    long mDuration;

    private WindowManager mWM;


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
