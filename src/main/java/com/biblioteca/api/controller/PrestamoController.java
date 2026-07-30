package com.biblioteca.api.controller;

import com.biblioteca.api.dto.PrestamoRequest;
import com.biblioteca.api.dto.PrestamoResponse;
import com.biblioteca.api.service.PrestamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<PrestamoResponse> registrar(@Valid @RequestBody PrestamoRequest request) {
        PrestamoResponse response = prestamoService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PrestamoResponse>> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long libroId) {
        if (usuarioId != null && libroId != null) {
            return ResponseEntity.ok(prestamoService.listarPorUsuarioYLibro(usuarioId, libroId));
        }
        if (usuarioId != null) {
            return ResponseEntity.ok(prestamoService.listarPorUsuario(usuarioId));
        }
        if (libroId != null) {
            return ResponseEntity.ok(prestamoService.listarPorLibro(libroId));
        }
        return ResponseEntity.ok(prestamoService.listarTodos());
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<PrestamoResponse> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.devolver(id));
    }
}
