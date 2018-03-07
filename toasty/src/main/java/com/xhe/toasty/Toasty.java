package com.xhe.toasty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntRange;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by hexiang on 2018/2/6.
 */
public class Toasty {


    private static ToastHandler handler;

    //持续时间
    public static final int LENGTH_SHORT = 1500;
    public static final int LENGTH_LONG = 2500;


    private Toasty(Activity activity) {
    }

    /**
     * @param context 必须是activity的
     * @return
     */
    public static ToastyBuilder with(Context context) {
        if (!(context instanceof Activity)) {
            throw new RuntimeException("$ context must be activity");
        }
        if (!isOnMainThread()) {
            throw new RuntimeException("$ toasty must be builder in MainThread");
        }
        handler = ToastHandler.getInstance((Activity) context);
        return new ToastyBuilder(handler);
    }

    /**
     * 判断是否在当前主线程
     *
     * @return
     */

    public static boolean isOnMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }


}
