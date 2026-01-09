package com.dev.java_flix.exception;

public class BadCredentialsException extends RuntimeException{

    public BadCredentialsException(String mensagem){
        super(mensagem);
    }
}
