package com.ggc.study_juc.comsumer_producer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程通信之生产者消费者阻塞队列版
 */
public class ProducerConsumerByBlockQueueDemo {


    public static void main(String[] args) throws InterruptedException {

        MyResource myResource = new MyResource(new ArrayBlockingQueue<>(10));

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t 生产线程启动");
            try {
                myResource.myProd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Producer").start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t 消费线程启动");
            System.out.println();
            System.out.println();
            try {
                myResource.myConsumer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Consumer").start();

        TimeUnit.SECONDS.sleep(5);

        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("5秒钟时间到，大老板main线程叫停，活动结束");

        myResource.stop();

    }

    static class MyResource {
        private BlockingQueue<String> blockingQueue = null;
        private volatile boolean FLAG = true;//默认开启，进行生产+消费

        private AtomicInteger atomicInteger = new AtomicInteger();

        public MyResource(BlockingQueue<String> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        void myProd() throws Exception {

            String data = null;
            boolean retValue;

            while (FLAG) {
                data = atomicInteger.incrementAndGet() + "";
                retValue = blockingQueue.offer(data, 2, TimeUnit.SECONDS);
                if (retValue) {
                    System.out.println(Thread.currentThread().getName() + "\t 插入队列 " + data + " 成功");
                } else {
                    System.out.println(Thread.currentThread().getName() + "\t 插入队列 " + data + " 失败");
                }
                TimeUnit.SECONDS.sleep(1);
            }
            System.out.println(Thread.currentThread().getName() + "\t大老板叫停了，表示Flag = false，生产动作结束");
        }

        void myConsumer() throws InterruptedException {
            String result = null;

            while (FLAG) {
                result = blockingQueue.poll(2L, TimeUnit.SECONDS);
                if (result == null || result.equals("")) {
                    FLAG = false;
                    System.out.println(Thread.currentThread().getName() + "\t 超过2秒钟没有取到蛋糕，消费退出");
                    System.out.println();
                    System.out.println();
                    return;
                }
                System.out.println(Thread.currentThread().getName() + "\t 消费队列" + result + "成功");
            }
        }

        void stop() {
            this.FLAG = false;
        }

    }


}


