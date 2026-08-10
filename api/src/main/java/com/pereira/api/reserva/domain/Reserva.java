package com.pereira.api.reserva.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.pereira.api.espacio.domain.Espacio;
import com.pereira.api.reserva.state.ReservaStateFactory;
import com.pereira.api.shared.domain.BaseEntity;
import com.pereira.api.usuario.domain.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reserva")
@Getter
@Setter
@NoArgsConstructor
public class Reserva extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "inicio", nullable = false)
    private OffsetDateTime inicio;

    @Column(name = "fin", nullable = false)
    private OffsetDateTime fin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoReserva estado;

    @Column(name = "monto_total", precision = 10, scale = 2)
    private BigDecimal montoTotal;

    public void confirmar() {
        this.estado = ReservaStateFactory.of(this.estado).confirmar();
    }

    public void cancelar() {
        this.estado = ReservaStateFactory.of(this.estado).cancelar();
    }

    public void pagoPendiente() {
        this.estado = ReservaStateFactory.of(this.estado).pagoPendiente();
    }

    public void completar() {
        this.estado = ReservaStateFactory.of(this.estado).completar();
    }

    public static Reserva createNew(Espacio espacio, Usuario usuario, OffsetDateTime inicio, OffsetDateTime fin) {
        Reserva r = new Reserva();
        r.espacio = espacio;
        r.usuario = usuario;
        r.inicio = inicio;
        r.fin = fin;
        r.estado = EstadoReserva.PENDING;
        return r;
    }

}
