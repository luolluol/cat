package com.dianping.cat.consumer.forward;

import com.dianping.cat.Constants;
import com.dianping.cat.analysis.MessageAnalyzer;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.internal.DefaultTransaction;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.cat.message.spi.internal.DefaultMessageTree;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ForwardAnalyzerTest extends ComponentTestCase {

    private long m_timestamp;

    private ForwardAnalyzer m_analyzer;

    private String m_domain = "group";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        m_timestamp = System.currentTimeMillis() - System.currentTimeMillis() % (3600 * 1000);
        m_analyzer = (ForwardAnalyzer) lookup(MessageAnalyzer.class, ForwardAnalyzer.ID);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
        Date date = sdf.parse("20120101 00:00");

        m_analyzer.initialize(date.getTime(), Constants.HOUR, Constants.MINUTE * 5);
    }

    @Test
    public void testProcess() throws Exception {
        for (int i = 1; i <= 100000; i++) {
            MessageTree tree = generateMessageTree(i);
            if(i % 10 == 0){
                Thread.sleep(10);
            }
            m_analyzer.process(tree);
        }

        System.in.read();
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

    protected MessageTree generateMessageTree(int i) {
        MessageTree tree = new DefaultMessageTree();

        tree.setMessageId("" + i);
        tree.setDomain(m_domain);
        tree.setHostName("group001");
        tree.setIpAddress("192.168.1.1");

        String type = getRandomStr(1);
        String name = getRandomStr(2);
        DefaultTransaction t = new DefaultTransaction(type, name, null);
        DefaultTransaction t2 = new DefaultTransaction(type + "-1", name + i % 3, null);

        if (i % 2 == 0) {
            t2.setStatus("ERROR");
        } else {
            t2.setStatus(Message.SUCCESS);
        }

        t2.complete();
        t2.setDurationInMillis(i);

        t.addChild(t2);

        if (i % 2 == 0) {
            t.setStatus("ERROR");
        } else {
            t.setStatus(Message.SUCCESS);
        }

        t.complete();
        t.setDurationInMillis(i * 2);
        t.setTimestamp(System.currentTimeMillis());
        t2.setTimestamp(System.currentTimeMillis() + 100);
        tree.setMessage(t);

        return tree;
    }
}
