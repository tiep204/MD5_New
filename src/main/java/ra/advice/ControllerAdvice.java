package ra.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import javax.persistence.EntityExistsException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> registerFail(EntityExistsException entityExistsException){
        return new ResponseEntity<>(entityExistsException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}