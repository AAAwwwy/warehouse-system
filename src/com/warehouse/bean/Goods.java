package com.warehouse.bean;

public class Goods {
    private int goodsId;
    private String goodsName;
    private int stockNum;
    private String goodsType;

    // 无参构造、有参构造、get/set方法
    public Goods() {}
    public Goods(int goodsId, String goodsName, int stockNum, String goodsType) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.stockNum = stockNum;
        this.goodsType = goodsType;
    }

    public int getGoodsId() { return goodsId; }
    public void setGoodsId(int goodsId) { this.goodsId = goodsId; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public int getStockNum() { return stockNum; }
    public void setStockNum(int stockNum) { this.stockNum = stockNum; }
    public String getGoodsType() { return goodsType; }
    public void setGoodsType(String goodsType) { this.goodsType = goodsType; }
}