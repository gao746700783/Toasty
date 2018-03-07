package com.xhe.toasty;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.xhe.toasty.interfaces.ToastInterface;

/**
 * Created by hexiang on 2018/2/6.
 */
public class Toasty {


    private static ToastHandler handler;

    //持续时间
    public static final int LENGTH_SHORT = 1500;
    public static final int LENGTH_LONG = 2500;

    protected static ToastFactory getToastFactory() {
        return toastFactory;
    }

    public static void setToastFactory(ToastFactory toastFactory) {
        Toasty.toastFactory = toastFactory;
    }

    private static ToastFactory toastFactory;

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
        if (toastFactory == null) {
            toastFactory = createDefaultToastFactory();
        }
        handler = ToastHandler.getInstance((Activity) context);
        return new ToastyBuilder(handler);
    }

    private static ToastFactory createDefaultToastFactory() {

        return new ToastFactory() {
            @Override
            ToastInterface createToastView(Activity activity) {
                ToastView toastView = new ToastView(activity);
                toastView.setTextSize(13f);
                toastView.setTextColor(Color.WHITE);
                toastView.setGravity(Gravity.CENTER);
                toastView.setShadowLayer(2.75f, 0, 0, 0xBB000000);
                toastView.setPadding(dip(activity, 18f), dip(activity, 8f), dip(activity, 18f), dip(activity, 8f));

                GradientDrawable bg = new GradientDrawable();
                bg.setCornerRadius(dip(activity, 12));
                bg.setColor(0x77000000);
                toastView.setBackgroundDrawable(bg);

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, dip(activity, 50f), 0, dip(activity, 50f));
                toastView.setLayoutParams(lp);
                return toastView;
            }
        };
    }

    /**
     * dp转px
     *
     * @param context
     * @return
     */
    private static int dip(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 判断是否在当前主线程
     *
     * @return
     */

    private static boolean isOnMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }


}
