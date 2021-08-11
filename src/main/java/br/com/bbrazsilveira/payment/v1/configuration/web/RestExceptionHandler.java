package br.com.bbrazsilveira.payment.v1.configuration.web;

import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(MethodArgumentNotValidException ex) {
        ErrorResponse.SubError[] errors = ex.getBindingResult().getAllErrors().stream().map(error -> {
            String field = ((FieldError) error).getField();
            field = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field);
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            String message = error.getDefaultMessage();
            return new ErrorResponse.SubError(field, rejectedValue, message);
        }).toArray(ErrorResponse.SubError[]::new);

        return new ErrorResponse(400, "Bad Request", "Validation failed.", errors);
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private final LocalDateTime timestamp = LocalDateTime.now();
        private int status;
        private String error;
        private String message;
        private SubError[] subErrors;

        public ErrorResponse(int status, String error, String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }

        @Data
        @AllArgsConstructor
        public static class SubError {
            private String field;
            private Object rejectedValue;
            private String message;
        }
    }
}
