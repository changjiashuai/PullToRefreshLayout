package io.github.changjiashuai.pulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 16/8/25 11:43.
 */
public class HeaderView extends BaseView {
    private static final String TAG = "HeaderView";

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
        init();
    }
    private Paint mPaint;
    private void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(400, 60, 20, mPaint);
    }

    @Override
    public void begin() {
        Log.i(TAG, "begin: ");
    }

    @Override
    public void refreshing(float progress, float total) {
//        Log.i(TAG, "refreshing: progress=" + progress + ", total=" + total);
    }

    @Override
    public void end() {
        Log.i(TAG, "end: ");
    }
}
