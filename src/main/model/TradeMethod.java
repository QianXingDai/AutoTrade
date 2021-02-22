package main.model;

import java.util.Locale;

public enum TradeMethod {

    S1,S2,S3,S4,S5,
    B1,B2,B3,B4,B5,
    NULL;

    public static TradeMethod getTradeMethod(String tradeMethod){
        switch (tradeMethod.toLowerCase(Locale.ROOT)){
            case "s1" :{
                return S1;
            }
            case "s2" :{
                return S2;
            }
            case "s3" :{
                return S3;
            }
            case "s4" :{
                return S4;
            }
            case "s5" :{
                return S5;
            }
            case "b1" :{
                return B1;
            }
            case "b2" :{
                return B2;
            }
            case "b3" :{
                return B3;
            }
            case "b4" :{
                return B4;
            }
            case "b5" :{
                return B5;
            }
        }
        return NULL;
    }
}
