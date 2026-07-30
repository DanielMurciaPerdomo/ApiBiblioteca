package com.biblioteca.api.repository;

import com.biblioteca.api.model.Ejemplar;
import com.biblioteca.api.model.EstadoEjemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {

    Optional<Ejemplar> findFirstByLibroIdAndEstado(Long libroId, EstadoEjemplar estado);

    List<Ejemplar> findByLibroIsbnAndEstado(String isbn, EstadoEjemplar estado);

    List<Ejemplar> findByLibroId(Long libroId);

    long countByLibroIdAndEstado(Long libroId, EstadoEjemplar estado);
}
