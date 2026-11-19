package com.oxxito.controller;

import com.oxxito.dto.AuthRequestDto;
import com.oxxito.dto.AuthResponseDto;
import com.oxxito.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDto request) {
        // Validación de credenciales de prueba
        if ("admin".equals(request.getUsername()) && "oxxito123".equals(request.getPassword())) {
            String token = jwtService.generarToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponseDto(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales invalidas", "mensaje", "Usuario o contraseña incorrectos"));
    }
}