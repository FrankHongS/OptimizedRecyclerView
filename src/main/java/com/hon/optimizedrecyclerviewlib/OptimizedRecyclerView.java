package com.hon.optimizedrecyclerviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Frank on 2018/2/22.
 * E-mail:frank_hon@foxmail.com
 */

public class OptimizedRecyclerView extends RecyclerView{

    private View mEmptyView;
    private View mLoadingView;
    private View mErrorView;

    private int mLoadMoreResId;
    private int mNoMoreResId;
    private int mLoadMoreErrorResId;

    private OnLoadMoreListener mLoadMoreListener;
    private OnScrollListener mScrollListener;

    private DataSetObserver mObserver;

    private boolean mIsRefreshing;
    private boolean mNowLoading;
    private boolean mLoadMoreFailed;
    private boolean mNoMore;

    private boolean mAutoLoadMoreEnabled = true;
    private boolean mLoadMoreViewEnabled = true;
    private boolean mNoMoreViewEnabled=true;
    private boolean mLoadMoreFailedViewEnabled=true;

    public OptimizedRecyclerView(Context context) {
        this(context,null);
    }

    public OptimizedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public OptimizedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        obtainStyledAttributes(context, attrs);
        mScrollListener=new OnScrollListener();
        addOnScrollListener(mScrollListener);
        mObserver=new DataSetObserver();
    }

    private void obtainStyledAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.OptimizedRecyclerView);
        int loadingResId = attributes.getResourceId(R.styleable.OptimizedRecyclerView_loading_layout, R.layout
                .default_loading_layout);
        int emptyResId = attributes.getResourceId(R.styleable.OptimizedRecyclerView_empty_layout, R.layout
                .default_empty_layout);
        int errorResId = attributes.getResourceId(R.styleable.OptimizedRecyclerView_error_layout, R.layout
                .default_error_layout);

        mLoadMoreResId = attributes.getResourceId(R.styleable.OptimizedRecyclerView_load_more_layout, R.layout
                .default_load_more_layout);
        mNoMoreResId = attributes.getResourceId(R.styleable.OptimizedRecyclerView_no_more_layout, R.layout
                .default_no_more_layout);
        mLoadMoreErrorResId = attributes.getResourceId(R.styleable.OptimizedRecyclerView_load_more_failed_layout, R
                .layout.default_load_more_failed_layout);

        mLoadingView = LayoutInflater.from(context).inflate(loadingResId, null, false);
        mEmptyView = LayoutInflater.from(context).inflate(emptyResId, null, false);
        mErrorView = LayoutInflater.from(context).inflate(errorResId, null, false);

        attributes.recycle();
    }

    void displayLoadingAndResetStatus() {
        resetStatus();
    }

    void displayContentAndResetStatus() {
        resetStatus();
    }

    void displayEmptyAndResetStatus() {
        resetStatus();
    }

    void displayErrorAndResetStatus() {
        resetStatus();
    }

    void showNoMoreIfEnabled() {
        mNoMore = true;
        displayNoMoreViewOrDisappear();
    }

    void showLoadMoreFailedIfEnabled() {
        mLoadMoreFailed = true;
        displayLoadMoreFailedViewOrDisappear();
    }

    void showResumeLoadMoreIfEnabled() {
        mLoadMoreFailed = false;
        showAutoLoadMoreIfEnabled();
    }

    void showAutoLoadMoreIfEnabled() {
        if (canNotLoadMore()) return;
        displayLoadMoreViewOrDisappear();
        if (mAutoLoadMoreEnabled) {
            mNowLoading = true;
            mLoadMoreListener.onLoadMore();
        }
    }

    void showManualLoadMoreIfEnabled() {
        if (canNotLoadMore()) return;
        displayLoadMoreViewOrDisappear();
        mNowLoading = true;
        mLoadMoreListener.onLoadMore();
    }

    private boolean canNotLoadMore() {
        return mLoadMoreListener == null || mIsRefreshing || mNowLoading || mLoadMoreFailed || mNoMore;
    }

    public void setRefreshing(boolean refreshing){
        mIsRefreshing=refreshing;
    }

    /**
     *  set if auto load more
     * @param autoLoadMoreEnable if true,auto load more
     */
    public void setAutoLoadEnable(boolean autoLoadMoreEnable) {
        mAutoLoadMoreEnabled = autoLoadMoreEnable;
    }

    /**
     * set if showing load_more_view.Anyway,there is nothing to do with Auto Load
     * @param enabled show load_more_view or not
     */
    public void setLoadMoreViewEnabled(boolean enabled) {
        mLoadMoreViewEnabled = enabled;
        if (enabled) {
            if (mScrollListener.isLastItem(this)) {
                showAutoLoadMoreIfEnabled();
                smoothScrollToPosition(getLayoutManager().getItemCount() - 1);
            }
        } else {
            displayLoadMoreViewOrDisappear();
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
        /**
         * 避免第一次滑动到底部时需要再次滑动才会显示load more
         */
//        displayLoadMoreViewOrDisappear();
    }

    private void resetStatus() {
        mIsRefreshing = false;
        mNowLoading = false;
        mLoadMoreFailed = false;
        mNoMore = false;
    }

    private void displayNoMoreViewOrDisappear() {
        displayOrDisappear(mNoMoreResId, mNoMoreViewEnabled);
    }

    private void displayLoadMoreFailedViewOrDisappear() {
        displayOrDisappear(mLoadMoreErrorResId, mLoadMoreFailedViewEnabled);
    }

    private void displayLoadMoreViewOrDisappear() {
        displayOrDisappear(mLoadMoreResId, mLoadMoreViewEnabled);
    }


    private void displayOrDisappear(int resId, boolean enabled) {
        if (!(getAdapter() instanceof BaseAdapter)) return;
        BaseAdapter adapter = (BaseAdapter) getAdapter();
        adapter.showExtra(resId, enabled);
    }

    public void setAdapterWithLoading(RecyclerView.Adapter adapter) {
        if (adapter instanceof BaseAdapter) {
            BaseAdapter baseAdapter = (BaseAdapter) adapter;
            subscribeWithAdapter(baseAdapter);
        }
        displayLoadingAndResetStatus();
        setAdapter(adapter);
    }

    private void subscribeWithAdapter(BaseAdapter adapter) {
        adapter.registerObserver(mObserver);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private class DataSetObserver implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            Bridge type = (Bridge) arg;
            type.doSomething(OptimizedRecyclerView.this);
        }
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //当滚动到最后一个item时,自动加载更多
            if (isLastItem(recyclerView)) {
                Log.d("hon", "onScrollStateChanged: ");
                showAutoLoadMoreIfEnabled();
            }
        }

        private boolean isLastItem(RecyclerView recyclerView) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItemPosition = getLastVisibleItemPosition(layoutManager);
            Log.d("hon", "visibleItemCount: "+visibleItemCount);
            Log.d("hon", "totalItemCount: "+totalItemCount);
            Log.d("hon", "lastVisibleItemPosition: "+lastVisibleItemPosition);
            return visibleItemCount > 0 && lastVisibleItemPosition >= totalItemCount - 1 &&
                    totalItemCount >= visibleItemCount;
        }

        private int getLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            return lastVisibleItemPosition;
        }

        private int findMax(int[] lastPositions) {
            int max = lastPositions[0];
            for (int value : lastPositions) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }
    }
}
