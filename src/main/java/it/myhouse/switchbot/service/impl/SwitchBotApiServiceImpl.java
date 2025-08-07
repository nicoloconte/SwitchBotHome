package it.myhouse.switchbot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import it.myhouse.switchbot.handler.JsonBodyHandler;
import it.myhouse.switchbot.model.DeviceList;
import it.myhouse.switchbot.model.DeviceStatus;
import it.myhouse.switchbot.model.api.ApiResponse;
import it.myhouse.switchbot.service.SwitchBotApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class SwitchBotApiServiceImpl implements SwitchBotApiService {

    private static final Logger logger = LoggerFactory.getLogger(SwitchBotApiServiceImpl.class);
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final String BASE_URL = "https://api.switch-bot.com/v1.1/devices";
    private static final String METER_PLUS = "MeterPlus";
    private static final String TOKEN = "c85bf418c6bdc031ff84fe0512337b6124b9776a873f535755d402c1e8c9151182272f57602e4c98943cad18dc460ee5";
    private static final String SECRET = "18af6e425a6f3636806cbc49dbb88e80";

    @Override
    public DeviceList getDeviceList() throws Exception {
        logger.debug("Fetching device list...");
        String signature = generateSignature();
        HttpResponse<ApiResponse<DeviceList>> response = HttpClient.newHttpClient().send(
                createRequest(signature, BASE_URL),
                new JsonBodyHandler<>(new TypeReference<>() {})
        );
        logger.debug("Received response: {}", response.body());
        return response.body().getBody();
    }

    @Override
    public List<DeviceStatus> getDevicesStatus() throws Exception {
        logger.debug("Fetching device statuses...");
        List<DeviceStatus> statuses = new ArrayList<>();
        DeviceList deviceList = getDeviceList();

        if (deviceList != null && deviceList.getDeviceList() != null) {
            for (var device : deviceList.getDeviceList()) {
                if (!METER_PLUS.equals(device.getDeviceType())) continue;
                logger.debug("Fetching status for device: {}", device.getDeviceId());
                String signature = generateSignature();
                HttpResponse<ApiResponse<DeviceStatus>> response = HttpClient.newHttpClient().send(
                        createRequest(signature, BASE_URL + "/" + device.getDeviceId() + "/status"),
                        new JsonBodyHandler<>(new TypeReference<>() {})
                );
                DeviceStatus deviceStatus = response.body().getBody();
                deviceStatus.setName(device.getDeviceName());
                logger.debug("Received status response for device {}: {}", device.getDeviceId(), deviceStatus);
                statuses.add(deviceStatus);
            }
        } else {
            logger.warn("No devices found.");
        }
        logger.debug("Total device statuses fetched: {}", statuses.size());
        return statuses;
    }


    private static String generateSignature() throws Exception {
        String data = TOKEN + Instant.now().toEpochMilli() + UUID.randomUUID() + Math.random();
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
        return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private static HttpRequest createRequest(String signature, String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", TOKEN)
                .header("sign", signature)
                .header("nonce", UUID.randomUUID().toString())
                .header("t", String.valueOf(Instant.now().toEpochMilli()))
                .GET()
                .build();
    }
}