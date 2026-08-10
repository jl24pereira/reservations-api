package com.pereira.api.reserva.service;

import java.util.UUID;

import com.pereira.api.pago.dto.ValidacionPagoResponse;
import com.pereira.api.pago.service.PagoService;
import com.pereira.api.reserva.dto.ReservaResponse;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

/**
 *
 * @author Jose Luis Pereira
 */
@Service
@AllArgsConstructor
public class ConfirmacionReservaService {

    private final ConfirmacionTrxService trx;
    private final PagoService pagoService;

    public ReservaResponse confirm(UUID reservaId, String paymentMethod, UUID usuarioId, boolean isAdmin) {
        ConfirmedData data = trx.prepareConfirmacion(reservaId, usuarioId, isAdmin);
        ValidacionPagoResponse validacion = pagoService.validate(reservaId, paymentMethod, data.monto());

        return trx.applyPaymentResult(reservaId, paymentMethod, validacion);
    }

}
