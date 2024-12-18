package com.delivery.app.mobile.user.exception;

import com.delivery.app.configs.exception.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MobileOrderDifferentTotalAmount extends ApplicationException {
    public MobileOrderDifferentTotalAmount(Double val1, Double val2) {
        super(
                String.format("%s - %s different total", val1, val2)
        );

        errorCode = "MOBILE_ORDER_EXCEPTION_00002";
        httpStatus = HttpStatus.BAD_REQUEST;

    }
}
