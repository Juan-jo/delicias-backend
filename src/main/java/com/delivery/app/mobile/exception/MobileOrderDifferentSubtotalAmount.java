package com.delivery.app.mobile.exception;

import com.delivery.app.configs.exception.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MobileOrderDifferentSubtotalAmount extends ApplicationException {
    public MobileOrderDifferentSubtotalAmount(Double val1, Double val2) {
        super(
                String.format("%s - %s different subtotal", val1, val2)
        );

        errorCode = "MOBILE_ORDER_EXCEPTION_00001";
        httpStatus = HttpStatus.BAD_REQUEST;

    }
}
