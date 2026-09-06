// warehouse/bean/OperRecord.java
package com.warehouse.bean;

import java.util.Date;

public class OperRecord {
    // 对应数据库的record_id
    private int recordId;
    private int goodsId;
    private String goodsName; // 冗余字段，方便显示
    private String operType; // in:入库, out:出库, add:添加, delete:删除
    private int operNum;
    private Date operTime;

    // 空构造方法
    public OperRecord() {}

    // 带参构造方法
    public OperRecord(int goodsId, String operType, int operNum) {
        this.goodsId = goodsId;
        this.operType = operType;
        this.operNum = operNum;
    }

    // getter和setter方法（全部适配recordId）
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public int getOperNum() {
        return operNum;
    }

    public void setOperNum(int operNum) {
        this.operNum = operNum;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }
}