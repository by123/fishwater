package com.sjy.ttclub.photopicker;

/**
 * Created by linhz on 2015/12/25.
 * Email: linhaizhong@ta2she.com
 */
public class PhotoInfo {
    public static final int ALBUM_ID_ALL = 0;
    public static final int ALBUM_ID_SELECTED = 1;

    public int bucketId = ALBUM_ID_ALL;
    public String bucketName;
    public String path;
    public int bucketSize;
    public long size;
    public boolean isSelected = false;
}
