package com.pereira.api.espacio.domain;

import java.math.BigDecimal;

import com.pereira.api.shared.domain.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "espacio")
@Getter
@Setter
@NoArgsConstructor
public class Espacio extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoEspacio tipo;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "ubicacion", nullable = false, length = 160)
    private String ubicacion;

    @Column(name = "tarifa_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaHora;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}
