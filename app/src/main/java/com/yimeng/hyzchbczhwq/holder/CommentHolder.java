package com.yimeng.hyzchbczhwq.holder;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yimeng.hyzchbczhwq.R;
import com.yimeng.hyzchbczhwq.bean.CommentBean;
import com.yimeng.hyzchbczhwq.bean.PrescriptionBean;
import com.yimeng.hyzchbczhwq.utils.MyApp;
import com.yimeng.hyzchbczhwq.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 满意度评价holder
 */
public class CommentHolder extends BaseHolder<CommentBean> {
    private TextView tv_name;
    private RatingBar rating_bar;
    private TextView tv_time;
    private TextView tv_comment;

    @Override
    protected View initView() {
        View view = UiUtils.inflate(R.layout.item_comment);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        rating_bar = (RatingBar) view.findViewById(R.id.rating_bar);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_comment = (TextView) view.findViewById(R.id.tv_comment);
        return view;
    }

    @Override
    public void bindData(CommentBean bean) {
        MyApp context = MyApp.getAppContext();
        if (bean.is_anonymity == 0) {// 实名
            tv_name.setText(String.format("%s：%s", context.getString(R.string.patient), bean.patient_name));
        } else {// 匿名
            tv_name.setText(String.format("%s：%s", context.getString(R.string.patient), context.getString(R.string.anonymous)));
        }
        rating_bar.setRating(bean.comment_point);
        tv_comment.setText(String.format("%s：%s", context.getString(R.string.comment), bean.comment_content));
        try {
            String date = bean.comment_time.substring(bean.comment_time.indexOf("(") + 1, bean.comment_time.indexOf(")"));
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(Long.parseLong(date)));
            tv_time.setText(date);
        } catch (Exception e) {
            tv_time.setText(context.getString(R.string.empty_content));
            e.printStackTrace();
        }
    }
}
