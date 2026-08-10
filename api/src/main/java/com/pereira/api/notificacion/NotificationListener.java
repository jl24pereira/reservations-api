package com.pereira.api.notificacion;

import com.pereira.api.reserva.event.ReservaConfirmadaEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 *
 * @author Jose Luis Pereira
 */
@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @Async("notificationExecutor")
    @TransactionalEventListener
    public void notificarConfirmacion(ReservaConfirmadaEvent event) {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        log.info("Correo enviado a {} | Reserva: {} confirmada para '{}' del {} al {}", event.emailUsuario(),
                event.reservaId(), event.nombreEspacio(), event.inicio(), event.fin());
    }

}
