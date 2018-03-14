package com.xhe.toasty;

import android.app.Activity;

import com.xhe.toasty.interfaces.ToastInterface;

/**
 * Created by gengqiquan on 2018/3/7.
 */

public abstract class ToastFactory {
    public abstract ToastInterface createToastView(Activity activity);
}
