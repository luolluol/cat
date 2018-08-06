package com.dianping.cat.consumer.forward;

import com.dianping.cat.analysis.AbstractMessageAnalyzer;
import com.dianping.cat.consumer.forward.domain.TransactionForwardDomain;
import com.dianping.cat.consumer.forward.service.ForwardService;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.cat.report.ReportManager;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.lookup.annotation.Inject;

import java.util.List;

public class ForwardAnalyzer extends AbstractMessageAnalyzer<Object> implements LogEnabled {
    public static final String ID = "forward";

    @Inject
    private ForwardService<TransactionForwardDomain> forwardService;

    @Override
    public void doCheckpoint(boolean atEnd) {
        m_logger.info("Forward analyzer checkpoint is completed!");
        // do nothing
    }

    @Override
    public ReportManager<?> getReportManager() {
        return null;
    }

    @Override
    public Object getReport(String domain) {
        throw new UnsupportedOperationException("This should not be called!");
    }

    @Override
    protected void loadReports() {
        // do nothing
    }

    @Override
    protected void process(MessageTree tree) {
        Message message = tree.getMessage();
        if (message instanceof Transaction) {
            String domain = tree.getDomain();
            String ip = tree.getIpAddress();
            TransactionForwardDomain root = new TransactionForwardDomain();
            root.setDomain(domain);
            root.setIp(ip);
            Transaction transaction = (Transaction) message;
            processTransaction(root, transaction);
        }
    }

    private void processTransaction(TransactionForwardDomain root, Transaction transaction) {

        String transactionType = transaction.getType();
        if ("_CatMergeTree".equals(transactionType)) {
            return;
        }
        String domain = root.getDomain();
        if(domain.startsWith("1")){//没有domain名称的抛弃
            return;
        }

        TransactionForwardDomain transactionForwardDomain = new TransactionForwardDomain();
        transactionForwardDomain.setDomain(root.getDomain());
        transactionForwardDomain.setIp(root.getIp());
        transactionForwardDomain.setType(transaction.getType());
        transactionForwardDomain.setName(transaction.getName());
        transactionForwardDomain.setDurationInMillis(transaction.getDurationInMillis());
        transactionForwardDomain.setStatus(transaction.getStatus());
        transactionForwardDomain.setCreationTimestamp(transaction.getTimestamp());
        transactionForwardDomain.setEndTimestamp(transaction.getTimestamp() + transaction.getDurationInMillis());
        forward(transactionForwardDomain);

        List<Message> childs = transaction.getChildren();
        for (Message child : childs) {
            if (child instanceof Transaction) {
                Transaction childTransaction = (Transaction) child;
                processTransaction(root, childTransaction);
            }
        }
    }

    @Override
    public void enableLogging(Logger logger) {
        m_logger = logger;
    }

    private void forward(TransactionForwardDomain transactionForwardDomain) {
        forwardService.forward(transactionForwardDomain);
    }
}
