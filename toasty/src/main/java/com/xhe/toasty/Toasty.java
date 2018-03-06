package com.xhe.toasty;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by hexiang on 2018/2/6.
 */
public class Toasty {

    private static boolean isShowing = false;//toast是否正在显示，标志全局的
    private static ArrayDeque<Toasty> queue = new ArrayDeque<>();//先进先出，保持队列是唯一的
    private static final int ANIMATION_DURATION = 200;//toast进入界面的动画所需时间


    private Activity activity;
    private ViewGroup container;//父容器
    private View view;//toast的view
    private String msg;//展示的内容
    private long duration = Time.LENGTH_SHORT;//持续时间
    private int gravity = Gravity.BOTTOM;//位置
    private boolean isReplace = false;//是否直接替换
    private Handler handler;
    private Runnable outRun;

    //持续时间
    public static class Time {
        public static final long LENGTH_SHORT = 1500;
        public static final long LENGTH_LONG = 2500;
    }

    //展示位置
    public static class Gravity {
        public static final int TOP = 0;
        public static final int CENTER = 1;
        public static final int BOTTOM = 2;
    }

    private Toasty() {
    }

    /**
     * @param context 必须是activity的
     * @param msg
     * @return
     */
    public static Toasty makeText(Context context, String msg) {
        Toasty pToast = new Toasty();
        if (!(context instanceof Activity)) {
            throw new RuntimeException("$ context must be activity");
        }
        pToast.activity = (Activity) context;
        pToast.msg = msg;
        //默认值
        pToast.gravity = Gravity.BOTTOM;
        pToast.duration = Time.LENGTH_LONG;
        return pToast;
    }

    /**
     * 持续时间
     *
     * @param duration
     * @return
     */
    public Toasty duration(@IntRange(from = 1) long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * 位置
     * 只能上中下
     *
     * @param gravity
     * @return
     */
    public Toasty gravity(@ToastGravity int gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * 是否直接替换
     *
     * @param replace
     * @return
     */
    public Toasty replace(boolean replace) {
        isReplace = replace;
        return this;
    }


    public void show() {
        //有内容的时候才加入队列
        if (msg != null && msg.trim().length() > 0) {
            //直接替换
            if (isReplace && !queue.isEmpty()) {
                //拿到当前正在现实的那个toast对象
                Toasty toast = queue.getFirst();
                //清空栈
                queue.clear();
                //移除handler的通知
                if (toast.outRun != null) {
                    Log.d("PToast", getClass().getSimpleName() + "------if (toast.outRun != null) {");
                    toast.handler.removeCallbacks(toast.outRun);
                }
                //移除view
                if (toast.container != null && toast.view != null) {
                    toast.view.setVisibility(View.GONE);
                    toast.container.removeView(toast.view);
                }

                isShowing = false;
            }

            queue.addLast(this);
        }

        if (!isShowing) {
            wakeUp();
        }
    }

    private void wakeUp() {
        Log.d("PToast", getClass().getSimpleName() + "------wakeUp()");
        isShowing = true;
        if (!queue.isEmpty()) {
            Toasty util = queue.getFirst();
            util.doshow();
        } else {
            isShowing = false;
        }

    }

    private void doshow() {
        container = (ViewGroup) activity.findViewById(android.R.id.content);
        view = activity.getLayoutInflater().inflate(R.layout.toast_layout_hexiang, null);
        container.addView(view);
        view.setVisibility(View.GONE);
        //设置显示内容
        TextView mTextView = (TextView) view.findViewById(R.id.mbMessage);
        mTextView.setText(msg);

        //设置显示位置
        LinearLayout llContanier = (LinearLayout) view.findViewById(R.id.mbContainer);
        switch (gravity) {
            case Gravity.TOP:
                llContanier.setGravity(android.view.Gravity.TOP | android.view.Gravity.CENTER);
                break;
            case Gravity.CENTER:
                llContanier.setGravity(android.view.Gravity.CENTER);
                break;
            case Gravity.BOTTOM:
                llContanier.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER);
                break;
        }


        // 显示动画
        AlphaAnimation mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        mFadeInAnimation.setDuration(ANIMATION_DURATION);
        //检查activity处于前台才显示，否则，移除该activity所有的toast
        if (Util.isAppOnForeground(activity)) {
            view.setVisibility(View.VISIBLE);
            view.startAnimation(mFadeInAnimation);
        } else {
            view.setVisibility(View.GONE);
            container.removeView(view);
            //找出所有该activity对toast，并移除
            removeActivityAllToast(activity);
            wakeUp();
            return;
        }
        mFadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("PToast", getClass().getSimpleName() + "------mFadeInAnimation-onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("PToast", getClass().getSimpleName() + "------mFadeInAnimation-onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // 消失动画
        final AlphaAnimation mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFadeOutAnimation.setDuration(ANIMATION_DURATION);
        mFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("PToast", getClass().getSimpleName() + "------mFadeOutAnimation-onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("PToast", getClass().getSimpleName() + "------mFadeOutAnimation-onAnimationEnd");
                // 隐藏布局，不使用remove方法为防止多次创建多个布局
                view.setVisibility(View.GONE);
                container.removeView(view);
                if (!queue.isEmpty()) {
                    queue.removeFirst();//栈顶出栈
                }
                wakeUp();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        outRun = new Runnable() {
            @Override
            public void run() {
                if (Util.isAppOnForeground(activity)) {
                    view.startAnimation(mFadeOutAnimation);
                } else {
                    view.setVisibility(View.GONE);
                    container.removeView(view);
                    //找出所有该activity对toast，并移除
                    removeActivityAllToast(activity);
                    wakeUp();
                }
            }
        };
        handler = new Handler();
        //过duration时间段让toast消失
        handler.postDelayed(outRun, duration);
    }

    /**
     * 移除某个activity的所有toast
     *
     * @param activity
     */
    private void removeActivityAllToast(Activity activity) {
        ArrayDeque<Toasty> clone = queue.clone();
        for (Iterator itr = clone.iterator(); itr.hasNext(); ) {
            Toasty toast = (Toasty) itr.next();
            if (toast.activity.getClass().getName().equals(activity.getClass().getName())) {
                queue.removeFirst();
            }
        }
    }

}
