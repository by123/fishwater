
package com.sjy.ttclub.widget.dialog;

import android.view.KeyEvent;

public interface IDialogOnKeyListener {
    void onDialogKey(GenericDialog dialog, int viewId, KeyEvent event,
                     Object extra);
}
