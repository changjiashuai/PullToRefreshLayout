package io.github.changjiashuai.pulltorefreshlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 16/8/25 11:05.
 */
public class PullToRefreshLayout extends FrameLayout {

    private OnRefreshListener mOnRefreshListener;
    private boolean mRefreshing = false;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mLoading = false;
    private boolean canRefresh = true;
    private boolean canLoadMore = true;

    private int mHeaderHeight = 100;    //default 100px
    private int mMaxHeaderHeight = 200; //default 200px=2*mHeaderHeight
    private int mFooterHeight = 100;
    private int mMaxFooterHeight = 200;

    private View mHeaderView;
    private View mChildView;
    private View mFooterView;
    private float mCurrentY;
    private float mTouchY;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (getChildCount() != 1) {
            throw new IllegalArgumentException("child only can be one!");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mChildView = getChildAt(0);
    }

    private void addHeaderView(){

    }

    private void addFooterView(){

    }

    public interface OnRefreshListener {
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
