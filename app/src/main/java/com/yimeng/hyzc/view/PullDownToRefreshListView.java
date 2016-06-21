package com.yimeng.hyzc.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yimeng.hyzc.R;

/**
 * 可以下拉刷新的ListView。
 * 使用时不需要调用setOnscrollListener(),onTouchEvent(),addHeaderView()和addRooterView()方法
 */
public class PullDownToRefreshListView extends ListView {
    private LinearLayout headerRoot;
    private ImageView ivArrow;
    private TextView tvText;
    private ProgressBar pb;
    private LinearLayout footerRoot;
    private TextView tvDate;

    private int headerRootHeight;
    private static final int DEFAULT_Y = 10000;
    private float startY = DEFAULT_Y;
    private float downY = DEFAULT_Y;

    private static final int STATE_DOWN = 1;
    private static final int STATE_RELEASE = 2;
    private static final int STATE_FRESH = 3;
    private int state = STATE_DOWN;
    private RotateAnimation downToUp;
    private RotateAnimation upToDown;
    /**
     * 刷新头下增加的第一个view
     */
    private View appendedHeaderView;
    private int footerRootHeight;
    public boolean isLoadingMore;

    public PullDownToRefreshListView(Context context, AttributeSet attrs,
                                     int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public PullDownToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PullDownToRefreshListView(Context context) {
        super(context);
        initView();
    }

    /**
     * 初始化listview的头、脚，设置监听
     */
    private void initView() {
        initAnimation();
        initHeaderView();
        initFooterView();
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE
                        && getLastVisiblePosition() == getCount() - 1
                        && onRefreshListener != null && !isLoadingMore) {
                    isLoadingMore = true;
                    onRefreshListener.onLoadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

    }

    private OnRefreshListener onRefreshListener;
    public boolean isRefreshing;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * 当下拉刷新或者上拉加载下一页时的回调监听
     */
    public interface OnRefreshListener {
        /**
         * 下拉刷新时调用此方法
         */
        void onPullToRefresh();

        /**
         * 下拉加载下一页时调用此方法
         */
        void onLoadMore();
    }

    private void initFooterView() {
        View view = View.inflate(getContext(), R.layout.layout_refresh_footer,
                null);
        footerRoot = (LinearLayout) view.findViewById(R.id.ll_refresh_footer_root);
        footerRoot.measure(0, 0);
        footerRootHeight = footerRoot.getMeasuredHeight();
        addFooterView(view);
    }

    private void initHeaderView() {
        View view = View.inflate(getContext(), R.layout.layout_refresh_header,
                null);
        headerRoot = (LinearLayout) view.findViewById(R.id.ll_refresh_header_root);
        tvDate = (TextView) view.findViewById(R.id.tv_refresh_header_time);
        tvText = (TextView) view.findViewById(R.id.tv_refresh_header_text);
        ivArrow = (ImageView) view.findViewById(R.id.iv_refresh_header_arrow);
        pb = (ProgressBar) view.findViewById(R.id.pb_refresh_header);
        headerRoot.measure(0, 0);
        headerRootHeight = headerRoot.getMeasuredHeight();
        headerRoot.setPadding(0, -headerRootHeight, 0, 0);
        String date = (String) DateFormat.format("yy-MM-dd kk:mm:ss", System.currentTimeMillis());
        tvDate.setText(date);
        addHeaderView(view);
    }

    private void initAnimation() {
        downToUp = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        downToUp.setDuration(200);
        downToUp.setFillAfter(true);

        upToDown = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upToDown.setDuration(200);
        upToDown.setFillAfter(true);
    }

    /**
     * 向刷新头下增加一个view布局
     *
     * @param view
     */
    public void appendHeaderView(View view) {
        if (appendedHeaderView == null) {
            appendedHeaderView = view;
        }
        headerRoot.addView(view);
    }

    @Override
    /**
     * 触摸事件发生时判断是否需要下拉刷新头，如果是调用对应方法，如果不是，将事件交给listview处理
     * 判断方法：获得listview的y坐标a，再获得刷新头下的第一个条目(添加的appendHeaderView或者是第一个数据view条目)的y坐标b，
     * 如果b>=a，说明需要执行下拉刷新头的操作
     */
    public boolean onTouchEvent(MotionEvent ev) {
        int[] location = new int[2];
        getLocationInWindow(location);
        int listY = location[1];
        if (appendedHeaderView == null) {
            getAdapter().getView(0, null, this).getLocationInWindow(location);
        } else {
            appendedHeaderView.getLocationInWindow(location);
        }
        int headerY = location[1];

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (downY == DEFAULT_Y) {
                    downY = ev.getY();
                    break;
                }
                float distanceY = ev.getY() - downY;
                if (headerY >= listY && distanceY > 0) {
                    pullHearderRoot(ev.getY());
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (state == STATE_DOWN) {
                    headerRoot.setPadding(0, -headerRootHeight, 0, 0);
                } else if (state == STATE_RELEASE) {
                    state = STATE_FRESH;
                    flushHeadRoot();
                }
                startY = DEFAULT_Y;
                downY = DEFAULT_Y;
                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * move事件发生时，如果判断需要执行下拉刷新头的操作，执行此方法，下拉刷新头
     *
     * @param y 当前move事件中getY()坐标
     */
    private void pullHearderRoot(float y) {
        if (startY == DEFAULT_Y) {
            startY = y;
            return;
        }
        int paddingTop = (int) (y - startY - headerRootHeight);
        headerRoot.setPadding(0, paddingTop, 0, 0);
        if (paddingTop > 0 && state == STATE_DOWN) {
            state = STATE_RELEASE;
            flushHeadRoot();
        } else if (paddingTop < 0 && state == STATE_RELEASE) {
            state = STATE_DOWN;
            flushHeadRoot();
        }
    }

    /**
     * 刷新listView头的状态
     */
    private void flushHeadRoot() {
        switch (state) {
            case STATE_DOWN:
                ivArrow.startAnimation(upToDown);
                tvText.setText("下拉刷新");
                break;
            case STATE_RELEASE:
                ivArrow.startAnimation(downToUp);
                tvText.setText("释放立即刷新");
                break;
            case STATE_FRESH:
                ivArrow.clearAnimation();
                headerRoot.setPadding(0, 0, 0, 0);
                pb.setVisibility(View.VISIBLE);
                ivArrow.setVisibility(View.INVISIBLE);
                tvText.setText("正在刷新...");
                if (onRefreshListener != null && !isRefreshing) {
                    isRefreshing = true;
                    ivArrow.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onRefreshListener.onPullToRefresh();
                        }
                    }, 2000);
                } else {
                    handler.sendEmptyMessageDelayed(0, 1000);
                }
                break;

            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            initHeaderRootState();
        }
    };

    /**
     * 初始化listview头的状态
     */
    public void initHeaderRootState() {
        state = STATE_DOWN;
        headerRoot.setPadding(0, -headerRootHeight, 0, 0);
        pb.setVisibility(View.INVISIBLE);
        ivArrow.setVisibility(View.VISIBLE);
        tvText.setText("下拉刷新");
    }

    /**
     * 隐藏listView的脚布局
     */
    public void hideFooter() {
        isLoadingMore = false;
        footerRoot.setPadding(0, -footerRootHeight, 0, 0);
    }

    /**
     * 显示listView的脚布局
     */
    public void displayFooter() {
        footerRoot.setPadding(0, 0, 0, 0);
    }

    /**
     * 下拉刷新完成时调用此方法，更新刷新状态，初始化listview的头
     *
     * @param result 重新加载数据成功标识
     */
    public void refreshCompleted(boolean result) {
        isRefreshing = false;
        initHeaderRootState();
        if (result) {
            String date = (String) DateFormat.format("yy-MM-dd kk:mm:ss", System.currentTimeMillis());
            tvDate.setText(date);
        }
    }

}
