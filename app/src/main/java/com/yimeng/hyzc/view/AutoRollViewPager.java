package com.yimeng.hyzc.view;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.yimeng.hyzc.utils.BitmapUtils;
import com.yimeng.hyzc.utils.DensityUtil;
import com.yimeng.hyzc.utils.MyApp;
import com.yimeng.hyzc.utils.UiUtils;

import java.util.ArrayList;

public class AutoRollViewPager extends ViewPager {

    private ArrayList<String> datas;


    public AutoRollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoRollViewPager(Context context) {
        super(context);
    }

    public void setData(ArrayList<String> datas) {
        this.datas = datas;
        this.datas.add(0, datas.get(datas.size() - 1));
        this.datas.add(datas.get(1));
        MyPagerAdapter adapter = new MyPagerAdapter();
        setAdapter(adapter);
        addOnPageChangeListener(null);
        setCurrentItem(1);
        startRoll();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int index = (getCurrentItem() + 1) % datas.size();
            setCurrentItem(index);
            handler.sendEmptyMessageDelayed(0, 3000);
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
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    public void stopRoll() {
        handler.removeCallbacksAndMessages(null);
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
                } else if (ev.getX() < downX && getCurrentItem() == datas.size() - 1) {
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
                    onItemClickListener.onItemClick(getCurrentItem());
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
        super.addOnPageChangeListener(new InnserOnPageChangeListener(listener));
    }

    public class InnserOnPageChangeListener implements OnPageChangeListener {
        private int position;

        private OnPageChangeListener listener;

        public InnserOnPageChangeListener(OnPageChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (listener != null)
                listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            this.position = position;
            if (listener != null)
                listener.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //  空闲 IDLE
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (position == getAdapter().getCount() - 1) {
                    // A 最后一个条目
                    // 切换到position = 1的 条目
                    //悄悄的切换
                    setCurrentItem(1, false);
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
        private int[] resId = new int[]{
                R.mipmap.app_logo,
                R.mipmap.ic_launcher,
                R.mipmap.icon_user_check,
                R.mipmap.app_logo,
                R.mipmap.ic_launcher,
        };

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = (ImageView) UiUtils.inflate(R.layout.layout_imageview);
            imageView.setImageBitmap(BitmapUtils.getResImg(getContext(),resId[position]));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            Picasso.with(getContext())//// TODO: 2016/7/14 轮播图数据源为模拟
//                    .load(datas.get(position))
//                    .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
//                    .error(R.mipmap.ic_launcher)
//                    .into(imageView);
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
