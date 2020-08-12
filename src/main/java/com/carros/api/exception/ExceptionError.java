package com.carros.api.exception;

import java.io.Serializable;

class ExceptionError implements Serializable {
    private String error;
    ExceptionError(String error) {
        this.error = error;
    }
    public String getError() {
        return error;
    }
}
