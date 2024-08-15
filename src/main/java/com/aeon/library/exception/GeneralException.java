package com.aeon.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class GeneralException extends RuntimeException {
    public GeneralException(String message) {
        super(message);
    }
}
