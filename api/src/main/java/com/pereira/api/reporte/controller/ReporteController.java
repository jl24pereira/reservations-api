package com.pereira.api.reporte.controller;

import java.time.OffsetDateTime;
import java.util.List;

import com.pereira.api.reporte.dto.OcupacionEspacioResponse;
import com.pereira.api.reporte.service.ReporteService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@RestController
@RequestMapping("/reportes")
@AllArgsConstructor
public class ReporteController {

    private final ReporteService service;

    @GetMapping("/ocupacion")
    public List<OcupacionEspacioResponse> ocupacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        return service.ocupacion(desde, hasta);
    }

}
