package main.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {

    public static List<Integer> delayTimeList;
    public static List<Stock> stockList;
    public static String title;

    public static void init(){
        loadSettingConfig();
        loadStockConfig();
    }

    private static void loadSettingConfig(){
        if(delayTimeList == null){
            delayTimeList = new ArrayList<>();
        }
        BufferedReader br = null;

        try {
            String filePath = "config.txt";
            StringBuilder sb = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String temp;

            while((temp = br.readLine()) != null){
                sb.append(temp);
                sb.append("\n");
            }

            temp = sb.toString().trim();
            Matcher m = Pattern.compile("=\"\\S+\"").matcher(temp);
            while(m.find()){
                String s = m.group();
                s = s.substring(2,s.length() - 1);
                if(isInteger(s)){
                    delayTimeList.add(Integer.parseInt(s));
                }else{
                    title = s;
                }
            }
        } catch (Exception e) {
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

    private static void loadStockConfig(){
        if(stockList == null){
            stockList = new ArrayList<>();
        }
        String filePath = "stock.txt";
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String temp;

            while ((temp = br.readLine()) != null){
                String[] ss = temp.trim().split("\\s+");
                Stock stock = new Stock(ss[0],ss[1],ss[2],ss[3]);
                stockList.add(stock);
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

    private static boolean isInteger(String s){
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(s).matches();
    }
}
