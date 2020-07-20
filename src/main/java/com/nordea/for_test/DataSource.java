package com.nordea.for_test;

import com.nordea.Solution;
import com.nordea.processor.Observer;
import com.nordea.publisher.Publisher;
import com.nordea.publisher.Sink;
import com.nordea.source.Source;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataSource implements Source {

    private List<Observer> observers = new ArrayList<Observer>();

    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    public void raiseMessage(long id, byte[] message) {
        for (Observer observer : observers) {
            observer.onMessage(id, message);
        }
    }

    public void raiseSalt(byte[] salt) {
        for (Observer observer : observers) {
            observer.onSalt(salt);
        }
    }

    public static void main(String[] args) {
        DataSource dataSource = new DataSource();
        Sink sink = new Publisher();
        Solution solution = new Solution(dataSource, sink);
        solution.start();

        //Simulating data source raising salt and messages with random values
        dataSource.raiseSalt(RandomStringUtils.randomAlphanumeric(20).getBytes());
        for (int i = 0; i < 1000; i++) {
            Random rnd = new Random();
            dataSource.raiseMessage(Math.abs(rnd.nextLong()), RandomStringUtils.randomAlphanumeric(100).getBytes());

            if (i % 10 == 0)
                dataSource.raiseSalt(RandomStringUtils.randomAlphanumeric(20).getBytes());
        }
    }
}

