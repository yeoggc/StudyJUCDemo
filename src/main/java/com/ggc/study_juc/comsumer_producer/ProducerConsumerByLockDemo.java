package com.ggc.study_juc.comsumer_producer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者消费者模式 by Lock + await和signal 实现
 * <p>
 * 生活例子：两个人对空调温度进行调节
 * <p>
 * 题目：一个初始值为零的变量，两个线程对其交替操作，一个加1，一个减1，来5轮
 * <p>
 * 1    线程  操作(方法)  资源类
 * 2    判断  干活  通知
 * 3    防止虚假唤醒机制
 */
public class ProducerConsumerByLockDemo {

    public static void main(String[] args) {
        ShareData shareData = new ShareData();

        new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                shareData.increment();
            }
        }, "AA").start();

        new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                shareData.decrement();
            }
        }, "BB").start();

    }

    static class ShareData {//资源类

        private int number = 0;
        private Lock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();


        public void increment() {
            lock.lock();
            try {
                //1. 判断
                while (number != 0) {
                    //等待，不能生产
                    condition.await();
                }

                //2. 干活
                number++;
                System.out.println(Thread.currentThread().getName() + "\t" + number);

                //3. 通知唤醒
                condition.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void decrement() {
            lock.lock();
            try {
                //1. 判断
                while (number == 0) {
                    //等待，不能生产
                    condition.await();
                }

                //2. 干活
                number--;
                System.out.println(Thread.currentThread().getName() + "\t" + number);

                //3. 通知唤醒
                condition.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

    }

}
