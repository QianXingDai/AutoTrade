package main.util;

import main.model.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    private static boolean isInit;
    private static int month;
    private static int day;
    private static int hour;
    private static int minute;
    private static String time;
    private static long startTime;
    private static SimpleDateFormat sdf;

    public static boolean isRightTime(){
        updateTime();
        if (Config.INSTANCE.isDebugMode){
            return true;
        }


        //只在9:15 - 11:30,13:00 - 15:00内执行
        if(hour >= 9 && hour != 12 && hour <= 15){
            if(hour == 9){
                return minute >= 15;
            }else if(hour == 11){
                return minute <= 30;
            }
            return true;
        }
        return false;
    }

    private static void updateTime(){
        if(!isInit){
            sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            startTime = System.currentTimeMillis();
            isInit = true;
        }
        time = sdf.format(new Date());
        String[] ss = time.split("[-:]");
        month = Integer.parseInt(ss[1]);
        day = Integer.parseInt(ss[2]);
        hour = Integer.parseInt(ss[3]);
        minute = Integer.parseInt(ss[4]);
    }

    public static boolean isNeedClearOutput(){
        if(System.currentTimeMillis() - startTime >= Config.INSTANCE.delay.clearOutputAreaIntervals){
            startTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public static int getMonth() {
        if(!isInit){
            updateTime();
        }
        return month;
    }

    public static int getDay() {
        if(!isInit){
            updateTime();
        }
        return day;
    }

    public static int getHour() {
        if(!isInit){
            updateTime();
        }
        return hour;
    }

    public static int getMinute() {
        if(!isInit){
            updateTime();
        }
        return minute;
    }

    public static String getTime() {
        return time;
    }
}
