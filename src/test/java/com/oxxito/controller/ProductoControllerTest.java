package com.oxxito.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxxito.exception.GlobalExceptionHandler;
import com.oxxito.model.Producto;
import com.oxxito.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService service;

    @InjectMocks
    private ProductoController controller;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listar() throws Exception {
        Producto p1 = new Producto(); p1.setNombre("Gansito");
        Producto p2 = new Producto(); p2.setNombre("Coca Cola");
        when(service.obtenerTodos()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void obtenerExito() throws Exception {
        Producto p = new Producto(); p.setId("1"); p.setNombre("A");
        when(service.obtenerPorId("1")).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("A"));
    }

    @Test
    void obtenerNoEncontrado() throws Exception {
        when(service.obtenerPorId("99")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void crearExito() throws Exception {
        Producto producto = new Producto();
        producto.setNombre("Café Andatti");
        producto.setPrecio(25.50);

        when(service.guardar(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Café Andatti"));
    }

    @Test
    void crearInvalido() throws Exception {
        Producto productoInvalido = new Producto(); 
        
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarExito() throws Exception {
        Producto existente = new Producto(); existente.setId("1");
        Producto actualizado = new Producto(); actualizado.setNombre("Vikingo"); actualizado.setPrecio(20.0);
        
        when(service.obtenerPorId("1")).thenReturn(Optional.of(existente));
        when(service.guardar(any(Producto.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Vikingo"));
    }

    @Test
    void actualizarNoEncontrado() throws Exception {
        Producto actualizado = new Producto(); actualizado.setNombre("Nuevo"); actualizado.setPrecio(10.0);
        when(service.obtenerPorId("99")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/productos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar() throws Exception {
        doNothing().when(service).eliminar("1");

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}