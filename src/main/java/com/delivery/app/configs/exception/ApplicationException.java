package com.delivery.app.configs.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {

    protected String title;
    protected  String errorCode;
    protected HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public ApplicationException(String message) { super(message); }

}
