package io.github.changjiashuai.pulltorefreshlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

    private BaseView mHeaderView;
    private View mChildView;
    private BaseView mFooterView;
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
        addHeaderView();
        addFooterView();
    }

    private void addHeaderView() {
        if (mHeaderView == null) {
            mHeaderView = new HeaderView(getContext());
        }
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        mHeaderView.setLayoutParams(layoutParams);//先不显示，高度为0
        if (mHeaderView.getParent() != null) {
            ((ViewGroup) mHeaderView.getParent()).removeAllViews();
        }
        addView(mHeaderView, 0);
    }

    private void addFooterView() {
        if (mFooterView == null) {
            mFooterView = new FooterView(getContext());
        }
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.BOTTOM;
        mFooterView.setLayoutParams(layoutParams);
        if (mFooterView.getParent() != null) {
            ((ViewGroup) mFooterView.getParent()).removeAllViews();
        }
        addView(mFooterView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canRefresh && !canLoadMore) {
            return super.onInterceptTouchEvent(ev);
        }
        if (mRefreshing || mLoading) {
            return true;//当前布局处理
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mCurrentY = mTouchY;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getY() - mCurrentY;
                if (canRefresh) {
                    //处理下拉
                    if (dy > 0 && !canChildScrollUp()) {//到达顶部而且到达可以刷新的下拉View最小的高度
                        //开始下拉刷新
                        mHeaderView.begin();
                        return true;
                    }
                } else if (canLoadMore) {
                    //处理上拉
                    if (dy < 0 && !canChildScrollDown()) {//到达低部而且到达可以上拉加载View最小的高度
                        //开始上拉加载
                        if (mOnLoadMoreListener!=null){
                            mOnLoadMoreListener.begin();
                        }
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 判断子控件是否还可以下拉
     *
     * @return false:已经拉到顶部
     */
    private boolean canChildScrollUp() {
        return mChildView != null && ViewCompat.canScrollVertically(mChildView, -1);
    }

    /**
     * 判断子控件是否还可以上拉
     *
     * @return false:已经到达底部
     */
    private boolean canChildScrollDown() {
        return mChildView != null && ViewCompat.canScrollVertically(mChildView, 1);
    }

    public interface OnRefreshListener {
        void begin();
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
        void end();
    }

    public interface OnLoadMoreListener {
        void begin();
        void onLoadMore();
        void end();
    }

    public int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
