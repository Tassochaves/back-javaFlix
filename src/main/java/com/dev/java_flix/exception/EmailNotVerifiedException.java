package com.dev.java_flix.exception;

public class EmailNotVerifiedException extends RuntimeException{

    public EmailNotVerifiedException(String mensagem){
        super(mensagem);
    }
}
