package it.myhouse.switchbot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "device_status")
@Data
public class DeviceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String version;
    private String deviceId;
    @Transient
    private String deviceType;
    private Double temperature;
    private Integer battery;
    private Integer humidity;
    @Transient
    private String hubDeviceId;

    private String name;
    private long timestamp = System.currentTimeMillis();

}
