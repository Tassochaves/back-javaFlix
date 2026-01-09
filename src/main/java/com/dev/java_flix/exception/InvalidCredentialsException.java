package com.dev.java_flix.exception;

public class InvalidCredentialsException extends RuntimeException{

    public InvalidCredentialsException(String mensagem){
        super(mensagem);
    }
}
