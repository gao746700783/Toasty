package com.xhe.toasty;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by gengqiquan on 2018/3/7.
 */

public class ToastHandler extends Handler {
    private static WeakReference<Activity> mWeakActivity;
    private WeakReference<ToastInterface> mWeakView;

    private static boolean isShowing = false;//toast是否正在显示，标志全局的
    private static ArrayDeque<ToastyBuilder> queue = new ArrayDeque<>();
    private static final int ANIMATION_DURATION = 500;//toast进入界面的动画所需时间
    private static ToastHandler handler;

    private ToastHandler(Activity activity) {
        mWeakActivity = new WeakReference<Activity>(activity);

        ToastInterface toastView = Toasty.getToastFactory().createToastView(activity);
        mWeakView = new WeakReference<ToastInterface>(toastView);
        ViewGroup.LayoutParams lp = toastView.getRealView().getLayoutParams();
        if (lp == null) {
            lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        }

        FrameLayout container = (FrameLayout) activity.findViewById(android.R.id.content);
        container.addView(toastView.getRealView(), lp);
        toastView.getRealView().setVisibility(View.GONE);

        clear();
    }

    protected static ToastHandler getInstance(Activity newActivity) {
        if (mWeakActivity == null || mWeakActivity.get() == null || !mWeakActivity.get().getClass().getSimpleName().equals(newActivity.getClass().getSimpleName())) {
            handler = new ToastHandler(newActivity);
        }
        return handler;
    }

    @Override
    public void handleMessage(Message msg) {
        if (mWeakActivity.get() == null || mWeakActivity.get().isFinishing()) {
            clear();
            return;
        }
        super.handleMessage(msg);
        switch (msg.what) {
            case ADD:
                dealBuilder((ToastyBuilder) msg.obj);
                break;
            case SHOW:
                showToast((ToastyBuilder) msg.obj);
                break;
            case HIDE:
                hideToast();
                break;
        }

    }

    private void dealBuilder(ToastyBuilder builder) {
        if (!isShowing) {//当前没有显示
            setToastMsg(builder);
            Message message = obtainMessage();
            message.obj = builder;
            message.what = SHOW;
            sendMessage(message);
            return;
        }
        if (builder.isReplace()) {//直接替换当前显示的，需要移除之前消失动画
            setToastMsg(builder);
            removeMessages(HIDE);
            sendEmptyMessageDelayed(HIDE, builder.getDuration());
            return;
        }
        if (builder.isWaiting()) {
            queue.add(builder);
        }
    }

    private void setToastMsg(ToastyBuilder builder) {
        //设置显示内容
        ToastInterface toastView = mWeakView.get();
        if (toastView == null) {
            return;
        }
        toastView.setMessage(builder.getMsg());

        //设置显示位置
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toastView.getRealView().getLayoutParams();
        switch (builder.getGravity()) {
            case Gravity.TOP:
                lp.gravity = Gravity.TOP | Gravity.CENTER;
                break;
            case Gravity.CENTER:
                lp.gravity = Gravity.CENTER;
                break;
            case Gravity.BOTTOM:
                lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
                break;
        }
    }

    private void clear() {
        queue.clear();
        if (mWeakActivity.get() == null || mWeakActivity.get().isFinishing()) {
            removeMessages(ADD);
            removeMessages(SHOW);
            removeMessages(HIDE);
        }
        isShowing = false;
    }

    private void showToast(final ToastyBuilder builder) {
        if (!isAppOnForeground()) {  //检查activity处于前台才显示，否则，移除该activity所有的toast
            ToastInterface toastView = mWeakView.get();
            if (toastView == null) {
                return;
            }
            toastView.getRealView().setVisibility(View.GONE);
            //找出所有该activity对toast，并移除
            clear();
            return;
        }
        // 显示动画
        AlphaAnimation mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        mFadeInAnimation.setDuration(ANIMATION_DURATION);

        mFadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isShowing = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("PToast", getClass().getSimpleName() + "------mFadeInAnimation-onAnimationEnd");
                sendEmptyMessageDelayed(HIDE, builder.getDuration());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ToastInterface toastView = mWeakView.get();
        if (toastView == null) {
            return;
        }
        toastView.getRealView().clearAnimation();
        toastView.getRealView().setVisibility(View.VISIBLE);
        toastView.getRealView().startAnimation(mFadeInAnimation);


    }

    private void hideToast() {
        if (!isAppOnForeground()) {
            ToastInterface toastView = mWeakView.get();
            if (toastView == null) {
                return;
            }
            toastView.getRealView().setVisibility(View.GONE);
            //找出所有该activity对toast，并移除
            clear();
            return;
        }
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
                ToastInterface toastView = mWeakView.get();
                if (toastView == null) {
                    return;
                }
                toastView.getRealView().setVisibility(View.GONE);
                isShowing = false;
                if (!queue.isEmpty()) {
                    ToastyBuilder builder = queue.removeLast();
                    setToastMsg(builder);
                    showToast(builder);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ToastInterface toastView = mWeakView.get();
        if (toastView == null) {
            return;
        }
        toastView.getRealView().startAnimation(mFadeOutAnimation);
    }


    final private static int ADD = -0x300001;
    final private static int SHOW = -0x300002;
    final private static int HIDE = -0x300003;

    public void show(ToastyBuilder builder) {
        Message message = obtainMessage();
        message.obj = builder;
        message.what = ADD;
        sendMessage(message);
    }

    private boolean isAppOnForeground() {
        Activity activity = mWeakActivity.get();
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = manager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            Log.d("PToast", "top Activity = " + tasksInfo.get(0).topActivity.getClassName());
            if (activity.getClass().getName().equals(tasksInfo.get(0).topActivity.getClassName())) {
                Log.d("PToast", activity.getClass().getSimpleName() + "----在前台");
                return true;
            }
        }
        Log.d("PToast", activity.getClass().getSimpleName() + "----在后台");

        return false;
    }
}
