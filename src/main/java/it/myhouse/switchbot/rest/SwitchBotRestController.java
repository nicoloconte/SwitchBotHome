package it.myhouse.switchbot.rest;

import it.myhouse.switchbot.model.DeviceList;
import it.myhouse.switchbot.model.DeviceStatus;
import it.myhouse.switchbot.service.SwitchBotApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/switchbot")
public class SwitchBotRestController {

    @Autowired
    private SwitchBotApiService switchBotApiService;

    @RequestMapping("/devices")
    // This endpoint will handle requests to retrieve the device list
    public String getDeviceList() {
        DeviceList deviceList = null;
        try {
            deviceList = switchBotApiService.getDeviceList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return deviceList.toString();
    }

    @RequestMapping("/devices/status")
    public List<DeviceStatus> getDevicesStatus() {
        List<DeviceStatus> deviceStatusList = null;
        try {
            deviceStatusList = switchBotApiService.getDevicesStatus();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return deviceStatusList;
    }

}
