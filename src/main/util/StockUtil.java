package main.Util;


import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.awt.*;

public class StockUtil {

    private static Robot robot;

    public static int timeToInt(String s) {
        int hour = Integer.parseInt(s.substring(0,2));
        int minute = Integer.parseInt(s.substring(3,5));
        int second = Integer.parseInt(s.substring(6,8));
        return hour * 3600 + minute * 60 + second;
    }

    public static void shiftProcess(String title){

        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, title);
        System.out.println(hwnd == null);
        User32.INSTANCE.ShowWindow(hwnd, 9);
        User32.INSTANCE.SetForegroundWindow(hwnd);
    }

    public static void inputStock(String code,int delayTime) {
        Robot robot = getRobot();
        for(int i = 0; i < code.length(); i++) {
            robot.keyPress(code.charAt(i));
            robot.keyRelease(code.charAt(i));
            robot.delay(delayTime);
        }
    }

    public static Robot getRobot(){
        if(robot == null){
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
        return robot;
    }

}
