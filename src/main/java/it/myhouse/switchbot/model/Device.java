package it.myhouse.switchbot.model;

public class Device {
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private boolean enableCloudService;
    private String hubDeviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isEnableCloudService() {
        return enableCloudService;
    }

    public void setEnableCloudService(boolean enableCloudService) {
        this.enableCloudService = enableCloudService;
    }

    public String getHubDeviceId() {
        return hubDeviceId;
    }

    public void setHubDeviceId(String hubDeviceId) {
        this.hubDeviceId = hubDeviceId;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", enableCloudService=" + enableCloudService +
                ", hubDeviceId='" + hubDeviceId + '\'' +
                '}';
    }
}
