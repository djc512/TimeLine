package martin.timelinedemo.View;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
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
    private String currentDate;

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
        //设置刻度值字体的高度
        scaleHeight = Utils.dip2px(context, 20);
        //小刻度
        minScale = Utils.dip2px(mContext, 60);
        //大刻度
        maxScale = Utils.dip2px(mContext, 300);
        //指示线距离顶部的位置,与0点距离顶部的高度一致，即minScale
        indicateLine = Utils.dip2px(mContext, 60);
    }

    private Map<Integer, Integer> map;
    private List<Integer> list;//存放的大刻度
    private int scrollHour = -1;
    private String currentTime;
    private int screenWidth;

    /**
     * 数据的初始化
     *
     * @param map
     * @param list
     * @param currentTime
     * @param screenWidth
     */
    public void initData(Map<Integer, Integer> map, List<Integer> list, String currentTime, int screenWidth) {
        this.currentTime = currentTime;
        this.list = list;
        this.map = map;
        this.screenWidth = screenWidth;

        setPisodeColor();
        setScaleLine();
        setScale();
        setBitmap();
        addView(container);

        topPos = map.get(0) - indicateLine;
        bottomPos = map.get(24) - indicateLine;

        String[] currentTimeSplit = currentTime.split(" ");
        currentDate = currentTimeSplit[0];
        String[] timeSplit = currentTimeSplit[1].split(":");

        firsthour = Integer.parseInt(timeSplit[0]);
        firstminute = Integer.parseInt(timeSplit[1]);
        firstsecond = Integer.parseInt(timeSplit[2]);
        firstScrollPos = getPostionByTime(firsthour, firstminute, firstsecond);
        Log.i("DJC", "firstScrollPos+++++++++" + firstScrollPos);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(0, (int) firstScrollPos);
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

    private List<TimeDataBean> pisodeList = new ArrayList<>();

    /**
     * 设置时间段的背景色
     */
    private void setPisodeColor() {
        for (int i = 0; i < 5; i++) {
            TimeDataBean bean = new TimeDataBean();
            bean.setStartTime(currentDate + " " + "0" + i + ":30:10");
            bean.setEndTime(currentDate + " " + "0" + (i + 1) + ":20:45");
            pisodeList.add(bean);
        }

        for (int i = 0; i < pisodeList.size(); i++) {
            TimeDataBean bean = pisodeList.get(i);
            String startTime = bean.getStartTime();
            String[] startSplit = startTime.split(" ")[1].split(":");
            String endTime = bean.getEndTime();
            String[] endSplit = endTime.split(" ")[1].split(":");

            float startPos = getPostionByTime(Integer.parseInt(startSplit[0]), Integer.parseInt(startSplit[1]), Integer.parseInt(startSplit[2]));
            float endPos = getPostionByTime(Integer.parseInt(endSplit[0]), Integer.parseInt(endSplit[1]), Integer.parseInt(endSplit[2]));
            float disPos = endPos - startPos;

            View view = new View(getContext());
            FrameLayout.LayoutParams params = new LayoutParams(screenWidth, (int) disPos);
            params.topMargin = (int) (startPos + indicateLine);
            view.setLayoutParams(params);

            view.setBackgroundColor(getResources().getColor(R.color.colorPisode));
            container.addView(view);
        }
    }

    /**
     * 添加图片
     */
    private void setBitmap() {
        for (int i = 0; i < 5; i++) {
            TimeDataBean bean = new TimeDataBean();
            bean.setStartTime(currentDate + " " + "0" + i + ":30:10");
            bean.setEndTime(currentDate + " " + "0" + (i + 1) + ":20:45");
            pisodeList.add(bean);
        }

        for (int i = 0; i < pisodeList.size(); i++) {
            TimeDataBean bean = pisodeList.get(i);
            String startTime = bean.getStartTime();
            String[] startSplit = startTime.split(" ")[1].split(":");

            float startPos = getPostionByTime(Integer.parseInt(startSplit[0]), Integer.parseInt(startSplit[1]), Integer.parseInt(startSplit[2]));
            ImageView iv = new ImageView(getContext());
            FrameLayout.LayoutParams params = new LayoutParams(screenWidth / 4, minScale);
            params.leftMargin = screenWidth / 2;
            params.topMargin = (int) (startPos + indicateLine);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(R.mipmap.avs_type_default);
            container.addView(iv);
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
    private String scrollHourStr;

    /**
     * 根据滚动的距离计算时间
     * 以当前整点的时间作为基础，判断是大刻度还是小刻度，获取整点在时间轴中的位置及对应的时间戳
     * scrollY减去基础时间的位置，获取偏移位置，计算偏移的秒数
     * 整点的时间戳加上偏移秒数计算当前滚动的时间
     *
     * @param scrollY
     * @return
     */
    private String getTimeByPostion(int scrollY) {
        long scrollSecond = 0;
        if (scrollHour < 10) {
            scrollHourStr = "0" + scrollHour;
        } else {
            scrollHourStr = "" + scrollHour;
        }
        long scrollHourMillion = Utils.dateToStamp(currentDate + " " + scrollHourStr + ":" + "00" + ":" + "00");//以当前整点的时间戳作为基准
        int disScroll = scrollY + indicateLine - map.get(scrollHour);
        if (list.contains(scrollHour)) {//大刻度
            scrollSecond = 3600 * disScroll / maxScale;//滚动的秒数
        } else {
            scrollSecond = 3600 * disScroll / minScale;
        }

        String scrollTime = Utils.stampToDate(scrollHourMillion + scrollSecond * 1000);
        stopTime = scrollTime.split(" ")[1];
        if (null != onScrollListener) {
            onScrollListener.onScroll(stopTime, scrollY);
        }
        scrollHour = Integer.parseInt(scrollTime.split(" ")[1].split(":")[0]);
        return scrollTime;
    }


    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    private OnScrollListener onScrollListener;

    public interface OnScrollListener {
        void onScroll(String time, int postion);

        void onScrollStop(String time, int postion);
    }

    private float downY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                float upY = ev.getY();
                if (upY != downY) {
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    handler.sendMessageDelayed(msg, 100);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int lastY;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    if (lastY == getScrollY()) {
                        if (null != onScrollListener) {
                            onScrollListener.onScrollStop(stopTime, lastY);
                        }
                    } else {
                        handler.sendEmptyMessageDelayed(1, 100);
                        lastY = getScrollY();
                    }
                    break;
            }
        }
    };
}
