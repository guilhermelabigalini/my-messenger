/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.api;

import mymessenger.backend.services.ValidationException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author guilherme
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorMessage handleException(ValidationException ex) {
        LOGGER.error(ex.toString());
        
        ErrorMessage errorMessage = new ErrorMessage(ex.getMessage());
        return errorMessage;
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    ErrorMessage handleException(InvalidTokenException ex) {
        LOGGER.error(ex.toString());
        ErrorMessage errorMessage = new ErrorMessage("Invalid token");
        return errorMessage;
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorMessage handleException(Exception ex) {
        LOGGER.error(ex.toString());
        ErrorMessage errorMessage = new ErrorMessage("Internal error");
        return errorMessage;
    }
}
