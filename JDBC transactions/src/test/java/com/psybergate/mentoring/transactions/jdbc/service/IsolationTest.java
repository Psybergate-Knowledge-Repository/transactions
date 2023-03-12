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

    // Do these in JDBC and leave this as homework
    @SneakyThrows
    @Test
    public void dirtyReadAttemptDemo() {
        //NOTE: This is not a real transaction. But we can imagine it's a transaction for the purposes of demonstrating what a dirty read looks like
        final Runnable firstTran = () -> accountService.debitAccountWithoutTransactionDeclaration(82762003036L,
                BigDecimal.valueOf(100),
                5,
                1,
                false);
        final Runnable secondTran = () -> accountService.debitAccountWithoutTransactionDeclaration(82762003036L,
                BigDecimal.valueOf(150),
                0,
                2,
                false);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        firstTranThrd.start();
        //Let first 'transaction' flush changes to DB before beginning second. If transaction 2 sees the flushed change, that is a dirty read
        Thread.sleep(2000);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
        //Despite the dirty read, everything is in order. Are dirty reads so bad?
    }

    @SneakyThrows
    @Test
    public void dirtyReadAttemptDemoWithRollback() {
        //Same as above, but first transaction is 'rolled back' after , but this one demonstrates the danger of
        final Runnable firstTran = () -> accountService.debitAccountWithoutTransactionDeclaration(82762003036L,
                BigDecimal.valueOf(100),
                1,
                1,
                true);
        final Runnable secondTran = () -> accountService.debitAccountWithoutTransactionDeclaration(82762003036L,
                BigDecimal.valueOf(150),
                0,
                2,
                false);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        firstTranThrd.start();
        //Let first 'transaction' flush changes to DB before beginning second. If transaction 2 sees the flushed change, that is a dirty read
        Thread.sleep(500);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
    }

    @SneakyThrows
    @Test
    public void dirtyReadAttemptOnReadUncommittedIsolationLevel() {
        //Dirty read does not occur because Postgres does not allow dirty reads even on Read uncommitted isolation level
        final Runnable firstTran = () -> accountService.debitAccountReadUncommittedLevel(82762003036L,
                BigDecimal.valueOf(100),
                5, //No matter how long we make this wait, transaction 2 always flushes after transaction 1
                1);
        final Runnable secondTran = () -> accountService.debitAccountReadUncommittedLevel(82762003036L,
                BigDecimal.valueOf(150),
                0,
                2);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        firstTranThrd.start();
        //Let first transaction flush changes to DB before beginning second. If transaction 2 sees the flushed change, that is a dirty read
        Thread.sleep(500);
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();
        //There is another anomaly apparent here.
    }

    @SneakyThrows
    @Test
    public void nonRepeatableReadAttemptDemo() {
        final Runnable firstTran = () -> accountService.debitAccountAndCreateHistory(82762003036L,
                BigDecimal.valueOf(100),
                5,
                1);
        final Runnable secondTran = () -> accountService.debitAccountAndCreateHistory(82762003036L,
                BigDecimal.valueOf(150),
                0,
                2);
        final Thread firstTranThrd = new Thread(firstTran, "firstTranThrd");
        firstTranThrd.start();
        final Thread secondTranThrd = new Thread(secondTran, "secondTranThrd");
        secondTranThrd.start();
        secondTranThrd.join();
        firstTranThrd.join();

    }
}