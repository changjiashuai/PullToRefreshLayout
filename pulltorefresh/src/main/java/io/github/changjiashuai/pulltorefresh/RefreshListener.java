package io.github.changjiashuai.pulltorefresh;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 16/8/25 14:35.
 */
public interface RefreshListener {
    void begin();

    /**
     * 用来做刷新动画进度监听
     *
     * @param progress //滑动高度
     * @param total    //定义的最大滑动高度
     */
    void refreshing(float progress, float total);

    void end();
}
