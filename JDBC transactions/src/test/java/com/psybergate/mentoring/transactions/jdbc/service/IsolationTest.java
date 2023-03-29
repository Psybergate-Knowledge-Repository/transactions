package com.psybergate.mentoring.transactions.jdbc.service;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@NoArgsConstructor
@SpringBootTest
public class IsolationTest {

    @Autowired
    private AccountService accountService;

    @SneakyThrows
    @Test
    public void dirtyReadDemo() {
        final Runnable firstTran = () -> accountService.debitAccountReadUncommittedLevel(82762003036L,
                BigDecimal.valueOf(100),
                5,
                1,
                false);
        final Runnable secondTran = () -> accountService.debitAccountReadUncommittedLevel(82762003036L,
                BigDecimal.valueOf(150),
                0,
                2,
                false);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        firstTranThrd.start();
        //Let first 'transaction' flush changes to DB before beginning second. If transaction 2 sees the flushed change, that is a dirty read
        Thread.sleep(3000);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
        //Despite the dirty read, everything is in order. Are dirty reads so bad?
    }

    @SneakyThrows
    @Test
    public void dirtyReadDemoWithRollback() {
        //Same as above, but first transaction is 'rolled back' after , but this one demonstrates the danger of dirty reads
        final Runnable firstTran = () -> accountService.debitAccountReadUncommittedLevel(82762003036L,
                BigDecimal.valueOf(100),
                1,
                1,
                true);
        final Runnable secondTran = () -> accountService.debitAccountReadUncommittedLevel(82762003036L,
                BigDecimal.valueOf(150),
                0,
                2,
                false);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        firstTranThrd.start();
        //Let first 'transaction' write changes to transaction log before beginning second. If transaction 2 sees the uncommitted change, that is a dirty read
        Thread.sleep(2000);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
    }

    @SneakyThrows
    @Test
    public void dirtyReadAttemptOnReadCommittedIsolationLevel() {
        final Runnable firstTran = () -> accountService.debitAccountReadCommittedLevel(82762003036L,
                BigDecimal.valueOf(100),
                5, //No matter how long we make this wait, transaction 2 always flushes after transaction 1. What does this tell us?
                1, false);
        final Runnable secondTran = () -> accountService.debitAccountReadCommittedLevel(82762003036L,
                BigDecimal.valueOf(150),
                0,
                2, false);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        firstTranThrd.start();
        Thread.sleep(2500);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
    }

    @SneakyThrows
    @Test
    public void dirtyReadAttemptOnReadCommittedIsolationLevelWithRollback() {
        final Runnable firstTran = () -> accountService.debitAccountReadCommittedLevel(82762003036L,
                BigDecimal.valueOf(100),
                5,
                1, true);
        final Runnable secondTran = () -> accountService.debitAccountReadCommittedLevel(82762003036L,
                BigDecimal.valueOf(150),
                0,
                2, false);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        firstTranThrd.start();
        Thread.sleep(1500);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
    }

    @SneakyThrows
    @Test
    public void nonRepeatableReadDemo() {
        final Runnable firstTran = () -> accountService.trackNameChanges(82762003036L,
                5,
                1);
        final Runnable secondTran = () -> accountService.changeAccountName(82762003036L,
                "NEWACC",
                0,
                2);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        firstTranThrd.start();
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        Thread.sleep(1000);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
        accountService.resetAccountName(82762003036L);
    }

    @SneakyThrows
    @Test
    public void nonRepeatableReadAttemptUnderRepeatableReadIsolation() {
        final Runnable firstTran = () -> accountService.trackNameChangesUnderRepeatableRead(82762003036L,
                5,
                1);
        final Runnable secondTran = () -> accountService.changeAccountName(82762003036L,
                "NEWACC",
                0,
                2);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        firstTranThrd.start();
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        Thread.sleep(1000);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
        accountService.resetAccountName(82762003036L);
    }

    @SneakyThrows
    @Test
    public void phantomReadDemo() {
        final Runnable firstTran = () -> accountService.trackAccountHistoryReadCommitted(82762003036L,
                5,
                1);
        final Runnable secondTran = () -> accountService.debitAccountAndCreateHistory(82762003036L,
                BigDecimal.valueOf(100),
                0,
                2);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        firstTranThrd.start();
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        Thread.sleep(1000);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
    }

    @SneakyThrows
    @Test
    public void phantomReadAttemptSerializable() {
        final Runnable firstTran = () -> accountService.trackAccountHistorySerializable(82762003036L,
                10,
                1);
        final Runnable secondTran = () -> accountService.debitAccountAndCreateHistory(82762003036L,
                BigDecimal.valueOf(100),
                0,
                2);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        firstTranThrd.start();
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        Thread.sleep(1000);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
    }
}
