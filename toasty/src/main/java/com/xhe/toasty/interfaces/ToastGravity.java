package com.xhe.toasty.interfaces;

import android.support.annotation.IntDef;
import android.view.Gravity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hexiang on 2018/2/28.
 */

@IntDef({Gravity.TOP, Gravity.CENTER, Gravity.BOTTOM})
@Retention(RetentionPolicy.SOURCE)
public @interface ToastGravity {
}
