package com.oxxito.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductoDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    private Double precio;

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}