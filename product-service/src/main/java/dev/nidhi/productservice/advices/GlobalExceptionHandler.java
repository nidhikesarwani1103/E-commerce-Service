package dev.nidhi.productservice.advices;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public String handleException(Exception e) {
        return "Something went wrong: " + e.getMessage();
    }

    @ExceptionHandler
    public String handleRuntimeException(RuntimeException e) {
        return "Runtime exception occurred: " + e.getMessage();
    }

    @ExceptionHandler
    public String handleIllegalArgumentException(IllegalArgumentException e){
        return e.getMessage();
    }
}
