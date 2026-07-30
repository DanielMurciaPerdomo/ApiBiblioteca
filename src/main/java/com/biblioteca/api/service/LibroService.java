package com.biblioteca.api.service;

import com.biblioteca.api.dto.EjemplarResponse;
import com.biblioteca.api.dto.LibroRequest;
import com.biblioteca.api.dto.LibroResponse;
import com.biblioteca.api.exception.ResourceNotFoundException;
import com.biblioteca.api.model.Ejemplar;
import com.biblioteca.api.model.EstadoEjemplar;
import com.biblioteca.api.model.Libro;
import com.biblioteca.api.repository.EjemplarRepository;
import com.biblioteca.api.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;
    private final EjemplarRepository ejemplarRepository;

    @Transactional
    public LibroResponse crear(LibroRequest request) {
        Libro libro = new Libro();
        libro.setTitulo(request.getTitulo());
        libro.setIsbn(request.getIsbn());
        libro.setEdicion(request.getEdicion());
        libro.setFechaPublicacion(request.getFechaPublicacion());
        libro.setAutor(request.getAutor());
        libro = libroRepository.save(libro);

        for (int i = 0; i < request.getCantidadEjemplares(); i++) {
            Ejemplar ejemplar = new Ejemplar();
            ejemplar.setLibro(libro);
            ejemplar.setCodigoInventario(generarCodigoInventario(libro.getId()));
            ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
            ejemplarRepository.save(ejemplar);
        }

        return toResponse(libro);
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> listar() {
        return libroRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LibroResponse obtenerPorId(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", id));
        return toResponse(libro);
    }

    @Transactional
    public LibroResponse actualizar(Long id, LibroRequest request) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", id));
        libro.setTitulo(request.getTitulo());
        libro.setIsbn(request.getIsbn());
        libro.setEdicion(request.getEdicion());
        libro.setFechaPublicacion(request.getFechaPublicacion());
        libro.setAutor(request.getAutor());
        libro = libroRepository.save(libro);
        return toResponse(libro);
    }

    @Transactional
    public void eliminar(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "id", id));
        libroRepository.delete(libro);
    }

    @Transactional(readOnly = true)
    public List<EjemplarResponse> obtenerEjemplaresDisponibles(String isbn) {
        Libro libro = libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "isbn", isbn));
        return ejemplarRepository.findByLibroIsbnAndEstado(isbn, EstadoEjemplar.DISPONIBLE)
                .stream()
                .map(this::toEjemplarResponse)
                .toList();
    }

    private LibroResponse toResponse(Libro libro) {
        int cantidad = ejemplarRepository.findByLibroId(libro.getId()).size();
        return new LibroResponse(
                libro.getId(),
                libro.getTitulo(),
                libro.getIsbn(),
                libro.getEdicion(),
                libro.getFechaPublicacion(),
                libro.getAutor(),
                cantidad
        );
    }

    private EjemplarResponse toEjemplarResponse(Ejemplar ejemplar) {
        return new EjemplarResponse(
                ejemplar.getId(),
                ejemplar.getLibro().getId(),
                ejemplar.getCodigoInventario(),
                ejemplar.getEstado()
        );
    }

    private String generarCodigoInventario(Long libroId) {
        return "INV-" + libroId + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
