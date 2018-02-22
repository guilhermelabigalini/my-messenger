/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.api;

/**
 *
 * @author guilherme
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
    }

    public InvalidTokenException(String message) {
        super(message);
    }
    
}
