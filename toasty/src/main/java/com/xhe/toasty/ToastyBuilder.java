package com.xhe.toasty;

import android.support.annotation.NonNull;
import android.view.Gravity;

import com.xhe.toasty.interfaces.ToastDuration;
import com.xhe.toasty.interfaces.ToastGravity;

/**
 * Created by hexiang on 2018/2/6.
 */
final public class ToastyBuilder {

    private String msg;//展示的内容


    private long duration = Toasty.LENGTH_SHORT;//持续时间
    private int gravity = Gravity.BOTTOM;//位置
    private boolean isReplace = false;//是否直接替换
    private boolean isWaiting = false;//是否尾缀后面展示
    ToastHandler toastHandler;

    protected ToastyBuilder(ToastHandler toastHandler) {
        this.toastHandler = toastHandler;
    }

    /**
     * 持续时间
     *
     * @param duration
     * @return
     */
    public ToastyBuilder duration(@ToastDuration int duration) {
        this.duration = duration;
        return this;
    }

    /**
     * 文本内容
     *
     * @param msg
     * @return
     */
    public ToastyBuilder message(@NonNull String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 位置
     * 只能上中下
     *
     * @param gravity
     * @return
     */
    public ToastyBuilder gravity(@ToastGravity int gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * 是否直接替换
     *
     * @param replace
     * @return
     */
    public ToastyBuilder replace(boolean replace) {
        isReplace = replace;
        return this;
    }

    /**
     * 是否尾缀后面展示
     *
     * @param waiting
     * @return
     */
    public ToastyBuilder waiting(boolean waiting) {
        isWaiting = waiting;
        return this;
    }

    protected String getMsg() {
        return msg;
    }

    protected long getDuration() {
        return duration;
    }

    protected int getGravity() {
        return gravity;
    }

    protected boolean isReplace() {
        return isReplace;
    }

    protected boolean isWaiting() {
        return isWaiting;
    }

    public void show() {
        toastHandler.show(this);
    }

}
