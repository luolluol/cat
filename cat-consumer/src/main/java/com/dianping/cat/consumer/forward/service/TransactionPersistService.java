package com.dianping.cat.consumer.forward.service;

import com.dianping.cat.consumer.forward.entity.TransactionForwardEntity;

public interface TransactionPersistService {

    void add(TransactionForwardEntity transactionForwardEntity);

}
