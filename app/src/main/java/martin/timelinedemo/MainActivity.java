package martin.timelinedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import martin.timelinedemo.View.TimeLineView;

public class MainActivity extends AppCompatActivity {

    private Map<Integer, Integer> map = new HashMap<>();//存放当前的小时，对应的刻度位置
    private int minScale;
    private int maxScale;
    private int indicateLine;
    private Context mContext;
    private List<Integer> list;
    private TimeLineView timeLineView;
    private TextView tv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();
        setListener();
    }

    private void initView() {
        timeLineView = (TimeLineView) findViewById(R.id.timelineview);
        tv_time = (TextView) findViewById(R.id.tv_time);
    }

    private int scalePos = 0;//刻度的位置

    private void initData() {
        list = new ArrayList<>();//存放有大刻度的集合
        list.add(0);
        list.add(1);
        list.add(5);
        list.add(6);
        list.add(22);
        list.add(23);
        //小刻度
        minScale = Utils.dip2px(mContext, 60);
        //大刻度
        maxScale = Utils.dip2px(mContext, 240);
        //指示线距离顶部的位置
        indicateLine = Utils.dip2px(mContext, 60);

        //为了保证指示线可以指示到24点，刻度需要大于24
        for (int i = 0; i < 30; i++) {

            if (list.contains(i - 1)) {//需要减去1,再判断大刻度
                scalePos = scalePos + maxScale;
                map.put(i, scalePos);
            } else {//小刻度
                scalePos = scalePos + minScale;
                map.put(i, scalePos);
            }
        }
        timeLineView.initData(map, list);
    }

    private void setListener() {

        timeLineView.setOnScrollListener(new TimeLineView.OnScrollListener() {
            @Override
            public void onScroll(final String time) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_time.setText(time);
                    }
                });
            }
        });
    }
}
