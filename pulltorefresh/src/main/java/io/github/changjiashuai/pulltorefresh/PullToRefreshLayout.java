package io.github.changjiashuai.pulltorefresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import io.github.changjiashuai.pulltorefreshlayout.R;

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

    private OnViewHeightListener mOnViewHeightListener;
    private int mHeaderHeight = 100;    //default 100px
    private int mMaxHeaderHeight = 200; //default 200px=2*mHeaderHeight
    private int mFooterHeight = 100;
    private int mMaxFooterHeight = 200;

    private View mHeaderView;
    private View mChildView;
    private View mFooterView;
    private float mCurrentY;
    private float mTouchY;

    private int mHeaderViewResId;
    private int mFooterViewResId;

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
        super(context, attrs);
        init(context, attrs);
    }

    //public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    //    super(context, attrs, defStyleAttr);
    //    init(context, attrs);
    //}

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    //public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    //    super(context, attrs, defStyleAttr, defStyleRes);
    //    init(context, attrs, defStyleRes);
    //}

    private void init(Context context, AttributeSet attrs) {
        mHeaderHeight = dp2Px(context, mHeaderHeight);
        mFooterHeight = dp2Px(context, mFooterHeight);
        mMaxHeaderHeight = 2 * mHeaderHeight;
        mMaxFooterHeight = 2 * mFooterHeight;
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.PullToRefreshLayout);

        LayoutInflater mInflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderViewResId = a.getResourceId(R.styleable.PullToRefreshLayout_headerView,
                R.layout.header_view);
        mFooterViewResId = a.getResourceId(R.styleable.PullToRefreshLayout_footerView,
                R.layout.footer_view);
        a.recycle();
        mHeaderView = mInflater.inflate(mHeaderViewResId, this, false);
        mFooterView = mInflater.inflate(mFooterViewResId, this, false);

        Log.i("TAG", "init: childCount=" + getChildCount());
//        if (getChildCount() != 1) {
//            throw new IllegalArgumentException("child only can be one!");
//        }
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    public boolean isCanRefresh() {
        return canRefresh;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setHeaderHeight(int headerHeight) {
        mHeaderHeight = headerHeight;
    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public void setFooterHeight(int footerHeight) {
        mFooterHeight = footerHeight;
    }

    public int getFooterHeight() {
        return mFooterHeight;
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
                        if (mHeaderView instanceof OnViewHeightListener) {
                            mOnViewHeightListener = (OnViewHeightListener) mHeaderView;
                            mOnViewHeightListener.begin();
                        }
                        return true;
                    }
                }
                if (canLoadMore) {
                    //处理上拉
                    if (dy < 0 && !canChildScrollDown()) {//到达低部而且到达可以上拉加载View最小的高度
                        //开始上拉加载
                        if (mFooterView instanceof OnViewHeightListener) {
                            mOnViewHeightListener = (OnViewHeightListener) mFooterView;
                            mOnViewHeightListener.begin();
                        }
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
                    if (mHeaderView instanceof OnViewHeightListener) {
                        mOnViewHeightListener = (OnViewHeightListener) mHeaderView;
                        mOnViewHeightListener.onHeight(dy, mMaxHeaderHeight);
                    }
                } else {
                    if (canLoadMore) {
                        dy = Math.min(mMaxFooterHeight, Math.abs(dy));
                        dy = Math.max(0, Math.abs(dy));
                        mFooterView.getLayoutParams().height = (int) dy;
                        ViewCompat.setTranslationY(mChildView, -dy);
                        requestLayout();
                        if (mFooterView instanceof OnViewHeightListener) {
                            mOnViewHeightListener = (OnViewHeightListener) mFooterView;
                            mOnViewHeightListener.onHeight(dy, mMaxFooterHeight);
                        }
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
                if (mHeaderView instanceof OnViewHeightListener) {
                    mOnViewHeightListener = (OnViewHeightListener) mHeaderView;
                    mOnViewHeightListener.end();
                }
            }
        });
    }

    private void endLoadMore(int currentY) {
        createTranslationYAnimation(LOADMORE, currentY, 0, new CallBack() {
            @Override
            public void onSuccess() {
                mLoading = false;
                if (mFooterView instanceof OnViewHeightListener) {
                    mOnViewHeightListener = (OnViewHeightListener) mFooterView;
                    mOnViewHeightListener.end();
                }
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
                    if (mHeaderView instanceof OnViewHeightListener) {
                        mOnViewHeightListener = (OnViewHeightListener) mHeaderView;
                        mOnViewHeightListener.onHeight(value, mMaxHeaderHeight);
                    }
                } else {
                    mFooterView.getLayoutParams().height = value;
                    ViewCompat.setTranslationY(mChildView, -value);
                    if (mFooterView instanceof OnViewHeightListener) {
                        mOnViewHeightListener = (OnViewHeightListener) mFooterView;
                        mOnViewHeightListener.onHeight(value, mMaxHeaderHeight);
                    }
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

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        if (canRefresh) {
            Log.i(TAG, "autoRefresh: mHeaderView"+mHeaderView);
            if (mHeaderView != null && mHeaderView instanceof PullToRefreshLayout.OnViewHeightListener) {
                ((OnViewHeightListener) mHeaderView).begin();//开始动画
            }
            startRefresh(0, mHeaderHeight);
        }
    }

    /**
     * 结束刷新
     */
    public void endRefresh() {
        if (mHeaderView != null && mHeaderView.getLayoutParams().height > 0 && mRefreshing) {
            endRefresh(mHeaderHeight);
        }
    }

    /**
     * 结束上拉加载
     */
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
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnViewHeightListener {
        void begin();

        /**
         * 用来做刷新动画进度监听
         *
         * @param currentHeight //滑动高度
         * @param maxHeight     //定义的最大滑动高度
         */
        void onHeight(float currentHeight, float maxHeight);

        void end();
    }

    private interface CallBack {
        void onSuccess();
    }

    private int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
