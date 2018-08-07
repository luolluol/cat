package com.dianping.cat.consumer.forward;

import org.junit.Test;
import org.unidal.dal.jdbc.DalException;
import org.unidal.lookup.ComponentTestCase;

import java.util.Date;

public class TransactionSqlDaoTest extends ComponentTestCase {

    @Test
    public void test() throws DalException {
        TransactionSqlDao dao = lookup(TransactionSqlDao.class);
        TransactionSql proto = new TransactionSql();

        proto.setPeriod(new Date());
        proto.setDomain("domain");
        proto.setIp("ip");
        proto.setName("name");
        proto.setCatServer("catserver");
        proto.setCreationDate(new Date());
        dao.insert(proto);
    }

}
