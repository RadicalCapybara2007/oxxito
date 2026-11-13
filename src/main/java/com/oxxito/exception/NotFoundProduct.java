package com.oxxito.exception;

public class NotFoundProduct extends RuntimeException {
    public NotFoundProduct(String mensaje) {
        super(mensaje);
    }
}