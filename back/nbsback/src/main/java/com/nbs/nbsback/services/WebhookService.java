package com.nbs.nbsback.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import com.nbs.nbsback.clients.StravaWebhookClient;

@Service
public class WebhookService {

    @Autowired
    private StravaWebhookClient stravaWebhookClient;

    @Value("${strava.client.id}")
    private String clientId;

    @Value("${strava.client.secret}")
    private String clientSecret;

    @Value("${strava.callback.url}")
    private String callbackUrl;

    @Value("${strava.verify.token}")
    private String verifyToken;

    @EventListener(ApplicationReadyEvent.class)
    public void subscribeToWebhook() {
        try {
            Map<String, Object> response = stravaWebhookClient.subscribeToWebhook(
                clientId, clientSecret, callbackUrl, verifyToken
            );
            System.out.println("Webhook suscrito exitosamente: " + response);
        } catch (Exception e) {
            System.err.println("Error al intentar suscribir el webhook: " + e.getMessage());
        }
    }
}