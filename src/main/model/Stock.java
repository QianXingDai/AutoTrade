package main.model;

import com.sun.glass.events.KeyEvent;
import main.Util.StockUtil;
import main.Util.TimeUtil;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static main.Util.StockUtil.*;

public class Stock {
    public String stockCode;   //  股票代码
    public String stockName;   //  股票名称
    public String date;          //  日期

    public int time;             //  时间

    public float todayOpenPrice;  //今日开盘价
    public float lastDayClosePrice;   //昨日收盘价
    public float nowPrice;          //  现价
    public float todayHighestPrice;  //今日最高价
    public float todayLowestPrice;  //今日最低价
    public float limitUpPrice;         // 涨停价

    public float dealSumPrice;   //今日总成交额
    public float dealSum;          //  今日成交股票数量

    public float buyOneAmount;       //  买一数量
    public float buyTwoAmount;    //  买二数量
    public float buyThreeAmount;  //买三数量
    public float buyFourAmount;  // 买四数量
    public float buyFiveAmount;  //买五数量

    public float buyOnePrice;  //买一价
    public float buyTwoPrice;  //买二价
    public float buyThreePrice;  //买三价
    public float buyFourPrice;  //买四价
    public float buyFivePrice;  //买五价

    public float soldOneAmount;   //卖一数量
    public float soldTwoAmount;  //卖二数量
    public float soldThreeAmount;  //卖三数量
    public float soldFourAmount;  //卖四数量
    public float soldFiveAmount;  //卖五数量

    public float soldOnePrice;  //卖一价
    public float soldTwoPrice;  //卖二价
    public float soldThreePrice;  //卖三价
    public float soldFourPrice;  //卖四价
    public float soldFivePrice;  //卖五价

    public float changeRate;  //涨幅

    public String dealMethod;  //交易方法
    public String startDate;  //开始日期
    public String dealNum;           //交易数量
    public int startMonth;         //开始月份
    public int startDay;        //开始日子
    public boolean isDeal;        //是否已经成交

    private URL url;   //请求url
    private final StringBuilder sb;   //留给下面update复用,避免大量构造重复对象

    public Stock(String stockCode,String dealMethod,String dealNum,String startDate) {
        this.stockCode = stockCode;
        this.dealMethod = dealMethod;
        this.dealNum = dealNum;
        this.startDate = startDate;
        this.startMonth = Integer.parseInt(startDate.substring(0,startDate.indexOf('.')));
        this.startDay = Integer.parseInt(startDate.substring(startDate.indexOf('.') + 1));
        this.sb = new StringBuilder();
        try {
            this.url = new URL("http://hq.sinajs.cn/list=" + (stockCode.charAt(0) == '6' ? "sh" : "sz") + stockCode);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        update();
    }

    //从接口获得数据，然后生成实体类
    public void update(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "gbk"));
            String contentText;
            String temp;

            while((temp = br.readLine()) != null){
                sb.append(temp);
            }
            contentText = sb.toString().trim();

            String[] vars = contentText.split(",");

            //股票名称不用每次都赋值，赋值一次就够了
            if(stockName == null){
                stockName = vars[0].substring(vars[0].indexOf('"') + 1);
            }

            todayOpenPrice = Float.parseFloat(vars[1]);
            lastDayClosePrice = Float.parseFloat(vars[2]);
            nowPrice = Float.parseFloat(vars[3]);
            todayHighestPrice = Float.parseFloat(vars[4]);
            todayLowestPrice = Float.parseFloat(vars[5]);
            buyOnePrice = Float.parseFloat(vars[6]);
            soldOnePrice = Float.parseFloat(vars[7]);
            dealSum = Float.parseFloat(vars[8]);
            dealSumPrice = Float.parseFloat(vars[9]);
            buyOneAmount = Float.parseFloat(vars[10]);
            buyOnePrice = Float.parseFloat(vars[11]);
            buyTwoAmount = Float.parseFloat(vars[12]);
            buyTwoPrice = Float.parseFloat(vars[13]);
            buyThreeAmount = Float.parseFloat(vars[14]);
            buyThreePrice = Float.parseFloat(vars[15]);
            buyFourAmount = Float.parseFloat(vars[16]);
            buyFourPrice = Float.parseFloat(vars[17]);
            buyFiveAmount = Float.parseFloat(vars[18]);
            buyFivePrice = Float.parseFloat(vars[19]);
            soldOneAmount = Float.parseFloat(vars[20]);
            soldOnePrice = Float.parseFloat(vars[21]);
            soldTwoAmount = Float.parseFloat(vars[22]);
            soldTwoPrice = Float.parseFloat(vars[23]);
            soldThreeAmount = Float.parseFloat(vars[24]);
            soldThreePrice = Float.parseFloat(vars[25]);
            soldFourAmount = Float.parseFloat(vars[26]);
            soldFourPrice = Float.parseFloat(vars[27]);
            soldFiveAmount = Float.parseFloat(vars[28]);
            soldFivePrice = Float.parseFloat(vars[29]);
            date = vars[30];
            time = timeToInt(vars[31]);
            changeRate = round((nowPrice / lastDayClosePrice - 1) * 100);
            limitUpPrice = round((float) (lastDayClosePrice * 1.1));

