package com.dianping.cat.consumer.forward.dao;

import java.util.Map;

public interface TransactionDao {

    void insert(long timestamp, Map<String, String> tagMap, Map<String, Long> fieldMap, String measurement);

}
