
package com.sjy.ttclub.framework;

import android.view.View;

public interface ITabView {

    public static final int TAB_TO_SHOW = 0;
    public static final int TAB_TO_HIDE = 1;
    public static final int TAB_AFTER_PUSH_IN = 2;

    public String getTitle();

    /**
     * 如果需要延迟加载（在STATE_AFTER_PUSH_IN后）会回调此接口
     */
    public void onPrepareContentView();

    public View getTabView();

    /**
     * 
     * @param tabChangedFlag
     *            TAB_TO_SHOW / TAB_TO_HIDE
     */
    public void onTabChanged(int tabChangedFlag);

}
