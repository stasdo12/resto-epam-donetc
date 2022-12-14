package com.epam.donetc.restaurant.exception;

public class DBException extends Exception{

    public DBException(String message, Throwable cause){
        super(message, cause);
    }

    public DBException(String message){
        super(message);
    }

}