package com.dianping.cat.consumer.forward.factory;

import org.influxdb.InfluxDB;

public interface InfluxDBClientHolder {
    InfluxDB getClient(Boolean enableBatch);

    Integer getUdpPort();
}
