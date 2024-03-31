package com.delivery.app.advisor;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Builder;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

    @Builder
    private record InvalidatedParams (String cause, String field) {}

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Request validation failed");
        problemDetail.setTitle("Validation Failed");
        problemDetail.setProperty("invalidParams", buildValidationErrors(ex.getConstraintViolations()));
        problemDetail.setProperty("code", "G00001");
        return ResponseEntity.status(BAD_REQUEST).body(problemDetail);

    }

    private List<InvalidatedParams> buildValidationErrors(Set<ConstraintViolation<?>> violations) {
        return violations.
                stream().
                map(violation ->
                        InvalidatedParams.builder().
                                field(
                                        Objects.requireNonNull(StreamSupport.stream(
                                                                violation.getPropertyPath().spliterator(), false).
                                                        reduce((first, second) -> second).
                                                        orElse(null)).
                                                toString()
                                ).
                                cause(violation.getMessage()).
                                build()).
                collect(toList());
    }

}

