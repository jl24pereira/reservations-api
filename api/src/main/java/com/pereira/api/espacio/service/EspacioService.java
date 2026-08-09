package com.pereira.api.espacio.service;

import java.util.UUID;

import com.pereira.api.espacio.domain.Espacio;
import com.pereira.api.espacio.dto.CreateEspacioRequest;
import com.pereira.api.espacio.dto.EspacioResponse;
import com.pereira.api.espacio.dto.UpdateEspacioRequest;
import com.pereira.api.espacio.repository.EspacioRepository;
import com.pereira.api.shared.exception.EspacioNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@Service
@AllArgsConstructor
public class EspacioService {

    private final EspacioRepository repository;

    @Transactional(readOnly = true)
    public Page<EspacioResponse> list(Pageable pageable, boolean includeInactivos) {
        Page<Espacio> page = includeInactivos ? repository.findAll(pageable) : repository.findByActivoTrue(pageable);

        return page.map(EspacioResponse::from);
    }

    @Transactional(readOnly = true)
    public EspacioResponse get(UUID id) {
        return EspacioResponse.from(find(id));

    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EspacioResponse create(CreateEspacioRequest request) {
        Espacio espacio = new Espacio();
        espacio.setNombre(request.nombre());
        espacio.setTipo(request.tipo());
        espacio.setCapacidad(request.capacidad());
        espacio.setUbicacion(request.ubicacion());
        espacio.setTarifaHora(request.tarifaHora());
        espacio.setActivo(true);
        return EspacioResponse.from(repository.save(espacio));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EspacioResponse update(UUID id, UpdateEspacioRequest request) {
        Espacio espacio = find(id);
        espacio.setNombre(request.nombre());
        espacio.setTipo(request.tipo());
        espacio.setCapacidad(request.capacidad());
        espacio.setUbicacion(request.ubicacion());
        espacio.setTarifaHora(request.tarifaHora());
        espacio.setActivo(request.activo());
        return EspacioResponse.from(espacio);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivate(UUID id) {
        find(id).setActivo(false);
    }

    private Espacio find(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EspacioNotFoundException(id));
    }
}
