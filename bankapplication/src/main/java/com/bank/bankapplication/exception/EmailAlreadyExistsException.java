package com.bank.bankapplication.exception;
public class EmailAlreadyExistsException
        extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}