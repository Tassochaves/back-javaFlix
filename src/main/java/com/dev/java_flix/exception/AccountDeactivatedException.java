package com.dev.java_flix.exception;

public class AccountDeactivatedException extends RuntimeException{

    public AccountDeactivatedException(String mensagem){
        super(mensagem);
    }
}
