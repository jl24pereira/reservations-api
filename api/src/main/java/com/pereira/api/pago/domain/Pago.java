package com.pereira.api.pago.domain;

import com.pereira.api.reserva.domain.Reserva;
import com.pereira.api.shared.domain.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
public class Pago extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPago estado;

    @Column(name = "autorizacion_id", length = 80)
    private String autorizacionId;

    @Column(name = "metodo", nullable = false, length = 40)
    private String metodo;
}
