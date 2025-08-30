package com.farmatodo.challenge.adapters.in.web.ping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/v1")
@Tag(name = "Ping", description = "API para verificar el estado del servicio")
public class PingController {
    /**
     * Verifica que el servicio est  activo y devuelve un mensaje
     * simple con el contenido "pong".
     * @return un objeto ResponseEntity con estado 200 y contenido
     * "pong".
     */
    @GetMapping("/ping")
    @Operation(summary = "Verifica que el servicio esté activo")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
