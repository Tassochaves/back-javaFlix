package com.dev.java_flix.exception;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(String mensagem){
        super(mensagem);
    }
}
