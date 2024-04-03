package com.delivery.app.configs.exception.common;

import com.delivery.app.configs.exception.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends ApplicationException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;


    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {

        super(
                String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue)
        );

        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;

        errorCode = "NOT_FOUND";
        httpStatus = HttpStatus.NOT_FOUND;
    }
}
