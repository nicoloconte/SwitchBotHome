package it.myhouse.switchbot.service.sheduler;

import it.myhouse.switchbot.model.DeviceStatus;
import it.myhouse.switchbot.repository.DeviceStatusRepository;
import it.myhouse.switchbot.service.SwitchBotApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DeviceStatusScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DeviceStatusScheduler.class);
    private final DeviceStatusRepository repository;
    private final SwitchBotApiService service;

    @Autowired
    public DeviceStatusScheduler(SwitchBotApiService switchBotApiService, DeviceStatusRepository repository) {
        this.service = switchBotApiService;
        this.repository = repository;
    }

    @Scheduled(fixedRate = 300000)
    public void fetchDevicesStatusPeriodically() {
        logger.info("Esecuzione schedulata: fetchDevicesStatusPeriodically");

        try {
            List<DeviceStatus> statuses = service.getDevicesStatus();
            logger.info("Dispositivi ricevuti: {}", statuses.size());

            statuses.forEach(repository::save);
        } catch (Exception e) {
            logger.error("Errore durante il fetch dei device status", e);
        }
    }

    @Scheduled(fixedRate = 600000)
    public void cleanOldData() {
        logger.info("Esecuzione schedulata: cleanOldData");

        try {
            long oneDayAgo = Instant.now().minusSeconds(24 * 3600).toEpochMilli();
            repository.deleteOlderThan(oneDayAgo);
        } catch (Exception e) {
            logger.error("Errore durante l'eliminazione dei dati vecchi", e);
        }
    }
}
