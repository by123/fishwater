package com.sjy.ttclub.bean.community;

import java.io.Serializable;

public class ImageCard implements Serializable {
    private int imageId;
    private int cardId;
    private String imageUrl;
    private String imageDescription;
    private int imageWidth;
    private int imageHeight;
    private String imageContent;
    //是否有图片：0、无；1、有
    private int hasImage;
    //当前图片的序号
    private int imageSortId;

    public int getImageSortId() {
        return imageSortId;
    }

    public void setImageSortId(int imageSortId) {
        this.imageSortId = imageSortId;
    }

    public int getHasImage() {
        return hasImage;
    }

    public void setHasImage(int hasImage) {
        this.hasImage = hasImage;
    }

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

}
