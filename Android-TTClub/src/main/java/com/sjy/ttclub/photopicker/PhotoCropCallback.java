package com.sjy.ttclub.photopicker;

/**
 * Created by linhz on 2015/12/28.
 * Email: linhaizhong@ta2she.com
 */
public interface PhotoCropCallback {

    void onPhotoCropResult(String path);

    String getOrigPhotoPath();
}
