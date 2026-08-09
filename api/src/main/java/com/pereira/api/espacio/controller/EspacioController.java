package com.pereira.api.espacio.controller;

import java.net.URI;
import java.text.MessageFormat;
import java.util.UUID;

import com.pereira.api.espacio.dto.CreateEspacioRequest;
import com.pereira.api.espacio.dto.EspacioResponse;
import com.pereira.api.espacio.dto.UpdateEspacioRequest;
import com.pereira.api.espacio.service.EspacioService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@RestController
@RequestMapping("/espacios")
@AllArgsConstructor
public class EspacioController {

    private final EspacioService service;

    @GetMapping
    public Page<EspacioResponse> list(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable,
            @RequestParam(defaultValue = "false") boolean includeInactivos,
            Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return service.list(pageable, includeInactivos && isAdmin);
    }

    @GetMapping("/{id}")
    public EspacioResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<EspacioResponse> create(@Valid @RequestBody CreateEspacioRequest request) {
        EspacioResponse created = service.create(request);
        return ResponseEntity.created(URI.create(MessageFormat.format("/espacios/{0}", created.id()))).body(created);
    }

    @PutMapping("/{id}")
    public EspacioResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateEspacioRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
