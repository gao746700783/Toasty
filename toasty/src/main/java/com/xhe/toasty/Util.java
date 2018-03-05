package com.xhe.toasty;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by hexiang on 2018/2/28.
 */

public class Util {
    /**
     * 判断activity是否位于前台
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = manager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            Log.d("PToast", "top Activity = " + tasksInfo.get(0).topActivity.getClassName());
            if (context.getClass().getName().equals(tasksInfo.get(0).topActivity.getClassName())) {
                Log.d("PToast", context.getClass().getSimpleName() + "----在前台");
                return true;
            }
        }
        Log.d("PToast", context.getClass().getSimpleName() + "----在后台");

        return false;
    }
}
