package com.sjy.ttclub.loadmore;

import android.widget.TextView;

public interface LoadMoreUIHandler {

	public void resetLoading(LoadMoreContainer container);
	
    public void onLoading(LoadMoreContainer container);

    public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore);

    public void onWaitToLoadMore(LoadMoreContainer container);

    public void onLoadError(LoadMoreContainer container, int errorCode, String errorMessage);

}