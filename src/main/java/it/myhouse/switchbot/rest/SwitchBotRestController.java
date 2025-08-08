package it.myhouse.switchbot.rest;

import it.myhouse.switchbot.model.DeviceList;
import it.myhouse.switchbot.model.DeviceStatus;
import it.myhouse.switchbot.service.SwitchBotApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/switchbot")
public class SwitchBotRestController {

    private static final Logger logger = LoggerFactory.getLogger(SwitchBotRestController.class);

    private final SwitchBotApiService switchBotApiService;

    public SwitchBotRestController(SwitchBotApiService switchBotApiService) {
        this.switchBotApiService = switchBotApiService;
    }

    @GetMapping("/devices")
    public DeviceList getDeviceList() {
        try {
            return switchBotApiService.getDeviceList();
        } catch (Exception e) {
            logger.error("Error retrieving device list", e);
            throw new IllegalStateException("Unable to retrieve device list", e);
        }
    }

    @GetMapping("/devices/status")
    public List<DeviceStatus> getDevicesStatus() {
        try {
            List<DeviceStatus> deviceStatusList = switchBotApiService.getLatestDeviceStatusesFromDb();

            if (deviceStatusList.isEmpty()) {
                logger.info("No device statuses found in the database, fetching from API...");
                deviceStatusList = switchBotApiService.getDevicesStatus();
            }

            return deviceStatusList;
        } catch (Exception e) {
            logger.error("Error retrieving device statuses", e);
            throw new IllegalStateException("Unable to retrieve device statuses", e);
        }
    }
}
