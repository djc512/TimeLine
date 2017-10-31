package martin.timelinedemo.View;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import martin.timelinedemo.R;
import martin.timelinedemo.Utils;

/**
 * Created by admin on 2017/10/30.
 */

public class TimeLineView extends ScrollView {

    private FrameLayout container;
    private Context mContext;
    private int scaleLineWidth;
    private int scaleLineHeight;
    private FrameLayout lineContainer;
    private int scaleHeight;
    private int textSize;
    private int minScale;
    private int maxScale;
    private int indicateLine;
    private float firstScrollPos;//第一次滚动的位置
    private int firsthour;
    private int firstminute;
    private int firstsecond;
    private long baseScrollMillion;
    private float topPos;
    private float bottomPos;

    public TimeLineView(Context context) {
        super(context);
        init(context);
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        container = new FrameLayout(context);
        mContext = context;

        //刻度指示线的宽度
        scaleLineWidth = Utils.dip2px(context, 10);
        //刻度指示线的高度
        scaleLineHeight = Utils.dip2px(context, 1);
        //设置字体的大小
        textSize = 15;
        //设置刻度值的高度
        scaleHeight = Utils.dip2px(context, 20);
        //小刻度
        minScale = Utils.dip2px(mContext, 60);
        //大刻度
        maxScale = Utils.dip2px(mContext, 240);
        //指示线距离顶部的位置
        indicateLine = Utils.dip2px(mContext, 60);
    }

    private Map<Integer, Integer> map;
    private List<Integer> list;//存放的大刻度
    private int scrollHour = -1;

    /**
     * 数据的初始化
     *
     * @param map
     * @param list
     */
    public void initData(Map<Integer, Integer> map, List<Integer> list) {
        this.list = list;
        this.map = map;
        setScaleLine();
        setScale();
        addView(container);

        topPos = map.get(0) - indicateLine;
        bottomPos = map.get(24) - indicateLine;

        String time = "2017-10-31 02:00:00";
        //第一次滚动的时间戳
        baseScrollMillion = Utils.dateToStamp(time);
        Log.i("DJC", "baseScrollMillion++++++++++" + baseScrollMillion);
        String[] timeSplit = time.split(" ")[1].split(":");

        firsthour = Integer.parseInt(timeSplit[0]);
        firstminute = Integer.parseInt(timeSplit[1]);
        firstsecond = Integer.parseInt(timeSplit[2]);
        firstScrollPos = getPostionByTime(firsthour, firstminute, firstsecond);
        Log.i("DJC", "firstScrollPos+++++++++" + firstScrollPos);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollTo(0, (int) firstScrollPos);
            }
        }, 200);
        scrollHour = firsthour;
    }

    /**
     * 添加刻度
     * 字体的大小设置必须小于字体设置的高度，否则字体不居中
     */
    private void setScale() {
        for (int i = 0; i <= 24; i++) {
            int scalePos = map.get(i);
            Log.i("DJC", "scalePos+++++++" + i + "****" + scalePos);
            TextView mTextView = new TextView(mContext);
            mTextView.setTextSize(textSize);//15的单位是px
            if (i < 10) {
                mTextView.setText("0" + i);
            } else {
                mTextView.setText(i + "");
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, scaleHeight);
            params.leftMargin = scaleLineWidth;
            params.topMargin = scalePos - scaleHeight / 2;
            mTextView.setLayoutParams(params);
            container.addView(mTextView);
            mTextView = null;
        }
    }

    /**
     * 设置刻度的位置
     */
    private void setScaleLine() {

        for (int i = 0; i < 30; i++) {
            int scalePos = map.get(i);//获取每一个刻度的位置
            View mView = new View(mContext);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(scaleLineWidth, scaleLineHeight);
            params.topMargin = scalePos;
            mView.setLayoutParams(params);

            if (i <= 24) {
                mView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
            }
            container.addView(mView);
            mView = null;
        }
    }

    /**
     * 根据时间计算滚动的距离
     * 只需要计算分秒即可
     */
    private float getPostionByTime(int hour, int minute, int second) {
        float hourPos = map.get(hour);
        float minutePos = 0;
        float secondPos = 0;
        if (list.contains(hour)) {//大刻度
            minutePos = maxScale * minute / 60f;
            secondPos = maxScale * second / 3600f;
        } else {//小刻度
            minutePos = minScale * minute / 60f;
            secondPos = minScale * second / 3600f;
        }
        float totalPos = hourPos + minutePos + secondPos - indicateLine;
        return totalPos;
    }


    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);

        if (y <= topPos) {
            scrollTo(0, (int) topPos);
        } else if (y >= bottomPos) {
            scrollTo(0, (int) bottomPos);
        } else {
            getTimeByPostion(y);
        }
    }

    private String stopTime;

    /**
     * 根据滚动的距离计算时间
     *
     * @param scrollY
     * @return
     */
    private String getTimeByPostion(int scrollY) {
        long scrollDis = (long) (scrollY - firstScrollPos);//与第一次停止位置，之间的差距
        long scrollSecond = 0;

        if (list.contains(scrollHour)) {//大刻度,1个dp5秒
            scrollSecond = 3600 * scrollDis / maxScale;//滚动的秒数
            Log.i("DJC", "---------------------------------");
        } else {//小刻度，1个dp20秒
            Log.i("DJC", "*********************************");
            scrollSecond = 3600 * scrollDis / minScale;
        }

        long scrollMillion = baseScrollMillion + scrollSecond * 1000;
        Log.i("DJC", "scrollMillion+++++++++" + scrollMillion);
        String scrollTime = Utils.stampToDate(scrollMillion);
        Log.i("DJC", "scrollTime+++++++++" + scrollTime);


        stopTime = scrollTime.split(" ")[1];
        if (null != onScrollListener) {
            onScrollListener.onScroll(stopTime);
        }
        scrollHour = Integer.parseInt(scrollTime.split(" ")[1].split(":")[0]);
        Log.i("DJC", "scrollHour++++++++++" + scrollHour);
        return scrollTime;
    }


    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    private OnScrollListener onScrollListener;

    public interface OnScrollListener {
        void onScroll(String time);
    }
}
