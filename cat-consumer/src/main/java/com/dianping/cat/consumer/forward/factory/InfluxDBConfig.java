package com.dianping.cat.consumer.forward.factory;

public class InfluxDBConfig {

    private String url;
    private String username;
    private String password;
    private String database;
    private Integer udpPort;

    public String getUrl() {
        return url;
    }

    public InfluxDBConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public InfluxDBConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public InfluxDBConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDatabase() {
        return database;
    }

    public InfluxDBConfig setDatabase(String database) {
        this.database = database;
        return this;
    }

    public Integer getUdpPort() {
        return udpPort;
    }

    public InfluxDBConfig setUdpPort(Integer udpPort) {
        this.udpPort = udpPort;
        return this;
    }
}
