package io.github.changjiashuai.pulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 16/8/25 11:46.
 */
public class FooterView extends View implements PullToRefreshLayout.OnViewHeightListener {
    private static final String TAG = "FooterView";

    public FooterView(Context context) {
        this(context, null);
    }

    public FooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FooterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private TextPaint mTextPaint;

    private void init() {
        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(14);
        mTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("上拉加载...", 300, 10, mTextPaint);

    }

    @Override
    public void begin() {
        Log.i(TAG, "begin: ");
    }

    @Override
    public void onHeight(float currentHeight, float maxHeight) {

    }

    @Override
    public void end() {
        Log.i(TAG, "end: ");
    }
}
