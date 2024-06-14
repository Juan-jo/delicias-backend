package com.delivery.app.security.exceptions;

import com.delivery.app.configs.exception.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UsernameAlreadyExistsDTO extends ApplicationException {
    public UsernameAlreadyExistsDTO(String username) {
        super(
                String.format("%s already exists", username)
        );

        errorCode = "USER_EXCEPTION_00001";
        httpStatus = HttpStatus.CONFLICT;

    }
}
