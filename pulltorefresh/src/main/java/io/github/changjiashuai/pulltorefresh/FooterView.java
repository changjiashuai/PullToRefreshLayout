package io.github.changjiashuai.pulltorefresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 16/8/25 11:46.
 */
public class FooterView extends View implements PullToRefreshLayout.OnViewHeightListener {
    private static final String TAG = "FooterView";

    private Paint mPaint;
    private RectF rectF;

    private float mWidth = 0f;
    private float mEyeWidth = 0f;

    private float mPadding = 0f;
    private float startAngle = 0f;
    private boolean isSmile = false;

    public FooterView(Context context) {
        this(context, null);
    }

    public FooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getMeasuredWidth() > getHeight()) {
            mWidth = getMeasuredHeight();
        } else {
            mWidth = getMeasuredWidth();
        }
        mWidth = dip2px(50);
        mPadding = dip2px(10);
        mEyeWidth = dip2px(3);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(dip2px(2));
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startX = getWidth() / 2;
        rectF.left = mPadding + startX - mWidth / 2;
        rectF.top = mPadding;
        rectF.right = mWidth - mPadding + startX - mWidth / 2;
        rectF.bottom = mWidth - mPadding;
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawArc(rectF, startAngle, 180, false, mPaint);//第三个参数是否显示半径

        mPaint.setStyle(Paint.Style.FILL);
        if (isSmile) {
            canvas.drawCircle(mPadding + startX - mWidth / 2 + mEyeWidth + mEyeWidth / 2, mWidth / 3, mEyeWidth, mPaint);
            canvas.drawCircle(mWidth - mPadding + startX - mWidth / 2 - mEyeWidth - mEyeWidth / 2, mWidth / 3, mEyeWidth, mPaint);
        }
    }


    public void startAnim() {
        stopAnim();
        startViewAnim(0f, 1f, 1500);
    }

    public void stopAnim() {
        if (valueAnimator != null) {
            clearAnimation();
            isSmile = false;
            mAnimatedValue = 0f;
            startAngle = 0f;
            valueAnimator.setRepeatCount(1);
            valueAnimator.cancel();
            valueAnimator.end();
        }
    }

    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    ValueAnimator valueAnimator;
    float mAnimatedValue = 0f;

    private ValueAnimator startViewAnim(float startF, final float endF, long time) {
        valueAnimator = ValueAnimator.ofFloat(startF, endF);
        valueAnimator.setDuration(time);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                mAnimatedValue = (float) valueAnimator.getAnimatedValue();
                if (mAnimatedValue < 0.5) {
                    isSmile = false;
                    startAngle = 720 * mAnimatedValue;
                } else {
                    startAngle = 720;
                    isSmile = true;
                }

                invalidate();
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
