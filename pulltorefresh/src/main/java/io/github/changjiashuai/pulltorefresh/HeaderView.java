package io.github.changjiashuai.pulltorefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 16/8/25 11:43.
 */
public class HeaderView extends View implements PullToRefreshLayout.OnViewHeightListener {
    private static final String TAG = "HeaderView";

    private Paint mPaint, mPaintEye;
    private RectF rectF;

    private float mWidth = 0f;
    private float mHigh = 0f;
    private float mPadding = 5f;

    private float eatErWidth = 60f;
    private float eatErPositonX = 0f;
    private int eatSpeed = 5;
    private float beansWidth = 10f;


    private float mAngle = 34;
    private float eatErStrtAngle = mAngle;
    private float eatErEndAngle = 360 - 2 * eatErStrtAngle;
    private ValueAnimator valueAnimator = null;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth();
        mHigh = getMeasuredHeight();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);

        mPaintEye = new Paint();
        mPaintEye.setAntiAlias(true);
        mPaintEye.setStyle(Paint.Style.FILL);
        mPaintEye.setColor(Color.BLACK);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float eatRightX = mPadding + eatErWidth + eatErPositonX;
        rectF.left = mPadding + eatErPositonX;
        rectF.top = mHigh / 2 - eatErWidth / 2;
        rectF.right = eatRightX;
        rectF.bottom = mHigh / 2 + eatErWidth / 2;
        canvas.drawArc(rectF, eatErStrtAngle, eatErEndAngle, true, mPaint);//第三个参数是否显示半径

        canvas.drawCircle(mPadding + eatErPositonX + eatErWidth / 2,
                mHigh / 2 - eatErWidth / 4,
                beansWidth / 2, mPaintEye);

        int beansCount = (int) ((mWidth - mPadding * 2 - eatErWidth) / beansWidth / 2);
        for (int i = 0; i < beansCount; i++) {

            float x = beansCount * i + beansWidth / 2 + mPadding + eatErWidth;
            if (x > eatRightX) {
                canvas.drawCircle(x,
                        mHigh / 2, beansWidth / 2, mPaint);
            }
        }


    }

    private void startAnim() {
        stopAnim();
        startViewAnim(0f, 1f, 3500);
    }

    private void stopAnim() {
        if (valueAnimator != null) {
            clearAnimation();
            valueAnimator.setRepeatCount(1);
            valueAnimator.cancel();
            valueAnimator.end();
            eatErPositonX = 0;
            postInvalidate();
        }
    }

    private ValueAnimator startViewAnim(float startF, final float endF, long time) {
        valueAnimator = ValueAnimator.ofFloat(startF, endF);
        valueAnimator.setDuration(time);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float mAnimatedValue = (float) valueAnimator.getAnimatedValue();
                eatErPositonX = (mWidth - 2 * mPadding - eatErWidth) * mAnimatedValue;
                eatErStrtAngle = mAngle * (1 - (mAnimatedValue * eatSpeed - (int) (mAnimatedValue * eatSpeed)));
                eatErEndAngle = 360 - eatErStrtAngle * 2;
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }
        });
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();

        }

        return valueAnimator;
    }

    @Override
    public void begin() {
        Log.i(TAG, "begin: ");
        startAnim();
    }

    @Override
    public void onHeight(float currentHeight, float maxHeight) {

    }

    @Override
    public void end() {
        Log.i(TAG, "end: ");
        stopAnim();
    }
}
