
package com.sjy.ttclub.photopicker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

import com.lsym.ttclub.BuildConfig;
import com.sjy.ttclub.util.ThreadManager;
import com.sjy.ttclub.util.file.FileUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;


/**
 * Created by linhz on 2015/12/25.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class PhotoLoader {

    private static final long MIN_SIZE = 10 * 1024;

    private static final String TAG = "PhotoLoader";
    private static final String[] PROJECTION_BUCKET = {ImageColumns.DATA,
            ImageColumns.BUCKET_ID, ImageColumns.BUCKET_DISPLAY_NAME,
            ImageColumns.SIZE};

    private static final int INDEX_DATA = 0;
    private static final int INDEX_BUCKET_ID = 1;
    private static final int INDEX_BUCKET_NAME = 2;
    private static final int INDEX_SIZE = 3;

    private static final String BUCKET_ORDER_BY = ImageColumns.DATE_TAKEN
            + " DESC";

    private ArrayList<WeakReference<IDataChangeObserver>> mDataObserverList = new
            ArrayList<WeakReference<IDataChangeObserver>>();

    private BucketManager mBucketManager;

    private ImageContentObserver mImageContentObserver;

    private boolean mIsLoaded = false;

    public PhotoLoader() {
        mBucketManager = new BucketManager();
    }

    public void registerDataObserver(IDataChangeObserver observer) {
        if (observer == null) {
            return;
        }
        synchronized (mDataObserverList) {
            for (WeakReference<IDataChangeObserver> ref : mDataObserverList) {
                IDataChangeObserver tmp = ref.get();
                if (tmp == observer) {
                    return;
                }
            }

            mDataObserverList.add(new WeakReference<IDataChangeObserver>(
                    observer));
        }
    }

    public void unregisterDataObserver(IDataChangeObserver observer) {
        if (observer == null) {
            return;
        }
        synchronized (mDataObserverList) {
            for (WeakReference<IDataChangeObserver> ref : mDataObserverList) {
                IDataChangeObserver tmp = ref.get();
                if (tmp == observer) {
                    mDataObserverList.remove(tmp);
                    return;
                }
            }
        }
    }


    private void notifyDataChanged(final Context context) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                synchronized (mDataObserverList) {
                    for (WeakReference<IDataChangeObserver> ref : mDataObserverList) {
                        IDataChangeObserver tmp = ref.get();
                        if (tmp != null) {
                            tmp.onDataChanged();
                        }

                    }
                }
            }
        };

        if (ThreadManager.isMainThread()) {
            runnable.run();
        } else {
            ThreadManager.post(ThreadManager.THREAD_UI, runnable);
        }
    }

    public void loadAllPhotos(Context context, IDataLoadCallback callback) {
        if (mIsLoaded) {
            if (callback != null) {
                callback.onDataLoadFinished();
            }
            return;
        }
        startLoadAllPhotos(context, callback);
        mIsLoaded = true;
    }

    private void startLoadAllPhotos(Context context,
                                    final IDataLoadCallback callback) {

        final Context appContext = context.getApplicationContext();

        ThreadManager.post(ThreadManager.THREAD_WORK, new Runnable() {

            @Override
            public void run() {
                ArrayList<PhotoInfo> allList = loadPhotosFromSystemDataBase(appContext);
                ArrayList<PhotoInfo> selectedList = mBucketManager.getSelectedPhotos(appContext);
                final ArrayList<PhotoInfo> delList = new ArrayList<PhotoInfo>();
                ArrayList<PhotoInfo> resultList = correctSelectedList(allList,
                        selectedList, delList);
                mBucketManager.clear();
                mBucketManager.addPhotoInfoList(allList);
                mBucketManager.addSelectedInfoList(resultList);
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (callback == null) {
                    notifyDataChanged(appContext);
                } else {
                    callback.onDataLoadFinished();
                }

            }
        });

    }

    private ArrayList<PhotoInfo> loadPhotosFromSystemDataBase(Context context) {
        final ContentResolver resolver = context.getContentResolver();
        final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ArrayList<PhotoInfo> list = new ArrayList<PhotoInfo>();
        try {
            Cursor cursor = resolver.query(uri, PROJECTION_BUCKET, null, null,
                    BUCKET_ORDER_BY);
            if (cursor == null) {
                return list;
            }
            PhotoInfo info = null;
            while (cursor.moveToNext()) {
                info = new PhotoInfo();
                info.path = cursor.getString(INDEX_DATA);
                if (!FileUtils.isFileExists(info.path)) {
                    Log.w(TAG, "photo file donot exist, skip it!!");
                    continue;
                }
                info.bucketId = cursor.getInt(INDEX_BUCKET_ID);
                info.bucketName = cursor.getString(INDEX_BUCKET_NAME);
                info.size = cursor.getLong(INDEX_SIZE);
                list.add(info);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private ArrayList<PhotoInfo> correctSelectedList(
            ArrayList<PhotoInfo> allList, ArrayList<PhotoInfo> selectedList,
            ArrayList<PhotoInfo> delList) {
        ArrayList<PhotoInfo> resultList = new ArrayList<PhotoInfo>();
        if (selectedList.isEmpty()) {
            return resultList;
        }
        ArrayList<PhotoInfo> tmpAllList = (ArrayList<PhotoInfo>) allList
                .clone();
        for (PhotoInfo selectedInfo : selectedList) {
            boolean isFound = false;
            for (PhotoInfo info : tmpAllList) {
                if (selectedInfo.path.equals(info.path)) {
                    isFound = true;
                    info.isSelected = true;
                    resultList.add(info);
                    tmpAllList.remove(info);
                    break;
                }
            }
            if (!isFound) {
                delList.add(selectedInfo);
            }
        }
        return resultList;
    }

    public ArrayList<PhotoInfo> getAllBuckets(Context context) {
        return mBucketManager.getAllBuckets(context);
    }

    public ArrayList<PhotoInfo> getPhotosByBucketId(Context context, int id) {
        ArrayList<PhotoInfo> list = null;
        if (id == PhotoInfo.ALBUM_ID_ALL) {
            list = mBucketManager.getAllPhotos(context);
        } else if (id == PhotoInfo.ALBUM_ID_SELECTED) {
            list = mBucketManager.getSelectedPhotos(context);
        } else {
            list = mBucketManager.getPhotosByBucketId(context, id);
        }

        return list;
    }

    public ArrayList<PhotoInfo> getAllPhotos(Context context) {
        return mBucketManager.getAllPhotos(context);
    }

    public ArrayList<PhotoInfo> getSelectedPhotos(Context context) {
        return mBucketManager.getSelectedPhotos(context);
    }

    public void addSelectedPhoto(Context context, PhotoInfo info) {
        addSelectedPhoto(context, info, true);
    }

    public void addSelectedPhoto(Context context, PhotoInfo info, boolean notify) {
        info.isSelected = true;
        mBucketManager.addSelectedInfo(info);
        if (notify) {
            notifyDataChanged(context.getApplicationContext());
        }
    }

    public void removeSelectedPhoto(Context context, PhotoInfo info) {
        removeSelectedPhoto(context, info, true);
    }

    public void removeSelectedPhoto(Context context, PhotoInfo info,
                                    boolean notify) {
        info.isSelected = false;
        mBucketManager.removeSelectedInfo(info);
        if (notify) {
            notifyDataChanged(context.getApplicationContext());
        }
    }

    public void registerContentObserver(Context context) {
        if (mImageContentObserver != null) {
            return;
        }

        if (mImageContentObserver == null) {
            mImageContentObserver = new ImageContentObserver(context,
                    new Handler(Looper.getMainLooper()));
        }
        mImageContentObserver.registerObserver();
    }

    public void unregisterContentObserver(Context context) {
        if (mImageContentObserver != null) {
            mImageContentObserver.unregisterObserver();
        }
        mImageContentObserver = null;
    }

    private class ImageContentObserver extends ContentObserver {
        private Context mContext;
        private Handler mHandler;

        public ImageContentObserver(Context context, Handler handler) {
            super(handler);
            mContext = context.getApplicationContext();
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            tryReloadAllPhotos();
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Content observer onChange selfChange : "
                        + selfChange);
            }
        }

        public void registerObserver() {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Content observer registerObserver");
            }
            final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            mContext.getContentResolver().registerContentObserver(uri, true,
                    ImageContentObserver.this);
        }

        public void unregisterObserver() {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Content observer unregisterObserver");
            }
            mContext.getContentResolver().unregisterContentObserver(
                    ImageContentObserver.this);
        }

        private Runnable mReloadPhotosRunnable = new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "tryReloadAllPhotos");
                startLoadAllPhotos(mContext, null);
            }
        };

        private void tryReloadAllPhotos() {
            // 这里延时去处理，是为了防止onChange有多次回调，导致不必要的加载与刷新
            mHandler.removeCallbacks(mReloadPhotosRunnable);
            mHandler.postDelayed(mReloadPhotosRunnable, 1000);
        }

    }

    private class BucketManager {
        private final Object sLock = new Object();
        private LinkedHashMap<Integer, ArrayList<PhotoInfo>> mDataMap = new LinkedHashMap<Integer,
                ArrayList<PhotoInfo>>();
        private ArrayList<PhotoInfo> mDataList = new ArrayList<PhotoInfo>();
        private ArrayList<PhotoInfo> mSelectedList = new ArrayList<PhotoInfo>();

        public void addPhotoInfo(PhotoInfo info) {
            if (info == null) {
                return;
            }
            synchronized (sLock) {
                ArrayList<PhotoInfo> arrayList = mDataMap.get(info.bucketId);
                if (arrayList == null) {
                    arrayList = new ArrayList<PhotoInfo>();
                    mDataMap.put(info.bucketId, arrayList);
                }
                arrayList.add(info);
            }
        }

        public void addPhotoInfoList(ArrayList<PhotoInfo> list) {
            if (list == null) {
                return;
            }
            synchronized (sLock) {
                mDataList.addAll(list);
                for (PhotoInfo info : list) {
                    ArrayList<PhotoInfo> arrayList = mDataMap
                            .get(info.bucketId);
                    if (arrayList == null) {
                        arrayList = new ArrayList<PhotoInfo>();
                        mDataMap.put(info.bucketId, arrayList);
                    }
                    arrayList.add(info);
                }
            }
        }

        public void addSelectedInfoList(ArrayList<PhotoInfo> list) {
            if (list == null) {
                return;
            }
            synchronized (sLock) {
                mSelectedList.clear();
                mSelectedList.addAll(list);
            }
        }

        public void addSelectedInfo(PhotoInfo info) {
            if (info == null || info.path == null) {
                return;
            }
            synchronized (sLock) {
                if (mSelectedList.contains(info)) {
                    return;
                }
                for (PhotoInfo tmp : mSelectedList) {
                    if (info.path.equals(tmp.path)) {
                        return;
                    }
                }
                mSelectedList.add(info);
            }
        }

        public void removeSelectedInfo(PhotoInfo info) {
            if (info == null || info.path == null) {
                return;
            }
            synchronized (sLock) {
                boolean removed = mSelectedList.remove(info);
                if (!removed) {
                    for (PhotoInfo tmp : mSelectedList) {
                        if (info.path.equals(tmp.path)) {
                            mSelectedList.remove(tmp);
                            return;
                        }
                    }
                }
            }
        }

        public void clear() {
            synchronized (sLock) {
                mDataMap.clear();
                mDataList.clear();
                mSelectedList.clear();
            }
        }

        public ArrayList<PhotoInfo> getPhotosByBucketId(Context context, int id) {
            synchronized (sLock) {
                ArrayList<PhotoInfo> resultList = new ArrayList<PhotoInfo>();
                ArrayList<PhotoInfo> list = mDataMap.get(id);
                if (list != null && !list.isEmpty()) {
                    long size = MIN_SIZE;
                    for (PhotoInfo info : list) {
                        if (info.size >= size) {
                            resultList.add(info);
                        }
                    }
                }
                return resultList;
            }
        }

        public ArrayList<PhotoInfo> getAllPhotos(Context context) {
            synchronized (sLock) {
                ArrayList<PhotoInfo> resultList = new ArrayList<PhotoInfo>();
                if (!mDataList.isEmpty()) {
                    long size = MIN_SIZE;
                    for (PhotoInfo info : mDataList) {
                        if (info.size >= size) {
                            resultList.add(info);
                        }
                    }
                }
                return resultList;
            }
        }

        public ArrayList<PhotoInfo> getAllBuckets(Context context) {
            synchronized (sLock) {
                Iterator<ArrayList<PhotoInfo>> it = mDataMap.values()
                        .iterator();
                ArrayList<PhotoInfo> bucketList = new ArrayList<PhotoInfo>();
                ArrayList<PhotoInfo> tmpList = null;
                long size = MIN_SIZE;
                while (it.hasNext()) {
                    tmpList = it.next();
                    int bucketSize = 0;
                    if (tmpList != null && !tmpList.isEmpty()) {
                        PhotoInfo info = null;
                        for (PhotoInfo tmpInfo : tmpList) {
                            if (tmpInfo.size >= size) {
                                if (info == null) {
                                    info = tmpInfo;
                                }
                                bucketSize++;
                            }
                        }
                        if (info != null) {
                            info.bucketSize = bucketSize;
                            bucketList.add(info);
                        }
                    }
                }
                return bucketList;
            }
        }

        public ArrayList<PhotoInfo> getSelectedPhotos(Context context) {
            synchronized (sLock) {
                ArrayList<PhotoInfo> resultList = new ArrayList<PhotoInfo>();
                if (!mSelectedList.isEmpty()) {
                    long size = MIN_SIZE;
                    for (PhotoInfo info : mSelectedList) {
                        if (info.size >= size) {
                            resultList.add(info);
                        }
                    }
                }
                return resultList;
            }

        }
    }

    public interface IDataChangeObserver {
        void onDataChanged();
    }

    public interface IDataLoadCallback {
        void onDataLoadFinished();
    }
}
