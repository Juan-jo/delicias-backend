package com.delivery.app.mobile.exception;

import com.delivery.app.configs.exception.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DeliveryZoneInvalidException extends ApplicationException {

    public DeliveryZoneInvalidException(String message) {

        super(
                "Delivery Zone Not Found"
        );

        errorCode = "MOBILE_DELIVERY_ZONE_NOT_FOUND";
        httpStatus = HttpStatus.BAD_REQUEST;
    }
}
