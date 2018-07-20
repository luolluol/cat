package com.dianping.cat.consumer.forward.dao.impl;

import com.dianping.cat.consumer.forward.dao.TransactionDao;
import com.dianping.cat.consumer.forward.entity.TransactionForwardEntity;
import com.dianping.cat.consumer.forward.factory.InfluxDBClientHolder;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.unidal.lookup.annotation.Inject;

import java.util.concurrent.TimeUnit;

public class TransactionDaoImpl implements TransactionDao {

    public static final String ID = "transactionDao";

    @Inject
    private InfluxDBClientHolder influxdbClientHolder;

    @Override
    public void insert(TransactionForwardEntity transactionForwardEntity) {
        InfluxDB influxDB = influxdbClientHolder.getClient(Boolean.TRUE);
        Point point = Point.measurement("transaction").time(transactionForwardEntity.getPeriodTimeStamp(), TimeUnit.MILLISECONDS)
                .tag("domain", transactionForwardEntity.getDomain())
                .tag("ip", transactionForwardEntity.getIp())
                .tag("type", transactionForwardEntity.getType())
                .tag("name", transactionForwardEntity.getName())
                .tag("catServer", transactionForwardEntity.getCatServer())
                .addField("totalCount", transactionForwardEntity.getTotalCount())
                .addField("failCount", transactionForwardEntity.getFailCount())
                .addField("sum", transactionForwardEntity.getSum())
                .build();

        if (null != influxdbClientHolder.getUdpPort()) {
            influxDB.write(influxdbClientHolder.getUdpPort(), point.lineProtocol());
        } else {
            influxDB.write(point);
        }
    }

}
