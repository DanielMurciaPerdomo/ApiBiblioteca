package com.biblioteca.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibroResponse {

    private Long id;
    private String titulo;
    private String isbn;
    private String edicion;
    private LocalDate fechaPublicacion;
    private String autor;
    private int cantidadEjemplares;
}
