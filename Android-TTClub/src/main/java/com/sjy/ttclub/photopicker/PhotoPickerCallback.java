package com.sjy.ttclub.photopicker;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/12/26.
 * Email: linhaizhong@ta2she.com
 */
public interface PhotoPickerCallback {

    void onPhotoPickResult(ArrayList<PhotoInfo> resultList);

    int getMaxPhotoNum();

    ArrayList<String> getPickedPhotos();
}
