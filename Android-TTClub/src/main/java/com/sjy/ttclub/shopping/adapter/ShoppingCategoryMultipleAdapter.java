package com.sjy.ttclub.shopping.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.shopping.model.ShoppingSelectorInfo;
import com.sjy.ttclub.util.ResourceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxu on 2015/12/30.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryMultipleAdapter extends BaseAdapter {

    private Context mContext;

    private List<ShoppingSelectorInfo> mSelectorInfoList = new ArrayList<>();
    private final int MULTIPLE = 0, MAXID = 1, MINID = 2;
    private final String MULTIPLE_STR = "multi", MAXID_STR = "priceMinus", MINID_STR = "pricePlus";

    public ShoppingCategoryMultipleAdapter(Context context) {
        mContext = context;
        ShoppingSelectorInfo itemMultiple = new ShoppingSelectorInfo();
        itemMultiple.selectId = MULTIPLE;
        itemMultiple.title = ResourceHelper.getString(R.string.shopping_category_top_multiple);
        itemMultiple.isSelected = false;
        mSelectorInfoList.add(itemMultiple);

        ShoppingSelectorInfo itemMax = new ShoppingSelectorInfo();
        itemMax.selectId = MAXID;
        itemMax.title = ResourceHelper.getString(R.string.shopping_category_top_max);
        itemMultiple.isSelected = false;
        mSelectorInfoList.add(itemMax);

        ShoppingSelectorInfo itemMin = new ShoppingSelectorInfo();
        itemMin.selectId = MINID;
        itemMin.title = ResourceHelper.getString(R.string.shopping_category_top_min);
        itemMultiple.isSelected = false;
        mSelectorInfoList.add(itemMin);
    }

    public void unFocus() {
        for (int i = 0; i < mSelectorInfoList.size(); i++) {
            mSelectorInfoList.get(i).isSelected = false;
        }
        notifyDataSetChanged();
    }

    public void focusMultiple() {
        for (int i = 0; i < mSelectorInfoList.size(); i++) {
            if (mSelectorInfoList.get(i).selectId == MULTIPLE) {
                mSelectorInfoList.get(i).isSelected = true;
            } else {
                mSelectorInfoList.get(i).isSelected = false;
            }
        }
        notifyDataSetChanged();
    }

    public void focus(TextView v) {
        ShoppingSelectorInfo selectorInfo = new ShoppingSelectorInfo();
        selectorInfo.selectId = getIdByType(v.getTag().toString());
        selectorInfo.title = v.getText() + "";
        selectorInfo.isSelected = true;
        for (int i = 0; i < mSelectorInfoList.size(); i++) {
            if (mSelectorInfoList.get(i).selectId == selectorInfo.selectId) {
                mSelectorInfoList.set(i, selectorInfo);
            } else {
                mSelectorInfoList.get(i).isSelected = false;
            }
        }
        notifyDataSetChanged();
    }

    public View stepListView(int position) {
        View view = View.inflate(mContext, R.layout.shopping_category_filter_right_items, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_category);
        textView.setText(mSelectorInfoList.get(position).title);
        textView.setTag(getTypeById(mSelectorInfoList.get(position).selectId));
        textView.setSelected(mSelectorInfoList.get(position).isSelected);
        return view;
    }

    private int getIdByType(String type) {
        if (type == MULTIPLE_STR) {
            return MULTIPLE;
        } else if (type == MAXID_STR) {
            return MAXID;
        } else if (type == MINID_STR) {
            return MINID;
        }
        return MULTIPLE;
    }

    private String getTypeById(int id) {
        if (id == MULTIPLE) {
            return MULTIPLE_STR;
        } else if (id == MAXID) {
            return MAXID_STR;
        } else if (id == MINID) {
            return MINID_STR;
        }
        return MULTIPLE_STR;
    }

    @Override
    public int getCount() {
        return mSelectorInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mSelectorInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return stepListView(position);
    }
}
