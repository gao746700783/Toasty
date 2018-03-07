package com.xhe.toasty;

import android.view.View;

/**
 * Created by gengqiquan on 2018/3/7.
 */

public interface ToastInterface {
    void setMessage(CharSequence msg);

    View getRealView();
}
