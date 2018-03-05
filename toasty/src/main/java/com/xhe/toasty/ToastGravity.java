package com.xhe.toasty;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hexiang on 2018/2/28.
 */

@IntDef({Toasty.Gravity.TOP, Toasty.Gravity.CENTER, Toasty.Gravity.BOTTOM})
@Retention(RetentionPolicy.SOURCE)
public @interface ToastGravity {
}
