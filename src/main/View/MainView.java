package main.View;

import main.Presenter.MainPresenter;
import main.model.Stock;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainView {

    private MainPresenter mainPresenter;
    private List<Stock> stockList;
    private boolean isRunning;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JLabel[] labels;

    public static void main(String[] args) {
        MainView mainView = new MainView();
        mainView.mainPresenter = new MainPresenter(mainView);

        mainView.initData();
        mainView.initView();
    }

    private void initData(){
        mainPresenter.initData();
        stockList = mainPresenter.getStockList();
    }

    private void initView() {
        JFrame frame;
        frame = new JFrame("AutoTrade");

        frame.setBounds(100, 100, 580, 574);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        frame.setResizable(false);

        textArea2 = new JTextArea();
        textArea2.setBounds(340, 0, 230, 121);
        textArea2.setEditable(false);
        textArea2.setLineWrap(true);
        frame.getContentPane().add(textArea2);

        labels = new JLabel[stockList.size() * 5];          /* 标签  */
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 340, 300);

        textArea1 = new JTextArea();
        textArea1.setEditable(false);
        textArea1.setBounds(0, 0, 340, 300);
        scrollPane.setViewportView(textArea1);
        frame.getContentPane().add(scrollPane);

        JPanel panel = new JPanel();
        panel.setBounds(10, 310, 511, 170);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        JLabel labelCode = new JLabel("代码");
        labelCode.setBounds(49, 10, 59, 15);
        panel.add(labelCode);

        JLabel labelMethod = new JLabel("方法");
        labelMethod.setBounds(137, 10, 37, 15);
        panel.add(labelMethod);

        JLabel labelAmount = new JLabel("数量");
        labelAmount.setBounds(241, 10, 54, 15);
        panel.add(labelAmount);

        JLabel labelStatus = new JLabel("状态");
        labelStatus.setBounds(350, 10, 54, 15);
        panel.add(labelStatus);

        JLabel labelStartDate = new JLabel("运行日期");
        labelStartDate.setBounds(431, 10, 54, 15);
        panel.add(labelStartDate);

        JPanel panelBottom = new JPanel();
        panelBottom.setBounds(38, 35, 511, 125);
        panel.add(panelBottom);
        panelBottom.setLayout(new GridLayout(stockList.size() , 5, 0, 0));

        JButton btnStart = new JButton("启动程序");
        btnStart.setBounds(10, 490, 511, 35);
        frame.getContentPane().add(btnStart);

        btnStart.addActionListener(e -> start());

        for(int i = 0; i < stockList.size(); i++) {
            Stock stock = stockList.get(i);
            labels[i * 5] = new JLabel(stock.getStockCode());
            labels[i * 5 + 1] = new JLabel(stock.dealMethod);
            labels[i * 5 + 2] = new JLabel(stock.dealNum);
            labels[i * 5 + 3] = new JLabel("未成交");
            labels[i * 5 + 4] = new JLabel(stock.startDate);

            panelBottom.add(labels[i * 5]);
            panelBottom.add(labels[i * 5 + 1]);
            panelBottom.add(labels[i * 5 + 2]);
            panelBottom.add(labels[i * 5 + 3]);
            panelBottom.add(labels[i * 5 + 4]);
        }

        //这一句必须放在最后面，用来刷新界面
        frame.setVisible(true);
    }

    private void start(){
        if(!isRunning){
            mainPresenter.start();
            isRunning = true;
        }
    }

    synchronized public void print(String s,int flag){
        if(flag == 1){
            textArea1.append(s);
            textArea1.setCaretPosition(textArea2.getText().length());
        }else{
            textArea2.append(s);
            textArea2.setCaretPosition(textArea2.getText().length());
        }
    }

    public void updateLabel(int index){
        labels[index].setText("已成交");
    }

    public void clearOutput(){
        textArea1.setText("");
    }

}
