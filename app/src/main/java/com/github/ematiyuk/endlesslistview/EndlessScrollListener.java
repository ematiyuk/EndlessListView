package com.github.ematiyuk.endlesslistview;

import android.widget.AbsListView;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
    // The minimum number of items to have below current scroll position
    // before loading more.
    // If threshold equals to 0, the user has to scroll to the very end of the list
    // in order to load more items.
    private int visibleThreshold = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // The total number of items in the dataset after the last load.
    private int previousTotalItemCount = 0;

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemsCount)
    {
        // If it's still loading, we check to see if the dataset count has
        // changed (by comparison with "totalItemsCount"), if so we conclude
        // it has finished loading and update total item count.
        if (loading && (totalItemsCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemsCount;
        }

        // If it isn't currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemsCount ) {
            loading = onLoadMore(totalItemsCount);
        }
    }

    // Defines the process for actually loading more data based on totalItemsCount as an offset
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    public abstract boolean onLoadMore(int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}
