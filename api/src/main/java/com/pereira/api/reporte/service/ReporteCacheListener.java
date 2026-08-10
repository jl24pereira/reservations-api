package com.pereira.api.reporte.service;

import com.pereira.api.reserva.event.ReservaCanceladaEvent;
import com.pereira.api.reserva.event.ReservaConfirmadaEvent;
import com.pereira.api.shared.config.CacheConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jose Luis Pereira
 */
@Component
public class ReporteCacheListener {

    private static final Logger log = LoggerFactory.getLogger(ReporteCacheListener.class);

    @EventListener
    @CacheEvict(cacheNames = CacheConfig.CACHE_OCUPACION, allEntries = true)
    public void alConfirmarReserva(ReservaConfirmadaEvent evento) {
        log.debug("Cache de ocupacion invalidado por confirmacion de reserva {}", evento.reservaId());
    }

    @EventListener
    @CacheEvict(cacheNames = CacheConfig.CACHE_OCUPACION, allEntries = true)
    public void alCancelarReserva(ReservaCanceladaEvent evento) {
        log.debug("Cache de ocupacion invalidado por cancelacion de reserva {}", evento.reservaId());
    }

}
