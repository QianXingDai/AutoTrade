package main.model;

import java.lang.reflect.Field;
import java.util.List;

public class ConfigBean {

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

    public boolean isDebugMode;

    public List<ConfigBean.TradeProgramName> tradeNameList;

    public static class TradeProgramName{
        public String name;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Class<TradeProgramName> clazz = TradeProgramName.class;

            for (Field declaredField : clazz.getDeclaredFields()) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Class<ConfigBean> configBeanClass = ConfigBean.class;

        for (Field declaredField : configBeanClass.getDeclaredFields()) {
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
