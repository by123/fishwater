
package com.sjy.ttclub.share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.util.CommonUtils;
import com.sjy.ttclub.util.StringUtils;
import com.sjy.ttclub.widget.dialog.GenericDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*package*/ class ShareLocalPickerDialog extends GenericDialog {
    private ArrayList<ItemInfo> mItemList = new ArrayList<ItemInfo>();
    private ListView mShareListView;
    private ShareAdapter mAdapter;
    private Intent mShareIntent;

    private ShareLocalPlatformListener mListener;

    public ShareLocalPickerDialog(Context context) {
        super(context);
        initDialog();
    }

    private void initDialog() {
        Resources res = getContext().getResources();
        addTitle(res.getString(R.string.share_title));
        createShareListView();

        LinearLayout contentView = new LinearLayout(getContext());
        contentView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0);
        lp.weight = 1;
        contentView.addView(mShareListView, lp);
        addContentView(contentView);

        addSingleButton(ID_BUTTON_NO, res.getString(R.string.cancel));
    }

    private void createShareListView() {
        Resources res = getContext().getResources();

        mAdapter = new ShareAdapter(getContext(), mItemList);

        mShareListView = new ListView(getContext());
        mShareListView.setScrollingCacheEnabled(false);
        mShareListView.setDivider(new ColorDrawable(res.getColor(R.color.dialog_divider_color)));
        mShareListView.setSelector(res.getDrawable(R.drawable.default_listview_seletor));
        mShareListView.setDividerHeight(res.getDimensionPixelSize(R.dimen.dialog_divider_height));
        mShareListView.setFadingEdgeLength(0);
        mShareListView.setFocusable(true);
        mShareListView.setAdapter(mAdapter);
        mShareListView.setVerticalScrollBarEnabled(false);
        mShareListView.setVerticalFadingEdgeEnabled(false);
        CommonUtils.disableEdgeEffect(mShareListView);

        mShareListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ItemInfo info = mAdapter.getItem(position);
                if (info != null && mListener != null) {
                    mListener.onLocalPlatformSelected(info.intent, mShareIntent);
                }

                dismiss();
            }
        });
    }


    public void setShareIntent(Intent shareIntent) {
        mShareIntent = shareIntent;
    }

    public void setListener(ShareLocalPlatformListener listener) {
        mListener = listener;
    }

    private void queryShareItems() {
        if (mShareIntent == null) {
            return;
        }
        PackageManager manager = getContext().getPackageManager();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        String shareType = mShareIntent.getType();
        if (StringUtils.isEmpty(shareType)) {
            shareType = "*/*";
        }

        shareIntent.setType(shareType);
        List<ResolveInfo> activityInfos = manager.queryIntentActivities(
                shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
        Intent itemIntent = null;
        ItemInfo itemInfo;
        if (activityInfos != null && activityInfos.size() != 0) {
            mItemList.clear();

            for (Iterator<ResolveInfo> iterator = activityInfos.iterator(); iterator
                    .hasNext(); ) {
                ResolveInfo resolveInfo = (ResolveInfo) iterator.next();
                itemIntent = shareIntent.cloneFilter();

                ActivityInfo activityInfo = resolveInfo.activityInfo;
                if (!activityInfo.exported) {
                    continue;
                }
                itemInfo = new ItemInfo();
                itemInfo.intent = itemIntent;

                itemIntent.setClassName(activityInfo.packageName,
                        activityInfo.name);
                itemInfo.icon = resolveInfo.loadIcon(manager);
                itemInfo.text = String.valueOf(resolveInfo.loadLabel(manager));
                mItemList.add(itemInfo);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void show() {
        queryShareItems();
        super.show();
    }

    private class ShareAdapter extends ArrayAdapter<ItemInfo> {

        public ShareAdapter(Context context, List<ItemInfo> objects) {
            super(context, 0, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemView itemView = null;
            if (convertView == null) {
                itemView = new ItemView(getContext());
                convertView = itemView;
            } else {
                itemView = (ItemView) convertView;
            }
            itemView.setupItemView(getItem(position));
            return convertView;
        }

    }

    private static class ItemView extends LinearLayout {
        private ItemInfo mItemInfo;

        private ImageView mIconView;
        private TextView mTitleView;

        public ItemView(Context context) {
            super(context);
            initItemView();
        }

        private void initItemView() {
            Resources res = getContext().getResources();
            setOrientation(LinearLayout.HORIZONTAL);
            mIconView = new ImageView(getContext());
            int iconSize = res.getDimensionPixelSize(R.dimen.share_list_item_icon_size);
            LayoutParams lp = new LayoutParams(
                    iconSize, iconSize);
            lp.gravity = Gravity.CENTER_VERTICAL;
            addView(mIconView, lp);

            mTitleView = new TextView(getContext());
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    res.getDimensionPixelSize(R.dimen.share_list_item_textsize));
            mTitleView.setTextColor(res.getColor(R.color.share_list_item_text_color));
            mTitleView.setGravity(Gravity.CENTER_VERTICAL);
            lp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            lp.leftMargin = res.getDimensionPixelSize(R.dimen.share_list_item_text_marginLeft);
            lp.gravity = Gravity.CENTER_VERTICAL;
            addView(mTitleView, lp);

            setMinimumHeight(res.getDimensionPixelSize(R.dimen.share_list_item_minHeight));
        }

        public void setupItemView(ItemInfo info) {
            mItemInfo = info;
            if (info != null) {
                mIconView.setImageDrawable(info.icon);
                mTitleView.setText(info.text);
            }
        }

    }

    public static class ItemInfo {
        Drawable icon;
        String text;
        Intent intent;
    }

    public interface ShareLocalPlatformListener {
        void onLocalPlatformSelected(Intent platformIntent, Intent shareIntent);
    }

}
