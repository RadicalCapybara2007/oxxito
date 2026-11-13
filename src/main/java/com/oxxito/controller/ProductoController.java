package com.oxxito.controller;

import com.oxxito.dto.ProductoDto;
import com.oxxito.model.Producto;
import com.oxxito.service.ProductoService;
import com.oxxito.exception.NotFoundProduct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable String id) {
        Producto producto = service.obtenerPorId(id)
                .orElseThrow(() -> new NotFoundProduct("Producto no encontrado con el id: " + id));
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoDto productoDto) {
        Producto producto = new Producto();
        producto.setNombre(productoDto.getNombre());
        producto.setPrecio(productoDto.getPrecio());
        return new ResponseEntity<>(service.guardar(producto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable String id, @Valid @RequestBody ProductoDto productoDto) {
        Optional<Producto> existente = service.obtenerPorId(id);
        if (existente.isPresent()) {
            Producto producto = existente.get();
            producto.setNombre(productoDto.getNombre());
            producto.setPrecio(productoDto.getPrecio());
            return ResponseEntity.ok(service.guardar(producto));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}