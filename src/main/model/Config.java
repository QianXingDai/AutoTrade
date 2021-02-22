package main.model;

import com.google.gson.Gson;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public enum Config {

    INSTANCE;

    public List<Stock> stockList;
    public String title;
    public Delay delay;
    public boolean isDebugMode;

    Config(){
        init();
    }

    private void init(){
        loadSettingConfig();
        loadStockConfig();
    }

    private void loadSettingConfig(){
        try {
            String filePath = "config.json";
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String temp;

            while((temp = br.readLine()) != null){
                sb.append(temp);
                sb.append("\n");
            }

            ConfigBean configBean = new Gson().fromJson(sb.toString(), ConfigBean.class);
            delay = new Delay();

            delay.afterSwitchToTradeProgramDelay = configBean.afterSwitchToTradeProgramDelay;
            delay.afterF1PressedDelay = configBean.afterF1PressedDelay;
            delay.afterF2PressedDelay = configBean.afterF2PressedDelay;
            delay.stockCodeNumberInputDelay = configBean.stockCodeNumberInputDelay;
            delay.afterStockCodeNumberInputDelay = configBean.afterStockCodeNumberInputDelay;
            delay.afterSwitchToTradePriceTABDelay = configBean.afterSwitchToTradePriceTABDelay;
            delay.afterDeletePriceDelay = configBean.afterDeletePriceDelay;
            delay.afterInputPriceDelay = configBean.afterInputPriceDelay;
            delay.afterSwitchToTradeNumberTABDelay = configBean.afterSwitchToTradeNumberTABDelay;
            delay.afterInputTradeNUmberDelay = configBean.afterInputTradeNUmberDelay;
            delay.afterSwitchToTradeButtonDelay = configBean.afterSwitchToTradeButtonDelay;
            delay.afterTradeButtonPressedDelay = configBean.afterTradeButtonPressedDelay;
            delay.afterConfirmTradeButtonPressedDelay = configBean.afterConfirmTradeButtonPressedDelay;
            delay.afterLastEnterDelay = configBean.afterLastEnterDelay;
            delay.latestInfoIntervals = configBean.latestInfoIntervals;
            delay.clearOutputAreaIntervals = configBean.clearOutputAreaIntervals;
            delay.longPressTabTime = configBean.longPressTabTime;
            for (ConfigBean.TradeProgramName tradeProgramName : configBean.tradeNameList){
                WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, tradeProgramName.name);
                if (hwnd != null){
                    title = tradeProgramName.name;
                    break;
                }
            }
            isDebugMode = configBean.isDebugMode;

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStockConfig(){
        stockList = new ArrayList<>();
        String filePath = "stock.txt";
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String temp;
            int index = 0;

            while ((temp = br.readLine()) != null){
                String[] ss = temp.trim().split("\\s+");
                Stock stock = new Stock(index,ss[0],ss[1],ss[2],ss[3]);
                if (stock.isAvailable){
                    stockList.add(stock);
                    index++;
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            try {
                assert br != null;
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Delay{
        public int afterSwitchToTradeProgramDelay;//切换到下单程序之后的延迟
        public int afterF1PressedDelay;  //按下F1(F2)之后的延迟
        public int afterF2PressedDelay;  //按下F2(F1)之后的延迟
        public int stockCodeNumberInputDelay; //输入股票代码各数字之间的延迟
        public int afterStockCodeNumberInputDelay;  //输完股票代码之后的延迟
        public int afterSwitchToTradePriceTABDelay; //切换到卖出(买入)价格按下的TAB键之后的延迟
        public int afterDeletePriceDelay;  //按五次退格键删掉预设价格(数量)之后的延迟
        public int afterInputPriceDelay; //输入跌(涨)停价之后的延迟
        public int afterSwitchToTradeNumberTABDelay; //切换到卖出(买入)数量按下的TAB键之后的延迟
        public int afterInputTradeNUmberDelay;// 输完卖出(买入)数量之后的延迟
        public int afterSwitchToTradeButtonDelay;  //切换到卖出(买入)按钮按下的TAB键之后的延迟
        public int afterTradeButtonPressedDelay;  //按下卖出按钮之后的延迟
        public int afterConfirmTradeButtonPressedDelay;  //按下确认卖出的ENTER键之后的延迟
        public int afterLastEnterDelay;  //按下最后一次ENTER键之后的延迟
        public int latestInfoIntervals; //每次查询间隔时间
        public int clearOutputAreaIntervals; //清空信息框间隔时间
        public int longPressTabTime;  //按住tab的持续时间

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Class<Delay> delayClass = Delay.class;

            for (Field declaredField : delayClass.getDeclaredFields()) {
                declaredField.setAccessible(true);
                sb.append(declaredField.getName()).append(" : ");
                try {
                    sb.append(declaredField.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }
}
