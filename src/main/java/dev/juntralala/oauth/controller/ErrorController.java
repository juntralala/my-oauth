package dev.juntralala.oauth.controller;

import dev.juntralala.oauth.dto.RestResponse;
import dev.juntralala.oauth.exception.HttpResponseValidationException;
import dev.juntralala.oauth.exception.ValidationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ErrorController {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Void>> handleHibernateValidation(MethodArgumentNotValidException e) {
        MultiValueMap<String, String> errors = new LinkedMultiValueMap<>();
        e.getFieldErrors()
                .forEach(fieldError -> errors.add(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(400)
                .body(RestResponse
                        .<Void>builder()
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<RestResponse<Void>> handleValidationException(ValidationException e) {
        return ResponseEntity.status(400)
                .body(RestResponse
                        .<Void>builder()
                        .error(e.getMessage())
                        .build());
    }

    @ExceptionHandler(HttpResponseValidationException.class)
    public ResponseEntity<RestResponse<Void>> handleHttpResponseValidationException(HttpResponseValidationException e) {
        Map<String, List<String>> errors = e.getErrors();
        return ResponseEntity.status(400)
                .body(RestResponse
                        .<Void>builder()
                        .errors(errors)
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestResponse<Void>> handleConstraintValidationException(ConstraintViolationException e) {
        MultiValueMap<String, String> errors = new LinkedMultiValueMap<>();
        e.getConstraintViolations()
                .forEach(violation -> errors.add(violation.getPropertyPath().toString(), violation.getMessage()));
        return ResponseEntity.status(400)
                .body(RestResponse
                        .<Void>builder()
                        .errors(errors)
                        .build());
    }

}
