package com.sjy.ttclub.emoji;


public interface IView {
    void onItemClick(EmoticonBean bean);

    void onItemDisplay(EmoticonBean bean);

    void onPageChangeTo(int position);
}
