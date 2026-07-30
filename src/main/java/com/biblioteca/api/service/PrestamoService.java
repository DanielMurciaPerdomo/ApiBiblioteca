package com.biblioteca.api.service;

import com.biblioteca.api.dto.PrestamoRequest;
import com.biblioteca.api.dto.PrestamoResponse;
import com.biblioteca.api.exception.BusinessRuleException;
import com.biblioteca.api.exception.ResourceNotFoundException;
import com.biblioteca.api.model.Ejemplar;
import com.biblioteca.api.model.EstadoEjemplar;
import com.biblioteca.api.model.EstadoPrestamo;
import com.biblioteca.api.model.Libro;
import com.biblioteca.api.model.Prestamo;
import com.biblioteca.api.model.Usuario;
import com.biblioteca.api.repository.EjemplarRepository;
import com.biblioteca.api.repository.LibroRepository;
import com.biblioteca.api.repository.PrestamoRepository;
import com.biblioteca.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final EjemplarRepository ejemplarRepository;

    @Transactional
    public PrestamoResponse registrar(PrestamoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.getUsuarioId()));

        if (prestamoRepository.countByUsuarioIdAndEstado(usuario.getId(), EstadoPrestamo.ACTIVO) > 0) {
            throw new BusinessRuleException("El usuario ya tiene un préstamo activo");
        }

        if (prestamoRepository.countByUsuarioIdAndEstado(usuario.getId(), EstadoPrestamo.VENCIDO) > 0) {
            throw new BusinessRuleException("El usuario ya tiene un préstamo vencido, por favor devolver primero");
        }

        Libro libro = libroRepository.findByIsbn(request.getIsbn())
                .orElseThrow(() -> new ResourceNotFoundException("Libro", "isbn", request.getIsbn()));

        Ejemplar ejemplar = ejemplarRepository
                .findFirstByLibroIdAndEstado(libro.getId(), EstadoEjemplar.DISPONIBLE)
                .orElseThrow(() -> new BusinessRuleException("No hay ejemplares disponibles para el ISBN: " + request.getIsbn()));

        ejemplar.setEstado(EstadoEjemplar.PRESTADO);
        ejemplarRepository.save(ejemplar);

        int dias = (request.getDiasPrestamo() != null) ? request.getDiasPrestamo() : 15;

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setEjemplar(ejemplar);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(dias));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        prestamo = prestamoRepository.save(prestamo);

        return toResponse(prestamo);
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarTodos() {
        return prestamoRepository.findAll().stream()
                .map(this::recalcularEstado)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario", "id", usuarioId);
        }
        return prestamoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::recalcularEstado)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorUsuarioYLibro(Long usuarioId, Long libroId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario", "id", usuarioId);
        }
        if (!libroRepository.existsById(libroId)) {
            throw new ResourceNotFoundException("Libro", "id", libroId);
        }
        return prestamoRepository.findByUsuarioIdAndEjemplarLibroId(usuarioId, libroId).stream()
                .map(this::recalcularEstado)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorLibro(Long libroId) {
        if (!libroRepository.existsById(libroId)) {
            throw new ResourceNotFoundException("Libro", "id", libroId);
        }
        return prestamoRepository.findByEjemplarLibroId(libroId).stream()
                .map(this::recalcularEstado)
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PrestamoResponse devolver(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo", "id", prestamoId));

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);

        Ejemplar ejemplar = prestamo.getEjemplar();
        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
        ejemplarRepository.save(ejemplar);

        prestamo = prestamoRepository.save(prestamo);
        return toResponse(prestamo);
    }

    private Prestamo recalcularEstado(Prestamo prestamo) {
        if (prestamo.getFechaDevolucionReal() != null) {
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        } else if (LocalDate.now().isAfter(prestamo.getFechaDevolucionEsperada())) {
            prestamo.setEstado(EstadoPrestamo.VENCIDO);
        } else {
            prestamo.setEstado(EstadoPrestamo.ACTIVO);
        }
        return prestamo;
    }

    private PrestamoResponse toResponse(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getUsuario().getId(),
                prestamo.getEjemplar().getId(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionEsperada(),
                prestamo.getFechaDevolucionReal(),
                prestamo.getEstado()
        );
    }
}
