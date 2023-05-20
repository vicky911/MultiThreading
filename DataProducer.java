package com.card.dao;

import java.io.FileNotFoundException;

public class DataProducer implements Runnable {

    private final CSVFileProcessor processor;

    public DataProducer(CSVFileProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void run() {

        try {
            processor.readFileAndAddToQueue();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Producer");
    }
}
