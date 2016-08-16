package com.yimeng.hyzc.view;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yimeng.hyzc.R;
import com.yimeng.hyzc.utils.UiUtils;

import java.util.ArrayList;

public class AutoRollViewPager extends ViewPager {

    private ArrayList<String> data = new ArrayList<>();
    private static final int WHAT_ROLL = 0;


    public AutoRollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoRollViewPager(Context context) {
        super(context);
    }

    public void setData(ArrayList<String> data) {
        this.data.clear();
        for (int i = 0; i < data.size(); i++) {
            this.data.add(data.get(i));
        }
        this.data.add(0, data.get(data.size() - 1));
        this.data.add(data.get(0));
        MyPagerAdapter adapter = new MyPagerAdapter();
        setAdapter(adapter);
        addOnPageChangeListener(null);
        setCurrentItem(1);
        startRoll();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case WHAT_ROLL:
                    int index = (getCurrentItem() + 1) % data.size();
                    setCurrentItem(index);
                    handler.sendEmptyMessageDelayed(WHAT_ROLL, 3000);
            }
        }
    };

    private float downX;


    protected void onDetachedFromWindow() {
        stopRoll();
        super.onDetachedFromWindow();
    }

    ;

    public void startRoll() {
        stopRoll();
        if (data != null && data.size() > 0)
            handler.sendEmptyMessageDelayed(WHAT_ROLL, 3000);
    }

    public void stopRoll() {
        handler.removeMessages(WHAT_ROLL);
    }

    private boolean isClick;

    private float downY;

    private long downTime;

    @Override
    /**
     * 点击此ViewPager区域时，在获得down事件时请求处理事件并停止滚动，默认为点击事件
     * 在move事件中，1.当当前点相对于点击的点移动的横或者竖方向上的距离大于5像素时，认为是滑动事件；
     * 2.当横向距离小于竖向距离时，将事件交与外层（可能是ViewPager的onTouchEvent中默认的处理方式）
     * 3.当滑动到Pager集合的两端，但仍然向外滑动时，交与外层
     * 4.当2和3的条件都不符合时（即用户要正常切换Pager或者点击），要求处理事件
     * 在up事件中，根据移动距离、事件时间间隔来执行点击事件监听中的事件点击方法，并继续开始滚动
     * 在cancel事件中，即此ViewPager区域失去move事件时，继续开始滚动
     */
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                downX = ev.getX();
                downY = ev.getY();
                downTime = SystemClock.uptimeMillis();
                stopRoll();
                isClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - downX) > 5
                        || Math.abs(ev.getY() - downY) > 5) {
                    isClick = false;
                }
                if (Math.abs(ev.getX() - downX) < Math.abs(ev.getY() - downY)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (ev.getX() > downX && getCurrentItem() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (ev.getX() < downX && getCurrentItem() == data.size() - 1) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                startRoll();
                break;
            case MotionEvent.ACTION_UP:
                if (isClick && (SystemClock.uptimeMillis() - downTime) < 500
                        && onItemClickListener != null) {
                    onItemClickListener.onItemClick(getCurrentItem() - 1);
                }
                startRoll();
                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int index);
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(new InnerOnPageChangeListener(listener));
    }

    public class InnerOnPageChangeListener implements OnPageChangeListener {
        private int position;

        private OnPageChangeListener listener;

        public InnerOnPageChangeListener(OnPageChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (listener != null && position != data.size() - 1 && position != 0)
                listener.onPageScrolled(position - 1, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            this.position = position;
            if (listener != null && position != data.size() - 1 && position != 0)
                listener.onPageSelected(position - 1);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //  空闲 IDLE
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (position == getAdapter().getCount() - 1) {
                    // A 最后一个条目 切换到position = 1的 条目 悄悄的切换
                    setCurrentItem(1, false);//TODO 跳页bug
                } else if (position == 0) {
                    //D
                    setCurrentItem(getAdapter().getCount() - 2, false);
                }
            }
            if (listener != null)
                listener.onPageScrollStateChanged(state);

        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = (ImageView) UiUtils.inflate(R.layout.layout_imageview);
            Picasso.with(getContext())
                    .load(data.get(position))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
