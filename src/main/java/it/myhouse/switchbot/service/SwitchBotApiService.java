package it.myhouse.switchbot.service;

import it.myhouse.switchbot.model.DeviceList;
import it.myhouse.switchbot.model.DeviceStatus;

import java.util.List;

public interface SwitchBotApiService {

    public DeviceList getDeviceList() throws Exception;

    public List<DeviceStatus> getDevicesStatus() throws Exception;

}
