package com.mc.ibpts.paymentapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomBusinessException extends RuntimeException{

    private HttpStatus httpStatus;
    private String message;
    private Throwable exception;

    public CustomBusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public CustomBusinessException(HttpStatus httpStatus, String message, Throwable exception) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
        this.exception = exception;
    }
}
