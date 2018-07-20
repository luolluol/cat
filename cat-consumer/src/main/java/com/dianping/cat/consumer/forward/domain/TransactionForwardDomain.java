package com.dianping.cat.consumer.forward.domain;

public class TransactionForwardDomain {

    private String domain;
    private String ip;
    private String type;
    private String name;
    private String status;
    private long durationInMillis;
    private long creationTimestamp;

    public String getDomain() {
        return domain;
    }

    public TransactionForwardDomain setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public TransactionForwardDomain setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getType() {
        return type;
    }

    public TransactionForwardDomain setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public TransactionForwardDomain setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public TransactionForwardDomain setStatus(String status) {
        this.status = status;
        return this;
    }

    public long getDurationInMillis() {
        return durationInMillis;
    }

    public TransactionForwardDomain setDurationInMillis(long durationInMillis) {
        this.durationInMillis = durationInMillis;
        return this;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public TransactionForwardDomain setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    @Override
    public String toString() {
        return "domain=" + domain +
                ",ip=" + ip +
                ",type=" + type +
                ",name=" + name +
                ",status=" + status +
                ",durationInMillis=" + durationInMillis +
                ",creationTimestamp=" + creationTimestamp;
    }
}
