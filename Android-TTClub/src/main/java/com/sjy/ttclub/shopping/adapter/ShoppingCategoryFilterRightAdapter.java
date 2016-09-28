package com.sjy.ttclub.shopping.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lsym.ttclub.R;
import com.sjy.ttclub.bean.shop.ShoppingCategoryBean;
import com.sjy.ttclub.bean.shop.ShoppingLabelBean;
import com.sjy.ttclub.shopping.model.ShoppingSelectorInfo;
import com.sjy.ttclub.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhxu on 2015/12/30.
 * Email:357599859@qq.com
 */
public class ShoppingCategoryFilterRightAdapter extends BaseAdapter {

    private Context mContext;

    private ShoppingCategoryBean mCategoryBean;

    public final int MODE_CATE = 1;
    public final int MODE_BRAND = 0;
    private int mCurrMode = MODE_CATE;

    private List<ShoppingSelectorInfo> mSelectorInfoList = new ArrayList<>();

    public ShoppingCategoryFilterRightAdapter(Context context, ShoppingCategoryBean categoryBean, ShoppingCategoryFilterLeftAdapter leftAdapter) {
        mContext = context;
        mCategoryBean = categoryBean;

        if (categoryBean.getBrand() == null || categoryBean.getBrand().size() == 0) {
            leftAdapter.noBrand();
        }
    }

    public boolean hasBrand() {
        for (ShoppingSelectorInfo selector : mSelectorInfoList) {
            if (selector.type == MODE_BRAND) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCate() {
        for (ShoppingSelectorInfo selector : mSelectorInfoList) {
            if (selector.type == MODE_CATE) {
                return true;
            }
        }
        return false;
    }

    public void setMode(int mode) {
        mCurrMode = mode;
        notifyDataSetChanged();
    }

    public void focus(TextView v) {

        boolean isChange = false;

        ShoppingSelectorInfo selectorInfo = new ShoppingSelectorInfo();
        selectorInfo.type = mCurrMode;
        selectorInfo.selectId = (int) v.getTag();
        selectorInfo.title = v.getText() + "";

        for (int i = 0; i < mSelectorInfoList.size(); i++) {
            if (mSelectorInfoList.get(i).type == mCurrMode) {
                mSelectorInfoList.set(i, selectorInfo);
                isChange = true;
            }
        }
        if (!isChange) {
            mSelectorInfoList.add(selectorInfo);
        }
        notifyDataSetChanged();
    }

    public void focus(ShoppingLabelBean labelBean) {

        boolean isChange = false;

        ShoppingSelectorInfo selectorInfo = new ShoppingSelectorInfo();
        selectorInfo.type = labelBean.getType() == 1 ? MODE_BRAND : MODE_CATE;
        selectorInfo.selectId = labelBean.getHid();
        selectorInfo.title = labelBean.getName();

        for (int i = 0; i < mSelectorInfoList.size(); i++) {
            if (mSelectorInfoList.get(i).type == mCurrMode) {
                mSelectorInfoList.set(i, selectorInfo);
                isChange = true;
            }
        }
        if (!isChange) {
            mSelectorInfoList.add(selectorInfo);
        }
        notifyDataSetChanged();
    }

    public List<ShoppingSelectorInfo> getSelectorInfoList() {
        return mSelectorInfoList;
    }

    public View stepRightView(int position) {
        View view = View.inflate(mContext, R.layout.shopping_category_filter_right_items, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_category);
        textView.setText(mCurrMode == MODE_CATE ? mCategoryBean.getCate().get(position).getName() : mCategoryBean.getBrand().get(position).getName());
        textView.setTag(mCurrMode == MODE_CATE ? mCategoryBean.getCate().get(position).getId() : mCategoryBean.getBrand().get(position).getId());
        textView.setSelected(false);

        for (ShoppingSelectorInfo selectorInfo : mSelectorInfoList) {
            if (selectorInfo.type == mCurrMode) {
                textView.setSelected(StringUtils.parseInt(textView.getTag() + "") == selectorInfo.selectId);
            }
        }

        return view;
    }

    @Override
    public int getCount() {
        return mCurrMode == MODE_CATE ? mCategoryBean.getCate().size() : mCategoryBean.getBrand().size();
    }

    @Override
    public Object getItem(int position) {
        return mCurrMode == MODE_CATE ? mCategoryBean.getCate().get(position) : mCategoryBean.getBrand().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return stepRightView(position);
    }
}
