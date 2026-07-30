package com.biblioteca.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LibroRequest {

    @NotBlank
    @Size(max = 200)
    private String titulo;

    @NotBlank
    @Size(max = 20)
    private String isbn;

    @Size(max = 50)
    private String edicion;

    private LocalDate fechaPublicacion;

    @NotBlank
    @Size(max = 150)
    private String autor;

    @Min(1)
    private int cantidadEjemplares;
}
