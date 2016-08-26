package io.github.changjiashuai.pulltorefreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.github.changjiashuai.pulltorefresh.PullToRefreshLayout;
import io.github.changjiashuai.pulltorefresh.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private PullToRefreshLayout mPullToRefreshLayout;
    private RecyclerView mRecyclerView;
    private StringAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pullToRefreshLayout);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        initData();
        mAdapter = new StringAdapter(initData());
        mRecyclerView.setAdapter(mAdapter);

        View mHeaderView = LayoutInflater.from(this).inflate(R.layout.refresh_view, null);
        View mFooterView = LayoutInflater.from(this).inflate(R.layout.pull_view, null);

        mPullToRefreshLayout.setHeaderView(mHeaderView);
        mPullToRefreshLayout.setFooterView(mFooterView);

        mPullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: ");
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setStrings(initData());
                        mPullToRefreshLayout.endRefresh();
                    }
                }, 1000);
            }
        });
        mPullToRefreshLayout.setOnLoadMoreListener(new PullToRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore: ");
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.insert(addData());
                        mPullToRefreshLayout.endLoadMore();
                    }
                }, 1000);
            }
        });
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshLayout.autoRefresh();
            }
        }, 500);
    }

    private List<String> initData() {
        List<String> mStrings = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mStrings.add("Test String--" + i);
        }
        return mStrings;
    }

    private List<String> addData() {
        List<String> testData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            testData.add("Test Add String--" + i);
        }
        return testData;
    }
}
