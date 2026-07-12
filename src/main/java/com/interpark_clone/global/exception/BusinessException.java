package com.interpark_clone.global.exception;


import com.interpark_clone.global.code.BusinessErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessErrorCode errorCode;

    public BusinessException(BusinessErrorCode businessErrorCode) {
        super(businessErrorCode.getMessage());
        this.errorCode = businessErrorCode;
    }
}
