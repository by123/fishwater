package com.sjy.ttclub.photopicker;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.framework.DefaultWindow;
import com.sjy.ttclub.framework.IDefaultWindowCallBacks;
import com.sjy.ttclub.framework.adapter.MenuItemIdDef;
import com.sjy.ttclub.framework.ui.DefaultTitleBar;
import com.sjy.ttclub.framework.ui.TitleBarActionItem;
import com.sjy.ttclub.util.HardwareUtil;
import com.sjy.ttclub.util.PhotoCacheHelper;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;

/**
 * Created by linhz on 2015/12/25.
 * Email: linhaizhong@ta2she.com
 */
/*package*/ class PhotoPickerWindow extends DefaultWindow implements PhotoLoader.IDataChangeObserver,
        PhotoThumbItemView.IPhotoStateChangeListener {

    private PhotoPickerCallback mResultCallback;
    private GridView mGridView;
    private PhotoGridViewAdapter mAdapter;
    private TextView mNoPhotoView;
    private PhotoLoader mPhotoLoader;

    private ArrayList<PhotoInfo> mPhotoList;

    private PhotoCacheHelper mCacheHelper;

    private ArrayList<String> mPickedList = new ArrayList<String>();

    public PhotoPickerWindow(Context context, IDefaultWindowCallBacks callBacks) {
        super(context, callBacks);
        setTitle(R.string.photo_picker_title);
        initActionBar();
        onCreateContent();
    }

    public void setResultCallback(PhotoPickerCallback callback) {
        mResultCallback = callback;
        mAdapter.setMaxNum(callback.getMaxPhotoNum());
        ArrayList<String> pickedList = callback.getPickedPhotos();
        mPickedList.clear();
        if (pickedList != null) {
            mPickedList.addAll(pickedList);
        }
    }

    private void initActionBar() {
        ArrayList<TitleBarActionItem> actionList = new ArrayList<TitleBarActionItem>(1);
        TitleBarActionItem item = new TitleBarActionItem(getContext());
        item.setText(ResourceHelper.getString(R.string.photo_picker_finish));
        item.setItemId(MenuItemIdDef.TITLEBAR_COMMIT);
        actionList.add(item);

        DefaultTitleBar titleBar = (DefaultTitleBar) getTitleBar();
        titleBar.setActionItems(actionList);
    }

    private View onCreateContent() {
        mPhotoLoader = new PhotoLoader();
        mCacheHelper = new PhotoCacheHelper(getContext());
        View parentView = View.inflate(getContext(), R.layout.photo_picker_layout, null);
        initViews(parentView);

        getBaseLayer().addView(parentView, getContentLPForBaseLayer());
        return parentView;
    }

    private void initViews(View view) {
        mPhotoList = new ArrayList<PhotoInfo>();
        int gap = ResourceHelper.getDimen(R.dimen.photo_picker_gap);
        int columns = ResourceHelper.getInteger(R.integer.photo_picker_column_num);
        int remainWidth = HardwareUtil.windowWidth - gap * columns;
        int columnsSize = remainWidth / columns;
        mAdapter = new PhotoGridViewAdapter(getContext(), mPhotoList);
        mAdapter.setCacheHelper(mCacheHelper);
        mAdapter.setColumnsSize(columnsSize);
        mAdapter.setPhotoStateChangeListener(this);

        mNoPhotoView = (TextView) view.findViewById(R.id.photo_picker_no_photo);
        mGridView = (GridView) view.findViewById(R.id.photo_picker_gridview);
        mGridView.setAdapter(mAdapter);
        mGridView.setVerticalScrollBarEnabled(false);
        mGridView.setVerticalFadingEdgeEnabled(false);
        mGridView.setPadding(0, 0, 0, 0);

    }

    @Override
    public void onTitleBarActionItemClick(int itemId) {
        super.onTitleBarActionItemClick(itemId);
        if (itemId == MenuItemIdDef.TITLEBAR_COMMIT) {
            mResultCallback.onPhotoPickResult(mPhotoLoader.getSelectedPhotos(getContext()));
            mCallBacks.onWindowExitEvent(this, true);
        }
    }

    @Override
    protected void onWindowStateChange(int stateFlag) {
        super.onWindowStateChange(stateFlag);
        if (stateFlag == STATE_AFTER_PUSH_IN) {
            mPhotoLoader.loadAllPhotos(getContext(), new PhotoLoader.IDataLoadCallback() {
                @Override
                public void onDataLoadFinished() {
                    updateViews(true);
                }
            });
        } else if (stateFlag == STATE_ON_DETACH) {
            mPhotoLoader.unregisterContentObserver(getContext());
            mPhotoLoader.unregisterDataObserver(this);
            mCacheHelper.clearCache();
        } else if (stateFlag == STATE_ON_ATTACH) {
            mPhotoLoader.registerContentObserver(getContext());
            mPhotoLoader.registerDataObserver(this);
        }
    }

    private void updateViews(boolean firstLoad) {
        ArrayList<PhotoInfo> allList = mPhotoLoader.getAllPhotos(getContext());
        if (allList.isEmpty()) {
            mGridView.setVisibility(View.INVISIBLE);
            mNoPhotoView.setVisibility(View.VISIBLE);
            mNoPhotoView.setText(ResourceHelper.getString(R.string.photo_picker_no_photos));
        } else {
            mGridView.setVisibility(View.VISIBLE);
            mNoPhotoView.setVisibility(View.GONE);
        }
        if (firstLoad) {
            for (String path : mPickedList) {
                for (PhotoInfo info : allList) {
                    if (path.equals(info.path)) {
                        info.isSelected = true;
                        mPhotoLoader.addSelectedPhoto(getContext(), info, false);
                    }
                }
            }
        }
        mPhotoList.clear();
        mPhotoList.addAll(allList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataChanged() {
        updateViews(false);
    }

    @Override
    public void onPhotoStateChanged(PhotoInfo info) {
        if (info.isSelected) {
            mPhotoLoader.addSelectedPhoto(getContext(), info, false);
        } else {
            mPhotoLoader.removeSelectedPhoto(getContext(), info, false);
        }
    }

    @Override
    public int getSelectedNum() {
        return mPhotoLoader.getSelectedPhotos(getContext()).size();
    }
}
