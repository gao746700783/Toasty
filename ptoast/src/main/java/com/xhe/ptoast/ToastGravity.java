package com.xhe.ptoast;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hexiang on 2018/2/28.
 */

@IntDef({PToast.Gravity.TOP, PToast.Gravity.CENTER, PToast.Gravity.BOTTOM})
@Retention(RetentionPolicy.SOURCE)
public @interface ToastGravity {
}
