package com.xhe.toasty;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xhe.toasty.annotation.MToastDuration;
import com.xhe.toasty.annotation.MToastGravity;

/**
 *
 */
public class MToast {

    private static final String TAG = "MToast";
    /**
     * Show the view or text notification for a short period of time.  This time
     * could be user-definable.  This is the default.
     */
    public static final long LENGTH_SHORT = 2000;

    /**
     * Show the view or text notification for a long period of time.  This time
     * could be user-definable.
     */
    public static final long LENGTH_LONG = 3500;

    public static final int GRAVITY_TOP = 0;
    public static final int GRAVITY_CENTER = 1;
    public static final int GRAVITY_BOTTOM = 2;

    private Context mContext;
    private Activity mActivity;

    private ViewGroup mContainer;//父容器

    private long mDuration;

    private int mGravity;
    private int mOffsetX, mOffsetY;

    private View mToastView;

    public View getToastView() {
        return mToastView;
    }

    //    private TextView mToastTv;

    // animator


    //    private static boolean isShowing = false;//toast是否正在显示，标志全局的
    //    private static ArrayDeque<MToast> queue = new ArrayDeque<>();//先进先出，保持队列是唯一的
    //    private static final int ANIMATION_DURATION = 200;//toast进入界面的动画所需时间


    // private final MToastManager mTM;
    private final MToastHandler mTH;

    private MToast(Context context) {
        if (!(context instanceof Activity)) {
            throw new RuntimeException("$ context must be activity");
        }

        this.mContext = context;
        this.mActivity = (Activity) context;

        this.mContainer = (ViewGroup) mActivity.findViewById(android.R.id.content);

        this.mTH = MToastHandler.getInstance();

        //        this.mTM = new MToastManager();
        //
        //        // 默认对齐方式 Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM
        //        this.mTM.mGravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        //        // 默认Y方向偏移 64dp
        //        //this.mTM.mY = 300;

    }

    /**
     * @param context 必须是activity的
     * @param msg
     * @return
     */
    public static MToast makeText(Context context, String msg) {
        MToast pToast = new MToast(context);

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.layout_toast_transient, null);
        TextView tv = (TextView) v.findViewById(R.id.message);
        tv.setText(msg);

        pToast.mToastView = v;
//        pToast.mToastTv = tv;

//        pToast.mToastMsg = msg;

        pToast.mGravity = GRAVITY_BOTTOM;
        pToast.mDuration = LENGTH_SHORT;
        return pToast;
    }

    //    public MToast message(String msg) {
    //        this.mToastMsg = msg;
    //        this.mToastTv.setText(msg);
    //        return this;
    //    }

    public MToast view(View toastView) {
        this.mToastView = toastView;
        return this;
    }

    /**
     * 持续时间
     *
     * @param duration
     * @return
     */
    public MToast duration(@MToastDuration long duration) {
        this.mDuration = duration;
        return this;
    }

    /**
     * 位置
     * 只能上中下
     *
     * @param gravity
     * @return
     */
    public MToast gravity(@MToastGravity int gravity) {
        this.mGravity = gravity;
        return this;
    }

    public MToast offset(int offsetX, int offsetY) {
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;

        return this;
    }

    /**
     * Show the view for the specified duration.
     */
    public void show() {
        if (mToastView == null) {
            throw new RuntimeException("setView have not been called");
        }

        this.mTH.mGravity = mGravity;
        this.mTH.mDuration = mDuration;
        this.mTH.mContainer = mContainer;
        this.mTH.mX = mOffsetX;
        this.mTH.mY = mOffsetY;

        this.mTH.enqueue(this);
    }

    //    /**
    //     * Close the view if it's showing, or don't show it if it isn't showing yet.
    //     * You do not normally have to call this.  Normally view will disappear on its own
    //     * after the appropriate duration.
    //     */
    //    public void cancel() {
    //        this.mTH.cancel();
    //    }

}
