package com.dev.java_flix.exception;

public class EmailSendingException extends RuntimeException{

    public EmailSendingException(String mensagem, Throwable cause){
        super(mensagem, cause);
    }
}
