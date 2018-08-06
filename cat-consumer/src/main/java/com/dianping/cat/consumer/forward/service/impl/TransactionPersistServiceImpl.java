package com.dianping.cat.consumer.forward.service.impl;

import com.dianping.cat.consumer.forward.dao.TransactionDao;
import com.dianping.cat.consumer.forward.entity.TransactionForwardEntity;
import com.dianping.cat.consumer.forward.service.TransactionPersistService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.lookup.annotation.Inject;

import java.util.HashMap;
import java.util.Map;

public class TransactionPersistServiceImpl implements TransactionPersistService, LogEnabled {

    public static final String ID = "transactionPersistService";

    private Logger m_logger;

    @Inject
    private TransactionDao transactionDao;

    @Override
    public void add(TransactionForwardEntity transactionForwardEntity) {

        String tableName;

        Map<String, String> tagMap = new HashMap<>();
        tagMap.put("domain", transactionForwardEntity.getDomain());
        tagMap.put("ip", transactionForwardEntity.getIp());
        tagMap.put("type", transactionForwardEntity.getType());
        tagMap.put("name", transactionForwardEntity.getName());
        tagMap.put("catServer", transactionForwardEntity.getCatServer());

        Map<String, Long> fieldMap = new HashMap<>();
        fieldMap.put("totalCount", transactionForwardEntity.getTotalCount());
        fieldMap.put("failCount", transactionForwardEntity.getFailCount());
        fieldMap.put("sum", transactionForwardEntity.getSum());
        fieldMap.put("avg", transactionForwardEntity.getAvg());

        if ("PigeonCall".equals(transactionForwardEntity.getType()) ||
                "PigeonService".equals(transactionForwardEntity.getType())) {
            String[] nameSplit = StringUtils.split(transactionForwardEntity.getName(), '.');
            String otherSide;
            String facadeName;
            String facadeMethod;
            if (nameSplit.length > 3) {
                otherSide = "Unknown";
                facadeName = nameSplit[nameSplit.length - 2];
                facadeMethod = nameSplit[nameSplit.length - 1];
            } else if (nameSplit.length == 3) {
                otherSide = nameSplit[0];
                facadeName = nameSplit[1];
                facadeMethod = nameSplit[2];
            } else if (nameSplit.length == 2) {
                otherSide = "Unknown";
                facadeName = nameSplit[0];
                facadeMethod = nameSplit[1];
            } else {
                otherSide = "Unknown";
                facadeName = "Unknown";
                facadeMethod = "Unknown";
            }
            tagMap.put("otherSide", otherSide);
            tagMap.put("facadeName", facadeName);
            tagMap.put("facadeMethod", facadeMethod);
        }

        if ("URL".equals(transactionForwardEntity.getType()) || "URL.Forward".equals(transactionForwardEntity.getType())) {
            tableName = "URL";
        } else {
            tableName = transactionForwardEntity.getType();
        }

        transactionDao.insert(transactionForwardEntity.getPeriodTimeStamp(), tagMap, fieldMap, tableName);
    }

    @Override
    public void enableLogging(Logger logger) {
        this.m_logger = logger;
    }
}
