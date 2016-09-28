
package com.sjy.ttclub.widget.dialog;


public interface IDialogCmdListener {
    /**
     * 对话框处理回调函数接口
     * 
     * @param dialog
     *            对话框本身，可以用来findViewById()
     * @param viewId
     *            触发事件的元件id
     * @param cmd
     *            触发的事件类型(类型可能为自定义、MotionEvent和KeyEvent)
     * @param extra
     *            附加信息(通常为null)
     */
    boolean onDialogCmd(GenericDialog dialog, int viewId, int cmd, Object extra);
}
