package io.github.changjiashuai.pulltorefreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.changjiashuai.pulltorefresh.PullToRefreshLayout;
import io.github.changjiashuai.pulltorefresh.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private PullToRefreshLayout mPullToRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<String> mStrings;

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
        StringAdapter mAdapter = new StringAdapter(mStrings);
        mRecyclerView.setAdapter(mAdapter);
        mPullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: ");
                mPullToRefreshLayout.endRefresh();
            }
        });
        mPullToRefreshLayout.setOnLoadMoreListener(new PullToRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore: ");
                mPullToRefreshLayout.endLoadMore();
            }
        });
    }

    private void initData(){
        mStrings = new ArrayList<>();
        for (int i=0;i<20;i++){
            mStrings.add("String--"+i);
        }
    }
}
