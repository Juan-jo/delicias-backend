package com.delivery.app.pos.order.exception;

import com.delivery.app.configs.exception.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidProcessStatusOrder extends ApplicationException {
    public InvalidProcessStatusOrder(Integer orderId, String currentStatus, String newStatus) {
        super(
                String.format("Can't process status from %s to %s. %orderId", currentStatus, newStatus, orderId)
        );

        errorCode = "ºKANBAN_EXCEPTION_00001";
        httpStatus = HttpStatus.BAD_REQUEST;
    }
}
