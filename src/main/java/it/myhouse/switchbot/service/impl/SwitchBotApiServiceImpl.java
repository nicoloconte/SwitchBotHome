package it.myhouse.switchbot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import it.myhouse.switchbot.handler.JsonBodyHandler;
import it.myhouse.switchbot.model.DeviceList;
import it.myhouse.switchbot.model.DeviceStatus;
import it.myhouse.switchbot.model.api.ApiResponse;
import it.myhouse.switchbot.repository.DeviceStatusRepository;
import it.myhouse.switchbot.service.SwitchBotApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${SWITCHBOT_TOKEN:${switchbot.token}}")
    private String token;

    @Value("${SWITCHBOT_SECRET:${switchbot.secret}}")
    private String secret;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final DeviceStatusRepository repository;

    public SwitchBotApiServiceImpl(DeviceStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeviceList getDeviceList() throws Exception {
        logger.debug("Fetching device list...");
        HttpResponse<ApiResponse<DeviceList>> response = sendGetRequest(BASE_URL, new TypeReference<ApiResponse<DeviceList>>() {});
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch device list, status code: " + response.statusCode());
        }
        DeviceList deviceList = response.body().getBody();
        logger.debug("Device list received: {}", deviceList);
        return deviceList;
    }

    @Override
    public List<DeviceStatus> getDevicesStatus() throws Exception {
        logger.debug("Fetching device statuses...");
        List<DeviceStatus> statuses = new ArrayList<>();
        DeviceList deviceList = getDeviceList();

        if (deviceList == null || deviceList.getDeviceList() == null) {
            logger.warn("No devices found.");
            return statuses;
        }

        for (var device : deviceList.getDeviceList()) {
            if (!METER_PLUS.equals(device.getDeviceType())) {
                continue;
            }

            logger.debug("Fetching status for device: {}", device.getDeviceId());
            String statusUrl = BASE_URL + "/" + device.getDeviceId() + "/status";

            HttpResponse<ApiResponse<DeviceStatus>> response = sendGetRequest(statusUrl, new TypeReference<ApiResponse<DeviceStatus>>() {});

            if (response.statusCode() != 200) {
                logger.warn("Failed to fetch status for device {} with status code {}", device.getDeviceId(), response.statusCode());
                continue;
            }

            DeviceStatus deviceStatus = response.body().getBody();
            deviceStatus.setName(device.getDeviceName());

            logger.debug("Status for device {}: {}", device.getDeviceId(), deviceStatus);
            statuses.add(deviceStatus);
        }

        logger.debug("Total device statuses fetched: {}", statuses.size());
        return statuses;
    }

    /**
     * Recupera l’ultimo stato registrato per ciascun device.
     */
    public List<DeviceStatus> getLatestDeviceStatusesFromDb() {
        logger.debug("Fetching latest status for each device from DB...");
        List<DeviceStatus> latestStatuses = repository.findLatestForEachDevice();
        logger.debug("Retrieved {} latest device statuses from DB", latestStatuses.size());
        return latestStatuses;
    }

    /**
     * Metodo generico per inviare una richiesta GET firmata.
     */
    private <T> HttpResponse<T> sendGetRequest(String url, TypeReference<T> typeReference) throws Exception {
        String nonce = UUID.randomUUID().toString();
        String t = String.valueOf(Instant.now().toEpochMilli());
        String signature = generateSignature(token, t, nonce);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", token)
                .header("sign", signature)
                .header("nonce", nonce)
                .header("t", t)
                .GET()
                .build();

        logger.debug("Sending GET request to URL: {}, nonce: {}, t: {}, signature: {}", url, nonce, t, signature);
        return httpClient.send(request, new JsonBodyHandler<>(typeReference));
    }

    /**
     * Genera la firma HMAC-SHA256 usando token, t e nonce.
     */
    private String generateSignature(String token, String t, String nonce) throws Exception {
        String data = token + t + nonce;
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

}