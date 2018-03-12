package com.xhe.toasty;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by gengqiquan on 2018/3/7.
 */

public class ToastHandler extends Handler {
    private static WeakReference<Activity> mWeakActivity;
    private WeakReference<ToastDialog> mWeakView;

    private static ArrayDeque<ToastyBuilder> queue = new ArrayDeque<>();
    private static ToastHandler handler;

    private ToastHandler(Activity activity) {
        mWeakActivity = new WeakReference<Activity>(activity);

        ToastDialog toastDialog = ToastDialog.createDialog(Toasty.getToastFactory().createToastView(activity));
        mWeakView = new WeakReference<ToastDialog>(toastDialog);
        clear();
    }


    protected static ToastHandler getInstance(Activity newActivity) {
        if (mWeakActivity == null || mWeakActivity.get() == null || !mWeakActivity.get().equals(newActivity)) {
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
        ToastDialog toastDialog = mWeakView.get();
        if (toastDialog == null || !toastDialog.isShowing()) {//当前没有显示
            setToastMsg(builder);
            Message message = obtainMessage();
            message.obj = builder;
            message.what = SHOW;
            sendMessage(message);
            return;
        }
        if (builder.getReplaceType() == Toasty.REPLACE_NOW) {//直接替换当前显示的，需要移除之前消失动画
            setToastMsg(builder);
            removeMessages(HIDE);
            sendEmptyMessageDelayed(HIDE, builder.getDuration());
            return;
        }
        if (builder.getReplaceType() == Toasty.REPLACE_BEHIND) {
            queue.add(builder);
        }
        //  builder.getReplaceType()==Toasty.DISCARD //## do nothing
    }

    private void setToastMsg(ToastyBuilder builder) {
        //设置显示内容
        ToastDialog toastDialog = mWeakView.get();
        if (toastDialog == null) {
            return;
        }
        toastDialog.getToastView().setMessage(builder.getMsg());

        //设置显示位置
        switch (builder.getGravity()) {
            case Gravity.TOP:
                toastDialog.getWindow().setGravity(Gravity.TOP | Gravity.CENTER);
                toastDialog.getWindow().getAttributes().y = Toasty.dip(toastDialog.getContext(), Toasty.offsetYDip);
                break;
            case Gravity.CENTER:
                toastDialog.getWindow().setGravity(Gravity.CENTER);
                break;
            case Gravity.BOTTOM:
                toastDialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);
                toastDialog.getWindow().getAttributes().y = Toasty.dip(toastDialog.getContext(), Toasty.offsetYDip);
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
    }

    private void showToast(final ToastyBuilder builder) {
        if (!isAppOnForeground()) {  //检查activity处于前台才显示，否则，移除该activity所有的toast
            ToastDialog toastDialog = mWeakView.get();
            if (toastDialog == null) {
                return;
            }
            toastDialog.dismiss();
            //找出所有该activity对toast，并移除
            clear();
            return;
        }
        ToastDialog toastDialog = mWeakView.get();
        if (toastDialog == null) {
            return;
        }
        toastDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                sendEmptyMessageDelayed(HIDE, builder.getDuration());
            }
        });
        toastDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ToastDialog toastDialog = mWeakView.get();
                if (toastDialog == null) {
                    return;
                }
                if (!queue.isEmpty()) {
                    ToastyBuilder builder = queue.removeLast();
                    setToastMsg(builder);
                    showToast(builder);
                }
            }
        });
        toastDialog.show();


    }

    private void hideToast() {
        ToastDialog toastDialog = mWeakView.get();
        if (toastDialog == null) {
            return;
        }
        toastDialog.dismiss();
        return;
    }


    final private static int ADD = 3001;
    final private static int SHOW = 3002;
    final private static int HIDE = 3003;

    protected void show(ToastyBuilder builder) {
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
