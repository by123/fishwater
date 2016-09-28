
package com.sjy.ttclub.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

import com.lsym.ttclub.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linhz on 2015/12/25.
 * Email: linhaizhong@ta2she.com
 */

/**
 * 注意：在使用结束后，一定要调用clear cache!!!!!否则造成内存泄露！！！！
 */
public class PhotoCacheHelper {
    private static final int MAX_CACHE = 80;
    private static final int MIN_CACHE = 40;
    private static Drawable sDefaultIcon;

    private Context mContext;

    private LruCache<String, ThumbBitmapItem> mThumbnailCache;
    private HashMap<String, ArrayList<IThumbnailBitmapCallback>> mDecodingCallbackMap = new HashMap<String,
            ArrayList<IThumbnailBitmapCallback>>();

    public static Drawable getDefaultIcon() {
        if (sDefaultIcon == null) {
            sDefaultIcon = new ColorDrawable(ResourceHelper.getColor(R.color.homepage_test_content_mask));
        }
        return sDefaultIcon;
    }

    public PhotoCacheHelper(Context context) {
        mContext = context;
    }


    /**
     * must call in ui thread!!
     *
     * @param path
     * @param size
     * @param callback
     * @return
     */
    public Bitmap getThumbnailBitmap(final String path, final int size,
                                     final IThumbnailBitmapCallback callback) {
        return getThumbnailBitmap(path, size, callback, true);
    }

    /**
     * must call in ui thread!!
     *
     * @param path
     * @param size
     * @param callback
     * @return
     */
    public Bitmap getThumbnailBitmap(final String path, final int size,
                                     final IThumbnailBitmapCallback callback, boolean cache) {
        if (callback == null) {
            return null;
        }
        ensureCache();

        Bitmap bitmap = null;
        ThumbBitmapItem item = mThumbnailCache.get(path);
        if (item != null) {
            if (item.isDecoded()) {
                if (item.bitmap != null) {
                    bitmap = item.bitmap;
                } else {
                    item.resetState();
                }
            }
        } else {
            item = new ThumbBitmapItem();
            if (cache) {
                mThumbnailCache.put(path, item);
            }
        }
        final ThumbBitmapItem finalItem = item;
        if (bitmap == null) {
            if (finalItem.isDecoding()) {
                // 如果在decoding，将callback保存，等等待decode结束后，通知过去
                addDecodingCallback(path, callback);
                return bitmap;
            }
            final ValueHolder holder = new ValueHolder();
            finalItem.setDecoding();
            ThreadManager.post(ThreadManager.THREAD_WORK, new Runnable() {

                @Override
                public void run() {
                    Bitmap value = BitmapUtil.createBitmapThumbnail(path, size, size);
                    holder.bitmap = value;
                }
            }, new Runnable() {

                @Override
                public void run() {
                    Bitmap result = null;
                    finalItem.setDecoded();
                    if (holder.bitmap != null) {
                        result = holder.bitmap;
                        finalItem.bitmap = result;
                    }
                    callback.onResult(result, path);
                    notifyDecodeFinished(path, result);
                }
            });
        } else {
            callback.onResult(bitmap, path);
        }
        return bitmap;
    }

    private void ensureCache() {
        if (mThumbnailCache == null || mThumbnailCache.size() <= 0) {
            int cache = HardwareUtil.isLowDevice() ? MIN_CACHE : MAX_CACHE;
            mThumbnailCache = new LruCache<String, ThumbBitmapItem>(cache);
        }
    }

    public void clearCache() {
        if (mThumbnailCache != null && mThumbnailCache.size() > 0) {
            mThumbnailCache.evictAll();
            CommonUtils.gc();
        }
        mThumbnailCache = null;
        synchronized (mDecodingCallbackMap) {
            mDecodingCallbackMap.clear();
        }

    }

    private void notifyDecodeFinished(String path, Bitmap bitmap) {
        if (path == null) {
            return;
        }
        synchronized (mDecodingCallbackMap) {
            ArrayList<IThumbnailBitmapCallback> list = mDecodingCallbackMap
                    .remove(path);

            if (list == null || list.isEmpty()) {
                return;
            }
            for (IThumbnailBitmapCallback callback : list) {
                if (callback != null) {
                    callback.onResult(bitmap, path);
                }
            }
            list.clear();
        }

    }

    private void addDecodingCallback(String path,
                                     IThumbnailBitmapCallback callback) {
        if (path == null || callback == null) {
            return;
        }
        synchronized (mDecodingCallbackMap) {
            ArrayList<IThumbnailBitmapCallback> list = mDecodingCallbackMap
                    .get(path);
            if (list == null) {
                list = new ArrayList<IThumbnailBitmapCallback>();
                mDecodingCallbackMap.put(path, list);
            }
            list.add(callback);
        }
    }

    private static class ThumbBitmapItem {
        public static final int STATE_NONE = 0;
        public static final int STATE_DECODING = 1;
        public static final int STATE_DECODED = 2;
        public Bitmap bitmap;
        public int state = STATE_NONE;

        public boolean isDecoding() {
            return state == STATE_DECODING;
        }

        public boolean isDecoded() {
            return state == STATE_DECODED;
        }

        public void setDecoded() {
            state = STATE_DECODED;
        }

        public void setDecoding() {
            state = STATE_DECODING;
        }

        public void resetState() {
            state = STATE_NONE;
        }

        public void recycle() {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
            state = STATE_NONE;
        }
    }

    public interface IThumbnailBitmapCallback {
        void onResult(Bitmap bitmap, String path);
    }
}
