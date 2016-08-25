package io.github.changjiashuai.pulltorefresh;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
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
    private static final String TAG = "PullToRefreshLayout";
    private static final long ANIM_TIME = 250;
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
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mHeaderHeight = dp2Px(getContext(), mHeaderHeight);
        mFooterHeight = dp2Px(getContext(), mFooterHeight);
        mMaxHeaderHeight = 2 * mHeaderHeight;
        mMaxFooterHeight = 2 * mFooterHeight;
        Log.i("TAG", "init: childCount=" + getChildCount());
//        if (getChildCount() != 1) {
//            throw new IllegalArgumentException("child only can be one!");
//        }
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
                }
                if (canLoadMore) {
                    //处理上拉
                    if (dy < 0 && !canChildScrollDown()) {//到达低部而且到达可以上拉加载View最小的高度
                        //开始上拉加载
                        mFooterView.begin();
                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRefreshing || mLoading) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = event.getY();
                float dy = mCurrentY - mTouchY;
                if (dy > 0 && canRefresh) {
                    dy = Math.min(mMaxHeaderHeight, dy);
                    dy = Math.max(0, dy);
                    mHeaderView.getLayoutParams().height = (int) dy;
                    ViewCompat.setTranslationY(mChildView, dy);
                    requestLayout();
                    mHeaderView.refreshing(dy, mMaxHeaderHeight);
                } else {
                    if (canLoadMore) {
                        dy = Math.min(mMaxFooterHeight, Math.abs(dy));
                        dy = Math.max(0, Math.abs(dy));
                        mFooterView.getLayoutParams().height = (int) dy;
                        ViewCompat.setTranslationY(mChildView, -dy);
                        requestLayout();
                        mFooterView.refreshing(dy, mMaxFooterHeight);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int dy2 = (int) (event.getY() - mTouchY);
                if (dy2 > 0 && canRefresh) {
                    if (dy2 >= mHeaderHeight) {
                        //
                        Log.i(TAG, "onTouchEvent: 开始刷新");
                        startRefresh(dy2 > mMaxHeaderHeight ? mMaxHeaderHeight : dy2, mHeaderHeight);
                    } else if (dy2 > 0 && dy2 < mHeaderHeight) {
                        endRefresh(dy2);
                        Log.i(TAG, "onTouchEvent: 结束刷新");
                    }
                } else {
                    if (canLoadMore) {
                        if (Math.abs(dy2) >= mFooterHeight) {
                            Log.i(TAG, "onTouchEvent: 开始上拉加载");
                            startLoadMore(Math.abs(dy2) > mMaxFooterHeight ? mMaxFooterHeight : Math.abs(dy2), mFooterHeight);
                        } else {
                            endLoadMore(Math.abs(dy2));
                            Log.i(TAG, "onTouchEvent: 结束上拉加载");
                        }
                    }
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        mCurrentY = 0;
        mTouchY = 0;
    }

    public static final int REFRESH = 0;
    public static final int LOADMORE = 1;

    private void startRefresh(int startY, int endY) {
        createTranslationYAnimation(REFRESH, startY, endY, new CallBack() {
            @Override
            public void onSuccess() {
                mRefreshing = true;
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        });
    }

    private void startLoadMore(int startY, int endY) {
        createTranslationYAnimation(LOADMORE, startY, endY, new CallBack() {
            @Override
            public void onSuccess() {
                mLoading = true;
                if (mOnLoadMoreListener != null) {
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        });
    }

    private void endRefresh(int currentY) {
        createTranslationYAnimation(REFRESH, currentY, 0, new CallBack() {
            @Override
            public void onSuccess() {
                mRefreshing = false;
                Log.i(TAG, "onSuccess: ");
                mHeaderView.end();
            }
        });
    }

    private void endLoadMore(int currentY) {
        createTranslationYAnimation(LOADMORE, currentY, 0, new CallBack() {
            @Override
            public void onSuccess() {
                mLoading = false;
                mFooterView.end();
            }
        });
    }

    private void createTranslationYAnimation(final int state, int startY, final int endY,
                                             final CallBack callBack) {
        ValueAnimator mValueAnimator = ValueAnimator.ofInt(startY, endY);
        mValueAnimator.setDuration(ANIM_TIME);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (state == REFRESH) {
                    mHeaderView.getLayoutParams().height = value;
                    ViewCompat.setTranslationY(mChildView, value);
                    mHeaderView.refreshing(value, mMaxHeaderHeight);
                } else {
                    mFooterView.getLayoutParams().height = value;
                    ViewCompat.setTranslationY(mChildView, -value);
                    mFooterView.refreshing(value, mMaxFooterHeight);
                }
                if (value == endY) {
                    if (callBack != null) {
                        callBack.onSuccess();
                    }
                }
                requestLayout();
            }
        });
        mValueAnimator.start();
    }

    public void endRefresh() {
        if (mHeaderView != null && mHeaderView.getLayoutParams().height > 0 && mRefreshing) {
            endRefresh(mHeaderHeight);
        }
    }

    public void endLoadMore() {
        if (mFooterView != null && mFooterView.getLayoutParams().height > 0 && mLoading) {
            endLoadMore(mFooterHeight);
        }
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
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface CallBack {
        void onSuccess();
    }

    public int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
