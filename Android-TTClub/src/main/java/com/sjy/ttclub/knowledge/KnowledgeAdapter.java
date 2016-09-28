package com.sjy.ttclub.knowledge;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lsym.ttclub.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sjy.ttclub.bean.KnowledgeArticleBean;
import com.sjy.ttclub.util.ResourceHelper;
import com.sjy.ttclub.util.StringUtils;

import java.util.List;

/**
 * Created by linhz on 2015/12/21.
 * Email: linhaizhong@ta2she.com
 */
public class KnowledgeAdapter extends ArrayAdapter<KnowledgeArticleBean.ArticleInfo> {
    private Context mContext;
    private int mType;


    public KnowledgeAdapter(Context context, List<KnowledgeArticleBean.ArticleInfo> list, int type) {
        super(context, 0, list);
        mType = type;
        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createItemView();
        }
        if (convertView instanceof SimpleDraweeView) {
            String url = getItem(position).getUrl();
            if (StringUtils.isNotEmpty(url)) {
                ((SimpleDraweeView) convertView).setImageURI(Uri.parse(url));
            }
        }
        return convertView;
    }

    private SimpleDraweeView createItemView() {
        SimpleDraweeView view = new SimpleDraweeView(mContext);
        if (mType == KnowledgeConst.TYPE_MAIN) {
            view.setMinimumHeight(ResourceHelper.getDimen(R.dimen.space_180));
        } else {
            view.setMinimumHeight(ResourceHelper.getDimen(R.dimen.space_150));
        }
        return view;
    }

}