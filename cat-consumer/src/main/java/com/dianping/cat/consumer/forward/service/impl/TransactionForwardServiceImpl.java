package com.dianping.cat.consumer.forward.service.impl;

import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.cat.consumer.forward.domain.TransactionForwardDomain;
import com.dianping.cat.consumer.forward.entity.TransactionForwardEntity;
import com.dianping.cat.consumer.forward.service.ForwardService;
import com.dianping.cat.consumer.forward.service.TransactionPersistService;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class TransactionForwardServiceImpl implements ForwardService<TransactionForwardDomain>, LogEnabled, Initializable {

    public static final String ID = "transactionForwardService";

    private Logger m_logger;

    private Map<String, MinuteStatistics> minuteStatisticsMap;

    private ThreadPoolExecutor threadPoolExecutor;

    private BlockingQueue delayQueue;

    @Inject
    private TransactionPersistService transactionPersistService;

    private String m_ip = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();

    @Override
    public int forward(TransactionForwardDomain transactionForwardDomain) {
        long periodTimeStamp = transactionForwardDomain.getEndTimestamp() - transactionForwardDomain.getEndTimestamp() % 60000;
        transactionForwardDomain.setPeriodTimeStamp(periodTimeStamp);

        long now = System.currentTimeMillis();
        if (periodTimeStamp + 70000 < now) {//保护策略，按分钟分片后，70秒以前的分片消息丢弃
            m_logger.info("失效消息丢弃！ " + transactionForwardDomain);
            return 0;
        }

        String key = KEY_GENERATOR_TRANSACTIONFORWARDDOMAIN.apply(transactionForwardDomain);
        MinuteStatistics minuteStatistics = minuteStatisticsMap.get(key);

        if (null == minuteStatistics) {
            synchronized (this) {
                minuteStatistics = minuteStatisticsMap.get(key);
                if (null == minuteStatistics) {
                    minuteStatistics = new MinuteStatistics();
                    minuteStatistics.setPeriodTimestamp(periodTimeStamp);
                    minuteStatistics.setExecuteTimestamp(minuteStatistics.getPeriodTimestamp() + 70000);//分片后70s执行入库
                    minuteStatistics.setDomain(transactionForwardDomain.getDomain());
                    minuteStatistics.setIp(transactionForwardDomain.getIp());
                    minuteStatistics.setType(transactionForwardDomain.getType());
                    minuteStatistics.setName(transactionForwardDomain.getName());
                    minuteStatistics.setCatServer(m_ip);
                    minuteStatisticsMap.put(key, minuteStatistics);
                    threadPoolExecutor.execute(minuteStatistics);
                }
            }
        }

        minuteStatistics.incTotalCount(1);
        if (!"0".equals(transactionForwardDomain.getStatus())) {
            minuteStatistics.incFailCount(1);
        }

        minuteStatistics.incSum(transactionForwardDomain.getDurationInMillis());

        return 1;
    }

    @Override
    public void enableLogging(Logger logger) {
        m_logger = logger;
    }

    @Override
    public void initialize() throws InitializationException {
        minuteStatisticsMap = Collections.synchronizedMap(new LinkedHashMap<>());
        delayQueue = new DelayQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, delayQueue,
                new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPoolExecutor.prestartAllCoreThreads();
    }

    private class MinuteStatistics implements Delayed, Runnable {
        private long periodTimestamp;
        private long executeTimestamp;
        private String domain;
        private String ip;
        private String type;
        private String name;
        private String catServer;
        private AtomicLong totalCount;
        private AtomicLong failCount;
        private AtomicLong sum;

        public MinuteStatistics() {
            this.totalCount = new AtomicLong();
            this.failCount = new AtomicLong();
            this.sum = new AtomicLong();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(executeTimestamp - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            MinuteStatistics msg = (MinuteStatistics) o;
            return Long.compare(this.executeTimestamp, msg.executeTimestamp);
        }

        @Override
        public void run() {
            TransactionForwardEntity transactionForwardEntity = new TransactionForwardEntity();
            transactionForwardEntity.setPeriodTimeStamp(this.periodTimestamp);
            transactionForwardEntity.setDomain(this.domain);
            transactionForwardEntity.setIp(this.ip);
            transactionForwardEntity.setType(this.type);
            transactionForwardEntity.setName(this.name);
            transactionForwardEntity.setCatServer(this.catServer);
            transactionForwardEntity.setTotalCount(this.totalCount.get());
            transactionForwardEntity.setFailCount(this.failCount.get());
            transactionForwardEntity.setSum(this.sum.get());
            if (this.totalCount.get() > 0) {
                transactionForwardEntity.setAvg(this.sum.get() / this.totalCount.get());
            } else {
                transactionForwardEntity.setAvg(0);
            }
            minuteStatisticsMap.remove(KEY_GENERATOR_MINUTESTATISTICS.apply(this));
            transactionPersistService.add(transactionForwardEntity);
        }

        public long getPeriodTimestamp() {
            return periodTimestamp;
        }

        public MinuteStatistics setPeriodTimestamp(long periodTimestamp) {
            this.periodTimestamp = periodTimestamp;
            return this;
        }

        public long getExecuteTimestamp() {
            return executeTimestamp;
        }

        public MinuteStatistics setExecuteTimestamp(long executeTimestamp) {
            this.executeTimestamp = executeTimestamp;
            return this;
        }

        public String getDomain() {
            return domain;
        }

        public MinuteStatistics setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public String getIp() {
            return ip;
        }

        public MinuteStatistics setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public String getType() {
            return type;
        }

        public MinuteStatistics setType(String type) {
            this.type = type;
            return this;
        }

        public String getName() {
            return name;
        }

        public MinuteStatistics setName(String name) {
            this.name = name;
            return this;
        }

        public String getCatServer() {
            return catServer;
        }

        public MinuteStatistics setCatServer(String catServer) {
            this.catServer = catServer;
            return this;
        }

        public AtomicLong getTotalCount() {
            return totalCount;
        }

        public AtomicLong getFailCount() {
            return failCount;
        }

        public AtomicLong getSum() {
            return sum;
        }

        public MinuteStatistics incTotalCount(long count) {
            this.totalCount.addAndGet(count);
            return this;
        }

        public MinuteStatistics incFailCount(long count) {
            this.failCount.addAndGet(count);
            return this;
        }

        public MinuteStatistics incSum(long duration) {
            this.sum.addAndGet(duration);
            return this;
        }
    }

    private Function<TransactionForwardDomain, String> KEY_GENERATOR_TRANSACTIONFORWARDDOMAIN = new Function<TransactionForwardDomain, String>() {
        @Override
        public String apply(TransactionForwardDomain transactionForwardDomain) {
            return transactionForwardDomain.getDomain() + "-" + transactionForwardDomain.getIp() + "-"
                    + transactionForwardDomain.getType() + "-" + transactionForwardDomain.getName() + "-" + transactionForwardDomain.getPeriodTimeStamp();
        }
    };

    private Function<MinuteStatistics, String> KEY_GENERATOR_MINUTESTATISTICS = new Function<MinuteStatistics, String>() {
        @Override
        public String apply(MinuteStatistics minuteStatistics) {
            return minuteStatistics.getDomain() + "-" + minuteStatistics.getIp() + "-" + minuteStatistics.getType()
                    + "-" + minuteStatistics.getName() + "-" + minuteStatistics.getCatServer() + "-" + minuteStatistics.getPeriodTimestamp();
        }
    };
}
