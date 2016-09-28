package com.sjy.ttclub.bean.community;

/**
 * @author: 张武林
 * @类 说   明: 管理操作按钮
 * @创建时间：${date}
 */
public class TitleButtonBean {

    private boolean hostSelected;
    private boolean seeSelected;
    private boolean hotSelected;

    public TitleButtonBean(boolean hostSelected, boolean seeSelected, boolean hotSelected) {
        this.hostSelected = hostSelected;
        this.seeSelected = seeSelected;
        this.hotSelected = hotSelected;
    }

    public boolean isHostSelected() {
        return hostSelected;
    }

    public void setHostSelected(boolean hostSelected) {
        this.hostSelected = hostSelected;
    }

    public boolean isHotSelected() {
        return hotSelected;
    }

    public void setHotSelected(boolean hotSelected) {
        this.hotSelected = hotSelected;
    }

    public boolean isSeeSelected() {
        return seeSelected;
    }

    public void setSeeSelected(boolean seeSelected) {
        this.seeSelected = seeSelected;
    }
}
