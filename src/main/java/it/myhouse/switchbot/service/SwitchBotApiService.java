package it.myhouse.switchbot.service;

import it.myhouse.switchbot.model.DeviceList;
import it.myhouse.switchbot.model.DeviceStatus;

import java.util.List;

public interface SwitchBotApiService {

    DeviceList getDeviceList() throws Exception;

    List<DeviceStatus> getDevicesStatus() throws Exception;

    List<DeviceStatus> getLatestDeviceStatusesFromDb() throws Exception;
}
