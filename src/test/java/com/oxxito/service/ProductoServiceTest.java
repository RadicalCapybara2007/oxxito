package com.oxxito.service;

import com.oxxito.exception.NotFoundProduct;
import com.oxxito.model.Producto;
import com.oxxito.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    @Test
    void guardar_DeberiaRetornarProductoGuardado() {
        Producto producto = new Producto();
        producto.setNombre("Gansito");
        producto.setPrecio(20.0);

        when(repository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = service.guardar(producto);

        assertNotNull(resultado);
        assertEquals("Gansito", resultado.getNombre());
        verify(repository, times(1)).save(producto);
    }

    @Test
    void obtenerPorId_DeberiaLanzarExcepcion_CuandoNoExiste() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThrows(NotFoundProduct.class, () -> {
            service.obtenerPorId("999").orElseThrow(() -> new NotFoundProduct("No existe"));
        });
    }
}