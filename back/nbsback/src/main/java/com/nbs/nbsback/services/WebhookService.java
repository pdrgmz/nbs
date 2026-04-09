package com.nbs.nbsback.services;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class WebhookService {

    @Value("${strava.client.id}")
    private String clientId;

    @Value("${strava.client.secret}")
    private String clientSecret;

    @Value("${strava.callback.url}")
    private String callbackUrl;

    @Value("${strava.verify.token}")
    private String verifyToken;

    @PostConstruct
    public void subscribeToWebhook() {
        try {
            URL url = new URL("https://www.strava.com/api/v3/push_subscriptions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String requestBody = String.format(
                "client_id=%s&client_secret=%s&callback_url=%s&verify_token=%s",
                clientId, clientSecret, callbackUrl, verifyToken
            );

            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                System.out.println("Webhook suscrito exitosamente.");
            } else {
                System.err.println("Error al suscribir el webhook. Código de respuesta: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error al intentar suscribir el webhook: " + e.getMessage());
        }
    }
}