package com.dev.java_flix.exception;

public class InvalidRoleException extends RuntimeException{

    public InvalidRoleException(String mensagem){
        super(mensagem);
    }
}
