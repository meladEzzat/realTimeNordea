package com.nordea.processor;

import com.nordea.publisher.Sink;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Processor implements Observer {

    private static final int NUMBER_OF_THREADS = 8;

    private Sink sink;

    //In case of the component will be deployed in distributed cluster
    //this variable should be moved into distributed cash server EX Redis
    private byte[] salt;

    private Executor executor;

    ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    Lock saltReadLock = rwLock.readLock();
    Lock saltWriteLock = rwLock.writeLock();

    public Processor(Sink sink) {
        this.sink = sink;
        executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    public void onSalt(byte[] salt) {
        saltWriteLock.lock();
        try {
            this.salt = salt;
        } finally {
            saltWriteLock.unlock();
        }
    }

    public void onMessage(long id, byte[] message) {
        saltReadLock.lock();
        try {
            Task task = new Task(id, message, salt, sink);
            executor.execute(task);
        } finally {
            saltReadLock.unlock();
        }
    }
}

class Task implements Runnable {
    private byte[] message;
    private byte[] salt;
    private long id;
    private Sink sink;

    public Task(long id, byte[] message, byte[] salt, Sink sink) {
        this.id = id;
        this.message = message;
        this.salt = salt;
        this.sink = sink;
    }

    public void run() {

        System.out.println(Thread.currentThread().getName());

        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);

            long start = System.currentTimeMillis();
            for (int i = 0; i < 5000; i++)
                hash = digest.digest(message);

            long end = System.currentTimeMillis();

            System.out.println(end - start);

            sink.publishHash(id, message, salt, hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
