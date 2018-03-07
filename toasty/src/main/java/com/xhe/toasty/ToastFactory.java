package com.xhe.toasty;

import android.app.Activity;

/**
 * Created by gengqiquan on 2018/3/7.
 */

public abstract class ToastFactory {
    abstract ToastInterface createToastView(Activity activity);
}
