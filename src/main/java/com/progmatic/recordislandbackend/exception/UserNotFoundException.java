/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.exception;

/**
 *
 * @author Dano
 */
public class UserNotFoundException extends Exception {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
}
