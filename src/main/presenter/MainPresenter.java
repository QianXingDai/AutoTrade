package main.Presenter;

import main.Util.StockUtil;
import main.Util.TimeUtil;
import main.View.MainView;
import main.model.Config;
import main.model.Stock;

import java.awt.*;
import java.util.List;

public class MainPresenter {

    public static final int SOLD = 1;
    public static final int BUY = 2;

    private final MainView mainView;
    private List<Integer> delayTimeList;
    private List<Stock> stockList;
    private int dealCount;
    private final Robot robot;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        this.robot = StockUtil.getRobot();
    }

    public void start(){
        new Thread(()->{
            while (dealCount < stockList.size()){
                robot.delay(delayTimeList.get(14));

                //判断是否在有效时间段内
                if(!TimeUtil.isRightTime()){
                    mainView.print("     还没到时间哟 ， 时间到了会自动执行哒\n",1);
                    continue;
                }

                //是否需要清除信息框输出，太长时间不清除会卡死，建议1小时内清除一次
                if(TimeUtil.isNeedClearOutput()){
                    mainView.clearOutput();
                }
                for(int i = 0; i < stockList.size(); i++){
                    Stock stock = stockList.get(i);
                    stock.update();

                    if(stock.isShouldQuery()){
                        mainView.print("   " + stock.stockName + " (" + stock.stockCode + ") 现价 : " + stock.nowPrice + "  ---- " + TimeUtil.getTime() + "\r\n",1);

                        switch (stock.dealMethod){
                            case "s1" : {
                                if(stock.soldWay1()){
                                    trade(stock,i,SOLD);
                                }
                                break;
                            }
                            case "s2" : {
                                if(stock.soldWay2()){
                                    trade(stock,i,SOLD);
                                }
                                break;
                            }
                            case "s3" : {
                                if(stock.soldWay3()){
                                    trade(stock,i,SOLD);
                                }
                                break;
                            }
                            case "s4" : {
                                if(stock.soldWay4()){
                                    trade(stock,i,SOLD);
                                }
                                break;
                            }
                            case "s5" : {
                                if(stock.soldWay5()){
                                    trade(stock,i,SOLD);
                                }
                                break;
                            }
                            case "b1" : {
                                if(stock.buyWay1()){
                                    trade(stock,i,BUY);
                                }
                                break;
                            }
                            case "b2" : {
                                if(stock.buyWay2()){
                                    trade(stock,i,BUY);
                                }
                                break;
                            }
                            case "b3" : {
                                if(stock.buyWay3()){
                                    trade(stock,i,BUY);
                                }
                                break;
                            }
                            case "b4" : {
                                if(stock.buyWay4()){
                                    trade(stock,i,BUY);
                                }
                                break;
                            }
                            case "b5" : {
                                if(stock.buyWay5()){
                                    trade(stock,i,BUY);
                                }
                                break;
                            }
                            default:break;
                        }
                    }
                }
            }
        }).start();
    }

    private void trade(Stock stock,int index,int flag) {
        String s = null;
        if (flag == SOLD) {
            s = "卖出";
            stock.sold();
        } else if (flag == BUY) {
            s = "买入";
            stock.buy();
        }
        dealCount++;
        stock.isDeal = true;
        mainView.print(s + " : " + stock.stockName + "  ---  " + TimeUtil.getTime() + "\r\n", 2);
        mainView.updateLabel(index * 5 + 3);
    }

    public void initData(){
        Config.init();
        stockList = Config.stockList;
        delayTimeList = Config.delayTimeList;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public List<Integer> getDelayTimeList() {
        return delayTimeList;
    }
}
