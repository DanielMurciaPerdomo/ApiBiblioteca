package com.biblioteca.api.repository;

import com.biblioteca.api.model.EstadoPrestamo;
import com.biblioteca.api.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    long countByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    List<Prestamo> findByUsuarioId(Long usuarioId);

    List<Prestamo> findByEjemplarLibroId(Long libroId);
}
