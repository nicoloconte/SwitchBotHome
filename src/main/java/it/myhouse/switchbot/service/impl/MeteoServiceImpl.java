package it.myhouse.switchbot.service.impl;

import it.myhouse.switchbot.service.MeteoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class MeteoServiceImpl implements MeteoService {

    private static final Logger logger = LoggerFactory.getLogger(MeteoServiceImpl.class);

    @Value("${meteobridge.base-url}")
    private String baseUrl;

    @Value("${meteobridge.username}")
    private String username;

    @Value("${METEOBRIDGE_PWD:${meteobridge.password}}")
    private String password;

    public String getMeteoData() throws Exception {
        String path = "/64e27e310a4fbe55ccb1e9cefc9c0813/cgi-bin/template.cgi";
        String query = "?template=%5Bth0temp-act%5D%7C%5Bth0hum-act%5D&contenttype=text/plain;charset=iso-8859-1";
        String url = baseUrl + path + query;

        // Crea HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Codifica Basic Auth
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;

        // Crea HttpRequest con header Authorization
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authHeader)
                .GET()
                .build();

        // Esegue la chiamata e ottiene la risposta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.ISO_8859_1));

        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            logger.debug("Meteo data fetched successfully: {}", response.body());
            return response.body();
        } else {
            throw new RuntimeException("HTTP error status: " + status);
        }
    }
}
