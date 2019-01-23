package com.xhe.toasty.annotation;

import android.support.annotation.IntDef;

import com.xhe.toasty.MToast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({MToast.GRAVITY_TOP, MToast.GRAVITY_CENTER, MToast.GRAVITY_BOTTOM})
@Retention(RetentionPolicy.SOURCE)
public @interface MToastGravity {
}
