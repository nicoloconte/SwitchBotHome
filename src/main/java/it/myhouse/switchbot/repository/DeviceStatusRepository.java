package it.myhouse.switchbot.repository;

import it.myhouse.switchbot.model.DeviceStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM DeviceStatus d WHERE d.timestamp < :cutoff")
    public int deleteOlderThan(long cutoff);

}