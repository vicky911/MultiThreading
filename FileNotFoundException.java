package com.card.Exception;

public class FileNotFoundException extends Exception {
    private String filename;

    public FileNotFoundException(String filename) {
        this.filename = filename;
    }

}