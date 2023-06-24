package com.jss.routes.rabbitmq;

import java.util.Random;

public class ThreadLocalExample {

    // Create a ThreadLocal variable to store a counter for each thread
    private static ThreadLocal<Integer> counter =
            new ThreadLocal<Integer>() {
                @Override
                protected Integer initialValue() {
                    return new Random().nextInt(100);
                }
            };

    public static void main(String[] args) throws InterruptedException {
        // Create three threads
        Thread t1 = new Thread(new MyRunnable());
        Thread t2 = new Thread(new MyRunnable());
        Thread t3 = new Thread(new MyRunnable());

        // Start the threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for the threads to finish
        t1.join();
        t2.join();
        t3.join();
    }

    public static class MyRunnable implements Runnable {
        @Override
        public void run() {
            // Get the current thread's counter value
            int value = counter.get();

            // Increment the counter and set the new value
            counter.set(value + 1);

            // Print the counter value for this thread
            System.out.println(
                    "Thread "
                            + Thread.currentThread().getName()
                            + " counter value: "
                            + counter.get());
        }
    }
}
