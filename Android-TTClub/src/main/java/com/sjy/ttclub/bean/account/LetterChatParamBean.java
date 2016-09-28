package com.sjy.ttclub.bean.account;

/**
 * Created by gangqing on 2015/12/28.
 * Email:denggangqing@ta2she.com
 */
public class LetterChatParamBean {
    private MessageDialogs letter;
    private String imageUrl;
    private String title;
    private String price;
    private String spId;
    private String time;
    private int type;    //订单时需要传这个类型，12

    public MessageDialogs getMessageDialogs() {
        return letter;
    }

    public void setLetter(MessageDialogs letter) {
        this.letter = letter;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSpId() {
        return spId;
    }

    public void setSpId(String spId) {
        this.spId = spId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
