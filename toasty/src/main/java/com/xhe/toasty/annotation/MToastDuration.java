package com.xhe.toasty.annotation;

import android.support.annotation.LongDef;

import com.xhe.toasty.MToast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@LongDef({MToast.LENGTH_LONG, MToast.LENGTH_SHORT})
@Retention(RetentionPolicy.SOURCE)
public @interface MToastDuration {
}
