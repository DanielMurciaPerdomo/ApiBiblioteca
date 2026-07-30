package com.biblioteca.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PrestamoRequest {

    @NotNull
    private Long usuarioId;

    @NotBlank
    @Size(max = 20)
    private String isbn;
}
