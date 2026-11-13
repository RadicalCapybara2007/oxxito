package com.oxxito;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OxxitoApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring cargue correctamente
    }

    @Test
    void mainTest() {
        OxxitoApplication.main(new String[]{});
    }
}