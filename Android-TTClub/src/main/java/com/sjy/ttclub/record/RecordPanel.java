package com.sjy.ttclub.record;

import android.content.Context;
import android.view.View;

import com.lsym.ttclub.R;
import com.sjy.ttclub.widget.BasePanel;

/**
 * Created by linhz on 2016/1/4.
 * Email: linhaizhong@ta2she.com
 */
public class RecordPanel extends BasePanel {

    public RecordPanel(Context context) {
        super(context);
    }

    @Override
    protected View onCreateContentView() {
        View contentView = View.inflate(mContext, R.layout.record_window, null);
        return contentView;
    }
}
