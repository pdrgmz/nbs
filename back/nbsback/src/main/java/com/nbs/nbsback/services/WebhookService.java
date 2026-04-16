package com.nbs.nbsback.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nbs.nbsback.clients.StravaWebhookClient;

@Service
public class WebhookService {

    @Autowired
    private StravaWebhookClient stravaWebhookClient;

    @Autowired
    private StravaService stravaService;

    @Value("${strava.client.id}")
    private String clientId;

    @Value("${strava.client.secret}")
    private String clientSecret;

    @Value("${strava.callback.url}")
    private String callbackUrl;

    @Value("${strava.verify.token}")
    private String verifyToken;

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    @EventListener(ApplicationReadyEvent.class)
    public void subscribeToWebhook() {
        try {
            Map<String, Object> response = stravaWebhookClient.subscribeToWebhook(
                    clientId, clientSecret, callbackUrl, verifyToken);
            logger.info("Webhook suscrito exitosamente: " + response);
        } catch (Exception e) {
            logger.error("Error during webhook subscription: {}", e);
        }
    }

    public void handleWebHookEvent(Map<String, Object> event) {
        logger.info("###########################################################################################");
        logger.info("###########################################################################################");
        logger.info("###########################################################################################");

        String objectType = (String) event.get("object_type");
        String aspectType = (String) event.get("aspect_type");
        Long objectId = ((Number) event.get("object_id")).longValue();
        Long ownerId = ((Number) event.get("owner_id")).longValue();

        logger.info("Received webhook event - Object Type: {}, Aspect Type: {}, Object ID: {}, Owner ID: {}",
                objectType, aspectType, objectId, ownerId);

        if ("activity".equals(objectType)) {
            switch (aspectType) {
                case "create":
                    stravaService.syncActivity(objectId, ownerId);
                    logger.info("Nueva actividad creada con ID: " + objectId);
                    break;
                case "update":
                    logger.info("Intento de actualización de actividad con ID: " + objectId);
                    break;
                case "delete":
                    logger.info("Intento de eliminación de actividad con ID: " + objectId);
                    break;
            }
        } else {
            logger.warn("Potential issue with object type: {}", objectType);
        }

        logger.info("###########################################################################################");
        logger.info("###########################################################################################");
        logger.info("###########################################################################################");

    }
}