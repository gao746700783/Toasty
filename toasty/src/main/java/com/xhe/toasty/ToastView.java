package com.xhe.toasty;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gengqiquan on 2018/3/7.
 */

public class ToastView extends TextView implements ToastInterface {
    public ToastView(Context context) {
        super(context);
    }

    @Override
    public void setMessage(CharSequence msg) {
        setText(msg);

    }

    @Override
    public View getRealView() {
        return this;
    }
}
