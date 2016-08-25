package io.github.changjiashuai.pulltorefreshlayout;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 16/8/25 14:35.
 */
public interface RefreshListener {
    void begin();
    void refreshing(int progress);
    void end();
}
