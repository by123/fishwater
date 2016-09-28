
package com.sjy.ttclub.widget;

public interface OnTabChangedListener {

    public void onTabChangeStart(int newTabIndex, int oldTabIndex);

    public void onTabChanged(int newTabIndex, int oldTabIndex);

    public void onTabChangedByTitle(int tabIndex);

}
