package io.github.changjiashuai.pulltorefreshlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 16/8/25 11:43.
 */
public class HeaderView extends BaseView{
    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void begin() {

    }

    @Override
    public void refreshing(int progress) {

    }

    @Override
    public void end() {

    }
}
