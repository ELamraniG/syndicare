package com.syndicare.exceptions;

import java.lang.IllegalArgumentException;

public class InvalidRequestException extends IllegalArgumentException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
