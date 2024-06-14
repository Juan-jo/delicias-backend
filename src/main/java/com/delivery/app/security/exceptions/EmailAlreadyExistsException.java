package com.delivery.app.security.exceptions;

import com.delivery.app.configs.exception.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailAlreadyExistsException extends ApplicationException {
    public EmailAlreadyExistsException(String email) {
        super(
                String.format("%s already exists", email)
        );

        errorCode = "USER_EXCEPTION_00002";
        httpStatus = HttpStatus.CONFLICT;

    }
}
