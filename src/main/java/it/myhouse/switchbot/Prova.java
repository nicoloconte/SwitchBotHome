package it.myhouse.switchbot;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Prova {
    public static void main(String[] args) throws Exception {

        String token = "c85bf418c6bdc031ff84fe0512337b6124b9776a873f535755d402c1e8c9151182272f57602e4c98943cad18dc460ee5";
        String secret = "18af6e425a6f3636806cbc49dbb88e80";
        String nonce = UUID.randomUUID().toString();
        String time= "" + Instant.now().toEpochMilli();
        String data = token + time + nonce;

        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        String signature = new String(Base64.getEncoder().encode(mac.doFinal(data.getBytes("UTF-8"))));

        HttpRequest getDevices = HttpRequest.newBuilder()
                .uri(new URI("https://api.switch-bot.com/v1.1/devices"))
                .header("Authorization", token)
                .header("sign", signature)
                .header("nonce", nonce)
                .header("t", time)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newBuilder().build().send(getDevices, BodyHandlers.ofString());

        System.out.println(response.body());
    }
}