package com.pereira.api.espacio.dto;

import java.math.BigDecimal;

import com.pereira.api.espacio.domain.TipoEspacio;
import com.sun.istack.NotNull;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @author Jose Luis Pereira
 */
public record CreateEspacioRequest(
        @NotBlank @Size(max = 120) String nombre,
        @NotNull TipoEspacio tipo,
        @NotNull @Min(1) @Max(1000) Integer capacidad,
        @NotBlank @Size(max = 160) String ubicacion,
        @NotNull @DecimalMin("0.0") @Digits(integer = 8, fraction = 2) BigDecimal tarifaHora) {

}
