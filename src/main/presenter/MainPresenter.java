package main.presenter;

import main.model.Config;
import main.model.Stock;
import main.util.StockUtil;
import main.util.TimeUtil;
import main.view.MainView;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainPresenter {

    public static final int MAX_THREAD_NUM = 3;

    private final MainView mainView;
    private final Robot robot;
    private final Queue<Stock> stockQueue;
    private final Queue<Stock> tradeStockQueue;
    private final Config config;
    private boolean isRunning;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        this.robot = StockUtil.getRobot();
        this.stockQueue = new LinkedList<>();
        this.tradeStockQueue = new LinkedList<>();
        config = Config.INSTANCE;
        stockQueue.addAll(config.stockList);
    }

    public void start(){
        if (isRunning){
            return;
        }
        isRunning = true;
        ExecutorService executor = Executors.newScheduledThreadPool(MAX_THREAD_NUM);

        //不断从还没成交的队列中进行查询,满足交易条件就加入交易队列
        executor.execute(()->{
            while (!stockQueue.isEmpty()){
                robot.delay(config.delay.latestInfoIntervals);

                //判断是否在有效时间段内
                if(!TimeUtil.isRightTime()){
                    mainView.print("     还没到时间哟 ， 时间到了会自动执行哒\n", MainView.OutputArea.LEFT_AREA);
                    continue;
                }

                //是否需要清除信息框输出，太长时间不清除会卡死，建议1小时内清除一次
                if(TimeUtil.isNeedClearOutput()){
                    mainView.clearOutput();
                }

                Stock stock = stockQueue.poll();
                assert stock != null;
                stock.update();

                if(stock.isShouldQuery()){
                    mainView.print("   " + stock.stockName + " (" + stock.stockCode + ") 现价 : " + stock.nowPrice + "  ---- " + TimeUtil.getTime() + "\r\n", MainView.OutputArea.LEFT_AREA);
                    if (StockUtil.checkIfShouldTrade(stock)){
                        synchronized (tradeStockQueue){
                            tradeStockQueue.add(stock);
                        }
                    }else{
                        stockQueue.add(stock);
                    }
                }
            }
        });

        //不断从要交易的股票队列中取出对象进行交易操作
        executor.execute(() -> {
            while (!tradeStockQueue.isEmpty() || !stockQueue.isEmpty()){
                Stock stock;
                synchronized (tradeStockQueue){
                    stock = tradeStockQueue.poll();
                }
                if (stock != null){
                    if (stock.isBuy){
                        StockUtil.buy(stock);
                    }else{
                        StockUtil.sold(stock);
                    }
                    stock.isDeal = true;
                    String s = "";
                    mainView.print(s + " : " + stock.stockName + "  ---  " + TimeUtil.getTime() + "\r\n", MainView.OutputArea.RIGHT_AREA);
                    mainView.updateLabel(stock.index * 5 + 3);
                }
            }
            executor.shutdown();
        });
    }

    public List<Stock> getStockList() {
        return Config.INSTANCE.stockList;
    }

    public boolean isSuccessLoadConfig() {
        return Config.INSTANCE.delay != null;
    }
}
