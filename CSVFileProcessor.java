package com.card.dao;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;


public class CSVFileProcessor {
    private final Queue<String> queue;
    public CSVFileProcessor() {

        this.queue=new LinkedList<>();
    }

    public synchronized void fileProcess() {
        FileWriter debitWriter = null;
        FileWriter creditWriter = null;
        FileWriter errorWriter= null;
        try {
            debitWriter = new FileWriter(FileProcessor.OUTPUT_DIRECTORY + "debit.csv");
            creditWriter = new FileWriter(FileProcessor.OUTPUT_DIRECTORY + "credit.csv");
            errorWriter = new FileWriter(FileProcessor.OUTPUT_DIRECTORY + "error.csv");

            dataProcessor(debitWriter, creditWriter, errorWriter);


        } catch (IOException e) {
            e.printStackTrace();
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

        finally {
            closeWriter(debitWriter);
            closeWriter(creditWriter);
            closeWriter(errorWriter);
            /*Thread t=Thread.currentThread();
            try {
                t.join(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            //   System.out.printf(queue.peek());
            System.out.println("Consumer: Finished processing lines."+Thread.currentThread().getName());

            //System.exit(0);
        }
    }

    private void dataProcessor(FileWriter debitWriter, FileWriter creditWriter, FileWriter errorWriter) throws InterruptedException, IOException {
        String result=new String();
        while (true) {
            System.out.println("Breaking ::");
            synchronized (queue) {
                while (queue.isEmpty() && !result.equals("EOF")) {
                    try {
                        System.out.println("Waiting::");

                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Queue size: "+queue.size());
                result = queue.poll();
                notifyAll();
            }
            String line = result;

            if (line.equals("EOF")) {

                System.out.println("Reached AT EOF of QUEUE "+Thread.currentThread().getName());
                notifyAll();
                Thread[] t =new Thread[Thread.activeCount()];
                int count = Thread.enumerate(t);
                for (int i = 0; i < count; i++) {

                    System.out.println("R "+t[i].getName()+"--"+t[i].getState());
                }
                //Thread.currentThread().interrupt();
                break;
            }
           // else {
                validateData(debitWriter, creditWriter, errorWriter, line);
            //}
        }
    }

    private void validateData(FileWriter debitWriter, FileWriter creditWriter, FileWriter errorWriter, String line) throws IOException {
        if (isValidTransaction(line)) {
            String[] data = line.split(",",-1);
            String type = data[4].trim();

            //credit validation
            if (type.equalsIgnoreCase("credit")) {
                creditWriter.write(line);
                creditWriter.write("\n");
              /*  System.out.println("Credit Process."+Thread.currentThread().getName());
                Thread[] t =new Thread[Thread.activeCount()];
                int count = Thread.enumerate(t);
                for (int i = 0; i < count; i++) {

                    System.out.println("C "+t[i].getName()+"--"+t[i].getState());
                }*/
            }

            //debit validation
            else if (type.equalsIgnoreCase("debit")) {
                debitWriter.write(line);
                debitWriter.write("\n");
                System.out.println("Debit Process."+Thread.currentThread().getName());
               /* Thread[] t =new Thread[Thread.activeCount()];
                int count = Thread.enumerate(t);
                for (int i = 0; i < count; i++) {

                    System.out.println("D "+t[i].getName()+"--"+t[i].getState());
                }*/
            }

            //error
            else {
                errorWriter.write(line);
                errorWriter.write("\n");
                System.out.println("Error Process."+Thread.currentThread().getName());
                /*Thread[] t =new Thread[Thread.activeCount()];
                int count = Thread.enumerate(t);
                for (int i = 0; i < count; i++) {

                    System.out.println("E "+t[i].getName()+"--"+t[i].getState());
                }*/
            }
        } else {
            errorWriter.write(line);
            errorWriter.write("\n");
        }
    }


    private boolean isValidTransaction(String line) {
        String[] data = line.split(",");
        if (data.length < 4) {
            return false;
        }

        String id = data[0].trim();
        String category = data[1].trim();
        String amount = data[3].trim();

        try {
            Long.parseLong(id);
            Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            return false;
        }

        return !id.isEmpty() && !category.isEmpty() && !amount.isEmpty();
    }


    public synchronized void readFileAndAddToQueue(  ) throws FileNotFoundException {

        BufferedReader br=null;
        FileReader fr=null;
        try{
            fr=new FileReader(FileProcessor.FILE_PATH);
            br=new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                synchronized (queue) {
                    queue.add(line);
                    queue.notifyAll();
                }
            }

            synchronized (queue) {
                queue.add("EOF");
                queue.notifyAll();
                System.out.println("Producer: Finished reading file. "+Thread.currentThread().getName());
            }

            br.close();
        }
        catch (FileNotFoundException e) {
            throw new FileNotFoundException("File "+FileProcessor.FILE_PATH+" not found");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            closeReader(br);

        }
    }

    private void closeWriter(FileWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void closeReader(BufferedReader br) {
        try {
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
