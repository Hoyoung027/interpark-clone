package com.interpark_clone.global.exception;

import com.interpark_clone.global.code.GeneralErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final GeneralErrorCode errorCode;

    public GeneralException(GeneralErrorCode generalErrorCode) {
        super(generalErrorCode.getMessage());
        this.errorCode = generalErrorCode;
    }
}
