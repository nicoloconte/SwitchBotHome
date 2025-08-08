package it.myhouse.switchbot.repository;

import it.myhouse.switchbot.model.DeviceStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM DeviceStatus d WHERE d.createdAt < :cutoff")
    int deleteOlderThan(Instant cutoff);

    @Query(value = """
                SELECT DISTINCT ON (device_id) *
                FROM device_status
                ORDER BY device_id, created_at DESC
            """, nativeQuery = true)
    List<DeviceStatus> findLatestForEachDevice();

}