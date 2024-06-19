package com.delivery.app.configs.exception.common;

import com.delivery.app.configs.exception.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidAccessResourceException extends ApplicationException {

    private final String resourceName;
    public InvalidAccessResourceException(String resourceName,  Integer id) {

        super(
                String.format("Invalid access resource : '%s'", id)
        );

        this.resourceName = resourceName;

        errorCode = "GEN_FOUND";
        httpStatus = HttpStatus.BAD_REQUEST;
    }
}
