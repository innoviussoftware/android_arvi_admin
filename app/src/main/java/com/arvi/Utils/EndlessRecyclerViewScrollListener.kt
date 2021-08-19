package com.sam.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


abstract class EndlessRecyclerViewScrollListener(var rvLayoutManager: GridLayoutManager) : RecyclerView.OnScrollListener() {

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold = 5

    // The current offset index of data you have loaded
    private var currentPage = 0

    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0

    // True if we are still waiting for the last set of data to load.
    private var loading = true

    // Sets the starting page index
    private val startingPageIndex = 0

    var mLayoutManager: RecyclerView.LayoutManager? = null

    open fun EndlessRecyclerViewScrollListener(layoutManager: LinearLayoutManager?) {
        mLayoutManager = layoutManager
    }

    open fun EndlessRecyclerViewScrollListener(layoutManager: GridLayoutManager) {
        mLayoutManager = layoutManager
        visibleThreshold = visibleThreshold * layoutManager.spanCount
    }

    open fun EndlessRecyclerViewScrollListener(layoutManager: StaggeredGridLayoutManager) {
        mLayoutManager = layoutManager
        visibleThreshold = visibleThreshold * layoutManager.spanCount
    }

    open fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override  fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        this.mLayoutManager = rvLayoutManager

        var lastVisibleItemPosition = 0
        val totalItemCount = mLayoutManager!!.itemCount
        if (mLayoutManager is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions =
                (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
        } else if (mLayoutManager is GridLayoutManager) {
            lastVisibleItemPosition =
                (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
        } else if (mLayoutManager is LinearLayoutManager) {
            lastVisibleItemPosition =
                (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        // modified by me
        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            currentPage++
            if (dy > 0) {
                onLoadMore(currentPage, totalItemCount, view)
                loading = true
            }
        }

        // predefined code
//        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
//            currentPage++;
//            onLoadMore(currentPage, totalItemCount, view);
//            loading = true;
//        }
    }

    open fun resetState() {
        currentPage = startingPageIndex
        previousTotalItemCount = 0
        loading = true
    }

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
}