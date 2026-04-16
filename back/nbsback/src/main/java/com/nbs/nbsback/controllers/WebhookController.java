package com.nbs.nbsback.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbs.nbsback.services.StravaService;
import com.nbs.nbsback.services.WebhookService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private WebhookService webhookService;

    private static final String VERIFY_TOKEN = "STRAVA_VERIFY_TOKEN_NBSB"; // Cambiar por tu token de verificación
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    // Endpoint para validar el webhook
    @GetMapping
    public ResponseEntity<Map<String, String>> validateWebhook(@RequestParam Map<String, String> params) {
        String mode = params.get("hub.mode");
        String token = params.get("hub.verify_token");
        String challenge = params.get("hub.challenge");

        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(Map.of("hub.challenge", challenge)); // Return JSON response
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Endpoint para recibir eventos del webhook
    @PostMapping
    public ResponseEntity<Void> handleWebhookEvent(@RequestBody Map<String, Object> event) {
        webhookService.handleWebHookEvent(event);
        return ResponseEntity.ok().build();
    }

    
}