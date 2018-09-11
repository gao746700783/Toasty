package com.xhe.toasty;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({MToast.LENGTH_LONG, MToast.LENGTH_SHORT})
@Retention(RetentionPolicy.SOURCE)
public @interface MToastDuration {
}
