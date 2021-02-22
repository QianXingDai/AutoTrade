package main.model.bean;

import main.model.TradeMethod;
import main.util.StockUtil;
import main.util.TimeUtil;
import okhttp3.Request;

import static main.util.StockUtil.timeToInt;

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

    public String startDate;  //开始日期
    public String dealNum;           //交易数量
    public int startMonth;         //开始月份
    public int startDay;        //开始日子
    public boolean isDeal;        //是否已经成交

    public boolean isAvailable;
    public int index;
    public TradeMethod tradeMethod;   //交易方式
    public String dealMethod;    //交易方式,同上,只是字符形式的
    public boolean isBuy;   //是买入还是卖出
    private final Request request;

    public Stock(int index,String stockCode,String tradeMethod,String dealNum,String startDate) {
        this.stockCode = stockCode;
        this.dealMethod = tradeMethod;
        this.tradeMethod = TradeMethod.getTradeMethod(tradeMethod);
        this.dealNum = dealNum;
        this.startDate = startDate;
        this.startMonth = Integer.parseInt(startDate.substring(0,startDate.indexOf('.')));
        this.startDay = Integer.parseInt(startDate.substring(startDate.indexOf('.') + 1));
        this.index = index;

        char ch = tradeMethod.charAt(0);
        this.isBuy = ch == 'b' || ch == 'B';

        String url = StockUtil.BASE_URL + (stockCode.charAt(0) == '6' ? "sh" : "sz") + stockCode;
        this.request = new Request.Builder().url(url).build();
        update();
    }

    //从接口获得数据，然后生成实体类
    public void update(){
        try {
            String[] vars = StockUtil.requestData(request).split(",");

            if (vars.length < 32){
                return;
            }

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
            changeRate = StockUtil.round((nowPrice / lastDayClosePrice - 1) * 100);
            limitUpPrice = StockUtil.round((float) (lastDayClosePrice * 1.1));

            isAvailable = true;
        } catch (Exception e) {
            e.printStackTrace();
            isAvailable = false;
        }
    }



    public boolean isShouldQuery(){
        return !isDeal && startMonth == TimeUtil.getMonth() && startDay == TimeUtil.getDay();
    }
}
