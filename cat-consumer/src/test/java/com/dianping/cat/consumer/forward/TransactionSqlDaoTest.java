package com.dianping.cat.consumer.forward;

import org.junit.Test;
import org.unidal.dal.jdbc.DalException;
import org.unidal.lookup.ComponentTestCase;

import java.util.Date;

public class TransactionSqlDaoTest extends ComponentTestCase {

    @Test
    public void test() throws DalException {
        TransactionSqlDao dao = lookup(TransactionSqlDao.class);
        long start = System.currentTimeMillis();
        for(int i=0;i<10000;i++){
            TransactionSql proto = new TransactionSql();
            proto.setPeriod(new Date());
            proto.setDomain("domain");
            proto.setIp("ip");
            proto.setName("name");
            proto.setCatServer("catserver");
            proto.setCreationDate(new Date());
            dao.insert(proto);
        }
        long end = System.currentTimeMillis();
        long cost = end - start;
        System.out.println("cost: " + cost + " ms");
    }

}
