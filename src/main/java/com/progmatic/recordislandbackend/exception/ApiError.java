package com.progmatic.recordislandbackend.exception;

import java.util.Arrays;
import java.util.List;

public class ApiError {
    
    private int code;
    private String status;
    private List<String> errors;

    public ApiError() {
    }

    public ApiError(int code, String status, List<String> errors) {
        this.code = code;
        this.status = status;
        this.errors = errors;
    }
    
    public ApiError(int code, String status, String... errors) {
        this.code = code;
        this.status = status;
        this.errors = Arrays.asList(errors);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}