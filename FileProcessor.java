package com.card.dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileProcessor {
    private static final int NUM_CONSUMERS = 3;
    public static final String FILE_PATH = "G:\\Miit\\multithreading\\CardValidation\\transaction.csv";
    public static final String OUTPUT_DIRECTORY = "G:\\Miit\\multithreading\\CardValidation\\";
    private CSVFileProcessor processor;
    public FileProcessor(CSVFileProcessor processor) {
        this.processor=processor;

    }

    public void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_CONSUMERS + 1);

        executorService.submit(new DataProducer(processor));
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            executorService.submit(new DataConsumer(processor));
        }
        executorService.shutdown();
    }
}
