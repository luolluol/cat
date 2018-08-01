package com.dianping.cat.consumer.forward.service;

import com.dianping.cat.consumer.forward.entity.TransactionForwardEntity;
import com.dianping.cat.consumer.forward.service.impl.TransactionPersistServiceImpl;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransactionServiceTest extends ComponentTestCase {


    @Test
    public void test() throws IOException {
        TransactionPersistService transactionPersistService = lookup(TransactionPersistService.class, TransactionPersistServiceImpl.ID);

        List<TransactionForwardEntity> transactionForwardEntityList = new ArrayList<>();

        for (int i = 0; i < 100000; i++) {
            TransactionForwardEntity t = new TransactionForwardEntity();
            t.setDomain("domain_" + getRandomStr(1));
            t.setIp("127.0.0.1");
            t.setType("type_" + getRandomStr(1));
            t.setName("name" + getRandomStr(2));
            t.setCatServer("127.0.0.1");
            t.setTotalCount(getRandomNumber());
            t.setFailCount(getRandomNumber());
            t.setSum(getRandomNumber());
            t.setPeriodTimeStamp(i);
            transactionForwardEntityList.add(t);
        }

        long start = System.currentTimeMillis();
        for (TransactionForwardEntity transactionForwardEntity : transactionForwardEntityList) {
            transactionPersistService.add(transactionForwardEntity);
        }
        long end = System.currentTimeMillis();
        System.out.println("cost time: " + (end - start) + "ms");
        System.in.read();
    }

    private long getRandomNumber() {
        Random random = new Random();
        return random.nextLong();
    }

    private String getRandomStr(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(52);// [0,51)
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
