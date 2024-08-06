package com.delivery.app.security.exceptions;

import com.delivery.app.configs.exception.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserRootException extends ApplicationException {

    public UserRootException() {
        super("User role is ROOT");

        errorCode = "USER_EXCEPTION_ROOT";
        httpStatus = HttpStatus.BAD_REQUEST;

    }
}
