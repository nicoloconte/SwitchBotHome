package it.myhouse.switchbot.service.scheduler;

import it.myhouse.switchbot.model.DeviceStatus;
import it.myhouse.switchbot.repository.DeviceStatusRepository;
import it.myhouse.switchbot.service.SwitchBotApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DeviceStatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DeviceStatusScheduler.class);

    private final DeviceStatusRepository repository;
    private final SwitchBotApiService service;

    @Value("${ENABLE_SCHEDULER:false}")  // di default false
    private boolean schedulerEnabled;

    public DeviceStatusScheduler(SwitchBotApiService service, DeviceStatusRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    /**
     * Recupera periodicamente lo stato dei dispositivi ogni 5 minuti.
     */
    @Scheduled(fixedRate = 300_000)
    public void fetchDevicesStatusPeriodically() {
        logger.info("Esecuzione schedulata: fetchDevicesStatusPeriodically");

        if (!schedulerEnabled) {
            logger.debug("Scheduler disabilitato!");
            return; // scheduler disabilitato, esco subito
        }

        try {
            List<DeviceStatus> statuses = service.getDevicesStatus();
            logger.info("Dispositivi ricevuti: {}", statuses.size());

            statuses.forEach(repository::save);
        } catch (Exception e) {
            logger.error("Errore durante il fetch dei device status", e);
        }
    }

    /**
     * Elimina i dati più vecchi di una settimana ogni 10 minuti.
     */
    @Scheduled(fixedRate = 600_000)
    public void cleanOldData() {
        logger.info("Esecuzione schedulata: cleanOldData");

        if (!schedulerEnabled) {
            logger.debug("Scheduler disabilitato!");
            return; // scheduler disabilitato, esco subito
        }

        try {
            Instant oneWeekAgo = Instant.now().minusSeconds(7L * 24 * 3600);

            int deletedRows = repository.deleteOlderThan(oneWeekAgo);
            logger.debug("Eliminati {} record piu' vecchi di una settimana", deletedRows);
        } catch (Exception e) {
            logger.error("Errore durante l'eliminazione dei dati vecchi", e);
        }
    }
}