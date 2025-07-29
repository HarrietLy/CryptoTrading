package com.project.harriet.constant;

public class AppConstant {

    public enum PriceDataSource{
        BINANCE,
        HUOBI
    }
    public enum OrderType{
        BUY,
        SELL
    }
    public enum TransactionStatus{
        CREATED,
        PROCESSING,
        COMPLETED,
        FAIL,
        CANCELED,
        TIMEOUT;
    }

    public static final double PRICE_MIN = 0d;
//    public static final double PRICE_MAX ;

}
