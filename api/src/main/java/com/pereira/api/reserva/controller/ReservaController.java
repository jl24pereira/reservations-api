package com.pereira.api.reserva.controller;

import java.net.URI;
import java.text.MessageFormat;
import java.util.UUID;

import com.pereira.api.reserva.dto.ConfirmarReservaRequest;
import com.pereira.api.reserva.dto.CreateReservaRequest;
import com.pereira.api.reserva.dto.FilterReservaRequest;
import com.pereira.api.reserva.dto.ReservaResponse;
import com.pereira.api.reserva.service.ConfirmacionReservaService;
import com.pereira.api.reserva.service.ReservaService;
import com.pereira.api.security.model.AuthenticatedUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@RestController
@RequestMapping("/reservas")
@AllArgsConstructor
public class ReservaController {

    private final ReservaService service;
    private final ConfirmacionReservaService confirmacionService;

    @PostMapping
    public ResponseEntity<ReservaResponse> create(
            @Valid @RequestBody CreateReservaRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {

        ReservaResponse create = service.create(request, user.getId());

        return ResponseEntity
                .created(URI.create(MessageFormat.format("/reservas/", create.id())))
                .body(create);
    }

    @PostMapping("/{id}/confirmar")
    public ReservaResponse confirm(
            @PathVariable UUID id,
            @Valid @RequestBody ConfirmarReservaRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return confirmacionService.confirm(id, request.paymentMethod(), user.getId(), isAdmin(user));
    }

    @GetMapping
    public Page<ReservaResponse> list(
            FilterReservaRequest filter,
            @PageableDefault(size = 20, sort = "inicio") Pageable pageable,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.list(filter, pageable, user.getId(), isAdmin(user));
    }

    @GetMapping("/{id}")
    public ReservaResponse get(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.get(id, user.getId(), isAdmin(user));
    }

    @DeleteMapping("/{id}")
    public ReservaResponse cancel(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return service.cancel(id, user.getId(), isAdmin(user));
    }

    private boolean isAdmin(AuthenticatedUser user) {
        return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

}
