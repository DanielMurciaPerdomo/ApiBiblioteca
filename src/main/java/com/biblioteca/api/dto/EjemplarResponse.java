package com.biblioteca.api.dto;

import com.biblioteca.api.model.EstadoEjemplar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EjemplarResponse {

    private Long id;
    private Long libroId;
    private String codigoInventario;
    private EstadoEjemplar estado;
}
