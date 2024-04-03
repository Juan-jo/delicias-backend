package com.delivery.app.advisor;

import com.delivery.app.configs.exception.ApplicationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Builder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

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

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ProblemDetail> handleApplicationException(ApplicationException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
        problemDetail.setTitle(ex.getTitle());
        problemDetail.setProperty("code", ex.getErrorCode());
        return ResponseEntity.status(ex.getHttpStatus()).body(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, "");

        problemDetail.setTitle("Unique key or database integrity violation.");
        problemDetail.setProperty("code", "CONFLICT");

        Pattern pattern = Pattern.compile("\\\"([\\w]+)");
        Matcher matcher = pattern.matcher(Optional.ofNullable(Objects.requireNonNull(ex.getRootCause()).getMessage()).orElse(""));

        if(matcher.find()) {
            problemDetail.setProperty("violation", matcher.group(1));
        }

        return ResponseEntity.status(CONFLICT).body(problemDetail);
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