            sb.delete(0,sb.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean soldWay1() {
        if(time < 34200)
            return false;

        if(todayHighestPrice < lastDayClosePrice) {
            if(nowPrice < lastDayClosePrice * 0.95) {
                if(time < 35100) {
                    return nowPrice < lastDayClosePrice * 0.93;
                } else{
                    return true;
                }
            }
        } else {
            return nowPrice < todayHighestPrice * 0.95;
        }
        return false;
    }

    public boolean soldWay2() {
        if(time < 33930)
            return false;
        return nowPrice >= limitUpPrice;
    }

    public boolean soldWay3() {
        return false;
    }

    public boolean soldWay4() {
        return false;
    }

    public boolean soldWay5() {
        return false;
    }

    public boolean buyWay1() {
        if(time <= 33900){
            return false;     //  从9:25开始
        }
        return todayHighestPrice / lastDayClosePrice >= 1.09;
    }

    public boolean buyWay2() {
        if(time <= 34200 && time >= 33930 ) {        //  从9:25:30 - 9:30:00
            return changeRate >= 2;
        }
        return false;
    }

    public boolean buyWay3() {
        return false;
    }

    public boolean buyWay4() {
        return false;
    }

    public boolean buyWay5() {
        return false;
    }

    public void buy() {
        try {
            Robot robot = StockUtil.getRobot();
            List<Integer> list = Config.delayTimeList;

            shiftProcess(Config.title);

            robot.delay(list.get(1));

            robot.keyPress(KeyEvent.VK_F2);          //切换到卖出界面 , 缓冲一下
            robot.keyRelease(KeyEvent.VK_F2);
            robot.delay(list.get(2));

            robot.keyPress(KeyEvent.VK_F1);          //切换到买入界面
            robot.keyRelease(KeyEvent.VK_F1);
            robot.delay(list.get(3));

            inputStock(stockCode,list.get(4));           // 输入股票代码

            robot.delay(list.get(5));

            robot.keyPress(KeyEvent.VK_TAB);    // 切换到买入价格
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(list.get(6));

            robot.keyPress(KeyEvent.VK_BACKSPACE);        // 删掉预设价格
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.keyPress(KeyEvent.VK_BACKSPACE);
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.keyPress(KeyEvent.VK_BACKSPACE);
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.keyPress(KeyEvent.VK_BACKSPACE);
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.keyPress(KeyEvent.VK_BACKSPACE);
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.delay(list.get(7));

            inputStock(String.valueOf(limitUpPrice),list.get(4));  // 自动输入涨停价
            robot.delay(list.get(8));

            robot.keyPress(KeyEvent.VK_TAB);    // 切换到买入数量
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(list.get(9));

            inputStock(dealNum,list.get(4));            //  获取买入数量
            robot.delay(list.get(10));

            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(list.get(11));
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(list.get(12));
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(list.get(13));
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(list.get(14));

            shiftProcess("AutoTrade");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sold(){
        try {
            Robot robot = new Robot();
            List<Integer> list = Config.delayTimeList;
            shiftProcess(Config.title);

            robot.delay(list.get(0));

            robot.keyPress(KeyEvent.VK_F1);          //切换到买入界面 , 缓冲一下
            robot.keyRelease(KeyEvent.VK_F1);
            robot.delay(list.get(1));

            robot.keyPress(KeyEvent.VK_F2);          //切换到卖出界面
            robot.keyRelease(KeyEvent.VK_F2);
            robot.delay(list.get(2));

            inputStock(stockCode,list.get(3));               // 输入股票代码
            robot.delay(list.get(4));

            robot.keyPress(KeyEvent.VK_TAB);    // 切换到卖出价格
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(list.get(5));

            robot.keyPress(KeyEvent.VK_BACKSPACE);        // 删掉预设价格
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.keyPress(KeyEvent.VK_BACKSPACE);
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.keyPress(KeyEvent.VK_BACKSPACE);
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.keyPress(KeyEvent.VK_BACKSPACE);
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.keyPress(KeyEvent.VK_BACKSPACE);
            robot.keyRelease(KeyEvent.VK_BACKSPACE);
            robot.delay(list.get(6));

            String price = Float.toString(round((float) (lastDayClosePrice * 0.9)));   // 自动输入跌停价
            price = price.substring(0,price.indexOf('.') + 3);

            inputStock(price,list.get(4));
            robot.delay(list.get(7));

            robot.keyPress(KeyEvent.VK_TAB);    // 切换到卖出数量
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(list.get(8));

            inputStock(dealNum,list.get(4));            //  输入卖出数量
            robot.delay(list.get(9));

            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.delay(list.get(10));
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(list.get(11));
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(list.get(12));
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(list.get(13));
            shiftProcess("AutoTrade");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean isShouldQuery(){
        return !isDeal && startMonth == TimeUtil.getMonth() && startDay == TimeUtil.getDay();
    }

    private static float round(float num) {
        float temp = num * 100;
        if(temp - Math.floor(temp) >= 0.5)
            return (float) ((Math.floor(temp) + 1) / 100);
        return (float) (Math.floor(temp) / 100);
    }
}
