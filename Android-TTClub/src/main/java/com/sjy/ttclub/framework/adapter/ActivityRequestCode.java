package com.sjy.ttclub.framework.adapter;

/**
 * Created by linhz on 2015/12/28.
 * Email: linhaizhong@ta2she.com
 */
public class ActivityRequestCode {

    private static byte sBaseId = 10;

    private static byte generateId() {
        return sBaseId++;
    }

    public static final int ACCOUNT_PHOTO_TAKE_PHOTO = generateId();
    public static final int ACCOUNT_PHOTO_GALLERY = generateId();

    public static final int FEEDBACK_PHOTO_TAKE_PHOTO = generateId();



}
