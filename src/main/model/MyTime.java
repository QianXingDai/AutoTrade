package main.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTime {

    public static int month;
    public static int day;
    public static int hour;
    public static int minute;
    public static String time;
    private static long startTime;
    private static SimpleDateFormat sdf;

    public static boolean isRightTime(){
        updateTime();

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
        if(sdf == null){
            sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            startTime = System.currentTimeMillis();
        }
        time = sdf.format(new Date());
        String[] ss = time.split("[-:]");
        month = Integer.parseInt(ss[1]);
        day = Integer.parseInt(ss[2]);
        hour = Integer.parseInt(ss[3]);
        minute = Integer.parseInt(ss[4]);

    }

    public static boolean isNeedClearOutput(){
        if(System.currentTimeMillis() - startTime >= Config.delayTimeList.get(15)){
            startTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

}
