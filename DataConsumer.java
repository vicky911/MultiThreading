package com.card.dao;

public class DataConsumer implements Runnable {
    private final CSVFileProcessor processor;

    public DataConsumer(CSVFileProcessor dataProcessor) {
        this.processor =dataProcessor;
    }

    @Override
    public void run() {
        processor.fileProcess();
        System.out.println("Consumer "+Thread.currentThread().getName());
        /*Thread t[]=new Thread[Thread.activeCount()];
        int count = Thread.enumerate(t);
        System.out.println("Threads in ThreadGroup: " + threadGroup.getName());
        for (int i = 0; i < count; i++) {
            System.out.println("At "+t[i].getName()+"--"+t[i].getState());
        }*/
    }


}
