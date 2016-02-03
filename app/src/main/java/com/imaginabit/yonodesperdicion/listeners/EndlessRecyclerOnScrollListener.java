package com.imaginabit.yonodesperdicion.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * based on
 * http://android-pratap.blogspot.com.es/2015/01/endless-recyclerview-onscrolllistener.html
 */

public abstract class EndlessRecyclerOnScrollListener extends
        RecyclerView.OnScrollListener {
    private static final String TAG = "EndlessRecyclerOnSV";

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1; //start with page 1
    private int current_scroll;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(
            LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading
                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            Log.d(TAG, "onScrolled:  End has been reached");


            // Do something
            current_page++;

            onLoadMore(current_page,dy);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page, int current_scroll);
}