package com.dianping.cat.consumer.forward.factory.impl;

import com.dianping.cat.consumer.forward.factory.InfluxDBClientHolder;
import com.dianping.cat.consumer.forward.factory.InfluxDBConfig;
import com.dianping.cat.consumer.forward.util.ConfigLoaderUtil;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class InfluxdbClientHolderImpl implements InfluxDBClientHolder, LogEnabled, Initializable, Disposable {

    public static final String ID = "influxdbClientHolder";

    private Logger m_logger;

    private InfluxDBConfig influxdbConfig;

    private Map<String, InfluxDB> enableBatchHolderMap = new ConcurrentHashMap<>();
    private Map<String, InfluxDB> holderMap = new ConcurrentHashMap<>();

    @Override
    public void enableLogging(Logger logger) {
        this.m_logger = logger;
    }

    @Override
    public void initialize() throws InitializationException {
        Properties properties = null;
        try {
            properties = ConfigLoaderUtil.loadPropertis("/data/appdatas/cat/influxdb.properties");
        } catch (IOException e) {
            m_logger.error("配置异常！");
        }

        if (null != properties) {
            influxdbConfig = new InfluxDBConfig();
            influxdbConfig.setUrl(properties.getProperty("url"));
            influxdbConfig.setUsername(properties.getProperty("username"));
            influxdbConfig.setPassword(properties.getProperty("password"));
            influxdbConfig.setDatabase(properties.getProperty("database"));
            String udpPort = properties.getProperty("udpPort");
            if (null != udpPort && udpPort.length() != 0 && udpPort.matches("\\d*")) {
                influxdbConfig.setUdpPort(Integer.valueOf(udpPort));
            }
        }
    }

    @Override
    public void dispose() {
        m_logger.info("influxdb客户端关闭！待关闭客户端数量" + holderMap.keySet().size());
        holderMap.values().forEach(InfluxDB::close);
    }

    @Override
    public InfluxDB getClient(Boolean enableBatch) {
        if (null == enableBatch) {
            enableBatch = Boolean.FALSE;
        }

        String database = influxdbConfig.getDatabase();

        if (enableBatch) {
            InfluxDB influxDB = enableBatchHolderMap.get(database);
            if (null == influxDB) {
                synchronized (this) {
                    influxDB = enableBatchHolderMap.get(database);
                    if (null == influxDB) {
                        influxDB = InfluxDBFactory.connect(influxdbConfig.getUrl(), influxdbConfig.getUsername(), influxdbConfig.getPassword());
                        influxDB.setDatabase(database);
                        influxDB.enableBatch();
                        enableBatchHolderMap.put(database, influxDB);
                    }
                }
            }
            return influxDB;
        } else {
            InfluxDB influxDB = holderMap.get(database);
            if (null == influxDB) {
                synchronized (this) {
                    influxDB = holderMap.get(database);
                    if (null == influxDB) {
                        influxDB = InfluxDBFactory.connect(influxdbConfig.getUrl(), influxdbConfig.getUsername(), influxdbConfig.getPassword());
                        influxDB.setDatabase(database);
                        holderMap.put(database, influxDB);
                    }
                }
            }
            return influxDB;
        }

    }

    @Override
    public Integer getUdpPort() {
        return influxdbConfig.getUdpPort();
    }
}
