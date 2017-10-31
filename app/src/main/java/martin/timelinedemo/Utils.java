package martin.timelinedemo;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2017/8/29.
 */

public class Utils {

    public static int px2dip(Context context, float pxValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int dip2px(Context context, float dipValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 日期转换成时间戳
     */
    public static long dateToStamp(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millinTime = date.getTime();
        return millinTime;
    }

    /**
     * 将时间戳转换成日期
     */
    public static String stampToDate(long l) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(l);
        String res = simpleDateFormat.format(date);
        return res;
    }

    private static boolean isShowLog = true;

    /**
     * 展示Log
     *
     * @param name 日志名称
     * @param msg  日志数值
     */
    public static void showLog(String name, String msg) {
        if (isShowLog) {
            Log.i("DJC", name + ":" + msg);
        }
    }
}
