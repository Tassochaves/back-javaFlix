package com.dev.java_flix.exception;

public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException(String mensagem){
        super(mensagem);
    }
}
