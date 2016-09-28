package com.sjy.ttclub.shopping.utils;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cundong.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.sjy.ttclub.shopping.widget.ShoppingLoadingFooter;

/**
 * update by zhxu on 2015/11/9.
 */
public class ShoppingRecyclerViewStateUtils {

    public static void setFooterViewState(Activity instance, RecyclerView recyclerView, int pageSize, ShoppingLoadingFooter.State state) {
        if (instance == null || instance.isFinishing()) {
            return;
        }

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter == null || !(outerAdapter instanceof HeaderAndFooterRecyclerViewAdapter)) {
            return;
        }

        HeaderAndFooterRecyclerViewAdapter headerAndFooterAdapter = (HeaderAndFooterRecyclerViewAdapter) outerAdapter;
        ShoppingLoadingFooter footerView;

        if (headerAndFooterAdapter.getFooterViewsCount() > 0) {
            footerView = (ShoppingLoadingFooter) headerAndFooterAdapter.getFooterView();
//            recyclerView.scrollToPosition(headerAndFooterAdapter.getItemCount() - 1);
        } else {
            footerView = new ShoppingLoadingFooter(instance);
            headerAndFooterAdapter.addFooterView(footerView);
//            recyclerView.scrollToPosition(headerAndFooterAdapter.getItemCount() - 1);
        }

        int itemCount = headerAndFooterAdapter.getInnerAdapter().getItemCount();
        if (itemCount < pageSize) {
            footerView.setState(ShoppingLoadingFooter.State.TheEnd);
        } else {
            footerView.setState(state);
        }
    }

    public static ShoppingLoadingFooter.State getFooterViewState(RecyclerView recyclerView) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof HeaderAndFooterRecyclerViewAdapter) {
            if (((HeaderAndFooterRecyclerViewAdapter) outerAdapter).getFooterViewsCount() > 0) {
                ShoppingLoadingFooter footerView = (ShoppingLoadingFooter) ((HeaderAndFooterRecyclerViewAdapter) outerAdapter).getFooterView();
                return footerView.getState();
            }
        }
        return ShoppingLoadingFooter.State.Normal;
    }

    public static void setFooterViewState(RecyclerView recyclerView, ShoppingLoadingFooter.State state) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof HeaderAndFooterRecyclerViewAdapter) {
            if (((HeaderAndFooterRecyclerViewAdapter) outerAdapter).getFooterViewsCount() > 0) {
                ShoppingLoadingFooter footerView = (ShoppingLoadingFooter) ((HeaderAndFooterRecyclerViewAdapter) outerAdapter).getFooterView();
                footerView.setState(state);
            }
        }
    }
}
