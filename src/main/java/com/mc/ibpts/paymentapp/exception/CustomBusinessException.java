package com.mc.ibpts.paymentapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomBusinessException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final String message;

    public CustomBusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
