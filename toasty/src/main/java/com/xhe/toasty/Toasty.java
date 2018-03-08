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

    /**
     * 显示时间
     *
     * @author gengqiquan
     * @date 2018/3/8 上午9:59
     */
    public static final int LENGTH_SHORT = 1500;
    public static final int LENGTH_LONG = 2500;

    /**
     * 显示中再次请求替换方式
     * <p>
     * DISCARD 直接丢弃
     * REPLACE_NOW 立即替换 ，不管当前显示状态，动画状态，并且延迟显示时间为当前替换者的持续时间
     * REPLACE_BEHIND  等待当前正常显示消失完成 再显示
     *
     * @author gengqiquan
     * @date 2018/3/8 上午9:57
     */
    public static final int DISCARD = -1;
    public static final int REPLACE_NOW = 0;
    public static final int REPLACE_BEHIND = 1;


    protected static ToastFactory getToastFactory() {
        return toastFactory;
    }

    /**
     * 设置toast布局生产工厂
     *
     * @author gengqiquan
     * @date 2018/3/8 上午9:59
     */
    public static void setToastFactory(ToastFactory toastFactory) {
        Toasty.toastFactory = toastFactory;
    }

    private static ToastFactory toastFactory;

    private Toasty(Activity activity) {
    }

    /**
     * @param context 必须是activity的上下文
     *                <p>
     *                弱引用持有
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

    /**
     * 创建默认toast生产工厂
     *
     * @author gengqiquan
     * @date 2018/3/8 上午10:00
     */
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
