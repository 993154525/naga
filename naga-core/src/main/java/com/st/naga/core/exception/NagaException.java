package com.st.naga.core.exception;

import lombok.Getter;

/**
 * @author ShaoTian
 * @date 2020/11/11 15:10
 */
@Getter
public class NagaException extends RuntimeException {

    private Integer errorCodes;

    private String errorMessages;

    public NagaException(Throwable cause, Integer errorCodes, String errorMessages) {
        super(cause);
        this.errorCodes = errorCodes;
        this.errorMessages = errorMessages;
    }

    public NagaException(String errorMessages, int errorCodes) {
        this.errorCodes = errorCodes;
        this.errorMessages = errorMessages;
    }
}
