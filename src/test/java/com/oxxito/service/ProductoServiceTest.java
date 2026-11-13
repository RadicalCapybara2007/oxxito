package com.oxxito.service;

import com.oxxito.model.Producto;
import com.oxxito.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
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
    void obtenerTodos() {
        Producto p1 = new Producto(); p1.setNombre("Gansito");
        Producto p2 = new Producto(); p2.setNombre("Coca Cola");
        when(repository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Producto> resultado = service.obtenerTodos();
        assertEquals(2, resultado.size());
    }

    @Test
    void obtenerPorId() {
        Producto p = new Producto(); p.setId("1");
        when(repository.findById("1")).thenReturn(Optional.of(p));

        Optional<Producto> resultado = service.obtenerPorId("1");
        assertTrue(resultado.isPresent());
        assertEquals("1", resultado.get().getId());
    }

    @Test
    void guardar() {
        Producto p = new Producto(); p.setNombre("Papas");
        when(repository.save(any(Producto.class))).thenReturn(p);

        Producto resultado = service.guardar(p);
        assertNotNull(resultado);
        assertEquals("Papas", resultado.getNombre());
    }

    @Test
    void eliminar() {
        doNothing().when(repository).deleteById("1");
        service.eliminar("1");
        verify(repository, times(1)).deleteById("1");
    }
}