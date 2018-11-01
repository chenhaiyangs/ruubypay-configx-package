package com.ruubypay.framework.configx.test;

import com.ruubypay.framework.configx.Encrypt;
import com.ruubypay.framework.configx.encrypt.impl.EncryptByAes;
import com.ruubypay.framework.configx.encrypt.impl.EncryptByDes;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class CihperTest {

    private Encrypt encrypt;
    private ExecutorService service = Executors.newFixedThreadPool(100);

    @Test
    public void test() throws Exception {
        int threadNum =100;
        encrypt = new EncryptByAes("a0f574ec51441f3e98011491f28b584d");

        CountDownLatch downLatch = new CountDownLatch(100);
        for(int i=0;i<threadNum;i++){
            service.submit(new Thread(new EncryptThread(i,downLatch)));
            downLatch.countDown();
        }
        LockSupport.park();


    }

    class EncryptThread implements Runnable{

        private int number;
        private CountDownLatch latch;

        private EncryptThread(int i, CountDownLatch downLatch) {
            this.number=i;
            this.latch=downLatch;
        }

        @Override
        public void run() {
            try {
                System.out.println("线程"+Thread.currentThread().getName()+"就绪");
                latch.await();
                String msg="原文"+number;
                String result = encrypt.encrypt(msg);
                String src =encrypt.decrypt(result);
                System.out.println(msg+","+result+","+src+","+src.equals(msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
