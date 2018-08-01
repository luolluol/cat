package com.dianping.cat.consumer.forward.dao.impl;

import com.dianping.cat.consumer.forward.dao.TransactionDao;
import com.dianping.cat.consumer.forward.factory.InfluxDBClientHolder;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.unidal.lookup.annotation.Inject;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TransactionDaoImpl implements TransactionDao {

    public static final String ID = "transactionDao";

    @Inject
    private InfluxDBClientHolder influxdbClientHolder;

    @Override
    public void insert(long timestamp, Map<String, String> tagMap, Map<String, Long> fieldMap, String measurement) {
        InfluxDB influxDB = influxdbClientHolder.getClient(Boolean.TRUE);
        Point.Builder pointBuilder = Point.measurement(measurement).time(timestamp, TimeUnit.MILLISECONDS);
        tagMap.forEach(pointBuilder::tag);
        fieldMap.forEach(pointBuilder::addField);
        Point point = pointBuilder.build();
        if (null != influxdbClientHolder.getUdpPort()) {
            influxDB.write(influxdbClientHolder.getUdpPort(), point.lineProtocol());
        } else {
            influxDB.write(point);
        }
    }

}
