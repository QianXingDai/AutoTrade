package main.util;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import main.model.Config;
import main.model.Stock;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;

public class StockUtil {

    public static final String BASE_URL = "http://hq.sinajs.cn/list=";

    private static Robot robot;
    private static final OkHttpClient okHttpClient = new OkHttpClient();

    public static int timeToInt(String s) {
        int hour = Integer.parseInt(s.substring(0,2));
        int minute = Integer.parseInt(s.substring(3,5));
        int second = Integer.parseInt(s.substring(6,8));
        return hour * 3600 + minute * 60 + second;
    }

    public static String requestData(Request request){
        try {
            synchronized (okHttpClient){
                return Objects.requireNonNull(okHttpClient.newCall(request).execute().body()).string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean checkIfShouldTrade(Stock stock){
        switch (stock.tradeMethod){
            case S1: {
                if(soldWay1(stock)){
                    return true;
                }
                break;
            }
            case S2 : {
                if(soldWay2(stock)){
                    return true;
                }
                break;
            }
            case S3 : {
                if(soldWay3(stock)){
                    return true;
                }
                break;
            }
            case S4 : {
                if(soldWay4(stock)){
                    return true;
                }
                break;
            }
            case S5 : {
                if(soldWay5(stock)){
                    return true;
                }
                break;
            }
            case B1 : {
                if(buyWay1(stock)){
                    return true;
                }
                break;
            }
            case B2 : {
                if(buyWay2(stock)){
                    return true;
                }
                break;
            }
            case B3 : {
                if(buyWay3(stock)){
                    return true;
                }
                break;
            }
            case B4 : {
                if(buyWay4(stock)){
                    return true;
                }
                break;
            }
            case B5 : {
                if(buyWay5(stock)){
                    return true;
                }
                break;
            }
            default:{
                try{
                    stock.isAvailable = false;
                    throw new IllegalArgumentException("没有定义的交易方法 for " + stock.stockName);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    synchronized public static void shiftProcess(String title){
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, title);
        if (hwnd != null){
            User32.INSTANCE.ShowWindow(hwnd, 9);
            User32.INSTANCE.SetForegroundWindow(hwnd);
            User32.INSTANCE.SetFocus(hwnd);
            return;
        }
        robot = getRobot();
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.delay(Config.INSTANCE.delay.longPressTabTime);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_ALT);
    }


    private static boolean soldWay1(Stock stock) {
        if(stock.time < 34200)
            return false;

        if(stock.todayHighestPrice < stock.lastDayClosePrice) {
            if(stock.nowPrice < stock.lastDayClosePrice * 0.95) {
                if(stock.time < 35100) {
                    return stock.nowPrice < stock.lastDayClosePrice * 0.93;
                } else{
                    return true;
                }
            }
        } else {
            return stock.nowPrice < stock.todayHighestPrice * 0.95;
        }
        return false;
    }

    private static boolean soldWay2(Stock stock) {
        if(stock.time < 33930)
            return false;
        return stock.nowPrice >= stock.limitUpPrice;
    }

    //朴素卖法
    private static boolean soldWay3(Stock stock) {
        float limit = 10;
        if(limit >= 0){
            if(stock.changeRate >= limit){
                return true;
            }
        }else{
            if(stock.changeRate <= limit){
                return true;
            }
        }
        return false;
    }

    //低吸卖出
    private static boolean soldWay4(Stock stock) {
        double primaryBound = -2.8;         //初级下界
        double mediumBound = -3.2;        //中级下界
        double primaryUpperBound = 2;       //初级上界
        double mediumUpperBound = 5;      //中级上界
        double primaryReply = 0.5;          //初级回幅
        double mediumReply = 0.8;          //中级回幅
        double highestRate = (stock.todayHighestPrice / stock.lastDayClosePrice - 1) * 100;        //最高涨幅
        double lowestRate = (stock.todayLowestPrice / stock.lastDayClosePrice - 1) * 100;       //最低涨幅

        if (stock.time > 10 * 3600 + 30 * 60){
            if(lowestRate <= mediumBound){
                if(stock.changeRate <= mediumBound){
                    return true;
                }
            }else{
                if(stock.changeRate <= primaryBound){
                    return true;
                }
            }
        }

        if(highestRate > mediumUpperBound){
            if(stock.changeRate < highestRate * mediumReply){
                return true;
            }
        }else if(highestRate > primaryUpperBound){
            if(stock.changeRate < highestRate * primaryReply){
                return true;
            }
        }else{
            if(stock.time >= 14 * 3600 + 54 * 60 && stock.changeRate <= 9.8){
                return true;
            }
        }
        return false;
    }

    //波动卖出
    private static boolean soldWay5(Stock stock) {
        double highestRate = (stock.todayHighestPrice / stock.lastDayClosePrice - 1) * 100;
        double backRate = 5;        //回落幅度
        if(highestRate >= 0){
            if(stock.changeRate <= highestRate - backRate){
                return true;
            }
        }else {
            if(stock.changeRate <= 0 - backRate){
                return true;
            }
        }
        return false;
    }

/*    //中线卖出
    public boolean soldWay6(){
        int days = 2;
        double startPrice;
        double stopLossRate = -4;
        double averageDailyProfitA = 1.008;
        double averageDailyProfitB = 1.01;
        double averageDailyProfitC = 1.012;

        if(days >= 2 || days <= 3){
            if(todayHighestPrice / startPrice >= 1.04){

            }
        }
    }*/

    private static boolean buyWay1(Stock stock) {
        if(stock.time <= 33900){
            return false;     //  从9:25开始
        }
        return stock.todayHighestPrice / stock.lastDayClosePrice >= 1.09;
    }

    private static boolean buyWay2(Stock stock) {
        if(stock.time <= 9 * 3600 + 30 * 60 && stock.time >= 9 * 3600 + 25 * 60 + 30 ) {        //  从9:25:30 - 9:30:00
            return stock.changeRate >= 2;
        }
        return false;
    }

    //竞价强买,阈值2%
    private static boolean buyWay3(Stock stock) {
        if(stock.time >= 9 * 3600 + 25 * 60 && stock.time <= 9 * 3600 + 30 * 60){
            return stock.changeRate >= 2;
        }
        return false;
    }

    //打板
    private static boolean buyWay4(Stock stock) {
        if(stock.stockCode.startsWith("3")){
            return stock.todayHighestPrice / stock.lastDayClosePrice >= 1.195;
        }else{
            return stock.todayHighestPrice / stock.lastDayClosePrice >= 1.095;
        }
    }

    //中线突破
    private static boolean buyWay5(Stock stock) {
        double openRate = (stock.todayOpenPrice / stock.lastDayClosePrice - 1) * 100;
        double highLimit = 5;
        double lowLimit = -5;
        double lowCritical = 2.7;
        if(stock.time > 9 * 3600 + 30 * 60){
            if(openRate <= highLimit){
                if(openRate >= lowCritical){
                    return true;
                }else{
                    return openRate >= lowLimit && stock.time >= 14 * 3600 + 55 * 60;
                }
            }
        }

        return false;
    }


    public static void buy(Stock stock) {
        Robot robot = getRobot();
        Config.Delay delay = Config.INSTANCE.delay;

        shiftProcess(Config.INSTANCE.title);

        robot.delay(delay.afterSwitchToTradeProgramDelay);

        robot.keyPress(KeyEvent.VK_F2);          //切换到卖出界面 , 缓冲一下
        robot.keyRelease(KeyEvent.VK_F2);
        robot.delay(delay.afterF1PressedDelay);

        robot.keyPress(KeyEvent.VK_F1);          //切换到买入界面
        robot.keyRelease(KeyEvent.VK_F1);
        robot.delay(delay.afterF2PressedDelay);

        inputCode(stock.stockCode, delay.stockCodeNumberInputDelay);           // 输入股票代码

        robot.delay(delay.afterStockCodeNumberInputDelay);

        robot.keyPress(KeyEvent.VK_TAB);    // 切换到买入价格
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_TAB);
        robot.delay(delay.afterSwitchToTradePriceTABDelay);

        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);        // 删掉预设价格
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.delay(delay.afterDeletePriceDelay);

        String price;
        if(stock.changeRate >= 8){
            price = String.valueOf(stock.limitUpPrice);
        }else {
            price = String.valueOf(stock.nowPrice * 1.015);
        }
        price = price.substring(0,price.indexOf('.') + 3);
        inputCode(price, delay.stockCodeNumberInputDelay);  // 自动输入涨停价
        robot.delay(delay.afterInputPriceDelay);

        robot.keyPress(KeyEvent.VK_TAB);    // 切换到买入数量
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.delay(delay.afterSwitchToTradeNumberTABDelay);

        inputCode(stock.dealNum, delay.stockCodeNumberInputDelay);            //  输入买入数量
        robot.delay(delay.afterInputTradeNUmberDelay);

        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.delay(delay.afterSwitchToTradeButtonDelay);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(delay.afterTradeButtonPressedDelay);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(delay.afterConfirmTradeButtonPressedDelay);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(delay.afterLastEnterDelay);

        shiftProcess("AutoTrade");
    }

    public static void sold(Stock stock){
        Robot robot = StockUtil.getRobot();
        Config.Delay delay = Config.INSTANCE.delay;

        shiftProcess(Config.INSTANCE.title);

        robot.delay(delay.afterSwitchToTradeProgramDelay);

        robot.keyPress(KeyEvent.VK_F1);          //切换到买入界面 , 缓冲一下
        robot.keyRelease(KeyEvent.VK_F1);
        robot.delay(delay.afterF1PressedDelay);

        robot.keyPress(KeyEvent.VK_F2);          //切换到卖出界面
        robot.keyRelease(KeyEvent.VK_F2);
        robot.delay(delay.afterF2PressedDelay);

        inputCode(stock.stockCode, delay.stockCodeNumberInputDelay);               // 输入股票代码
        robot.delay(delay.afterStockCodeNumberInputDelay);

        robot.keyPress(KeyEvent.VK_TAB);    // 切换到卖出价格
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.delay(delay.afterSwitchToTradePriceTABDelay);

        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);        // 删掉预设价格
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyPress(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.keyRelease(com.sun.glass.events.KeyEvent.VK_BACKSPACE);
        robot.delay(delay.afterDeletePriceDelay);

        String price;
        if(stock.changeRate <= -8){
            price = Float.toString(round((float) (stock.lastDayClosePrice * 0.9)));
        }else{
            price = Float.toString(round((float) (stock.nowPrice * 0.985)));
        }
        price = price.substring(0,price.indexOf('.') + 3);

        inputCode(price, delay.stockCodeNumberInputDelay);   // 自动输入跌停价
        robot.delay(delay.afterInputPriceDelay);

        robot.keyPress(KeyEvent.VK_TAB);    // 切换到卖出数量
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.delay(delay.afterSwitchToTradeNumberTABDelay);

        inputCode(stock.dealNum, delay.stockCodeNumberInputDelay);            //  输入卖出数量
        robot.delay(delay.afterInputTradeNUmberDelay);

        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.delay(delay.afterSwitchToTradeButtonDelay);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(delay.afterTradeButtonPressedDelay);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(delay.afterConfirmTradeButtonPressedDelay);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(delay.afterLastEnterDelay);
        shiftProcess("AutoTrade");
    }

    public static void inputCode(String code, int delayTime) {
        Robot robot = getRobot();
        for(int i = 0; i < code.length(); i++) {
            robot.keyPress(code.charAt(i));
            robot.keyRelease(code.charAt(i));
            robot.delay(delayTime);
        }
    }

    synchronized public static Robot getRobot(){
        if(robot == null){
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
        return robot;
    }

    public static float round(float num) {
        float temp = num * 100;
        if(temp - Math.floor(temp) >= 0.5)
            return (float) ((Math.floor(temp) + 1) / 100);
        return (float) (Math.floor(temp) / 100);
    }
}
