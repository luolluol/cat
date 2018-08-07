package com.dianping.cat.consumer.forward.entity;

import com.alibaba.fastjson.JSONObject;

public class TransactionForwardEntity {

    private String domain;
    private String ip;
    private String type;
    private String name;
    private String catServer;
    private long periodTimeStamp;
    private long totalCount;
    private long failCount;
    private long sum;
    private long avg;

    public String getDomain() {
        return domain;
    }

    public TransactionForwardEntity setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public TransactionForwardEntity setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getType() {
        return type;
    }

    public TransactionForwardEntity setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public TransactionForwardEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getCatServer() {
        return catServer;
    }

    public TransactionForwardEntity setCatServer(String catServer) {
        this.catServer = catServer;
        return this;
    }

    public long getPeriodTimeStamp() {
        return periodTimeStamp;
    }

    public TransactionForwardEntity setPeriodTimeStamp(long periodTimeStamp) {
        this.periodTimeStamp = periodTimeStamp;
        return this;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public TransactionForwardEntity setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public long getFailCount() {
        return failCount;
    }

    public TransactionForwardEntity setFailCount(long failCount) {
        this.failCount = failCount;
        return this;
    }

    public long getSum() {
        return sum;
    }

    public TransactionForwardEntity setSum(long sum) {
        this.sum = sum;
        return this;
    }

    public long getAvg() {
        return avg;
    }

    public void setAvg(long avg) {
        this.avg = avg;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
