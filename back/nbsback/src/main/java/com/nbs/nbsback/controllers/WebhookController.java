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

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private StravaService stravaService;

    private static final String VERIFY_TOKEN = "STRAVA_VERIFY_TOKEN_NBSB"; // Cambiar por tu token de verificación

    // Endpoint para validar el webhook
    @GetMapping
    public ResponseEntity<Map<String, String>> validateWebhook(@RequestParam Map<String, String> params) {
        System.out.println("Validating webhook with params: " + params);
        String mode = params.get("hub.mode");
        String token = params.get("hub.verify_token");
        String challenge = params.get("hub.challenge");

        if ("subscribe".equals(mode) && "STRAVA_VERIFY_TOKEN_NBSB".equals(token)) {
            System.out.println("Webhook validated successfully. Responding with challenge: " + challenge);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(Map.of("hub.challenge", challenge)); // Return JSON response
        }

        System.out.println("Webhook validation failed. Invalid token or mode.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Endpoint para recibir eventos del webhook
    @PostMapping
    public ResponseEntity<Void> handleWebhookEvent(@RequestBody Map<String, Object> event) {
        // Procesar el evento recibido
        System.out.println("Evento recibido: " + event);

        // Aquí puedes desencadenar acciones según el evento
        String objectType = (String) event.get("object_type");
        String aspectType = (String) event.get("aspect_type");
        Long objectId = ((Number) event.get("object_id")).longValue();

        // Ejemplo de procesamiento
        if ("activity".equals(objectType)) {
            switch (aspectType) {
                case "create":
                    // Crear actividad
                    stravaService.syncActivity(objectId);
                    System.out.println("Nueva actividad creada con ID: " + objectId);
                    break;
                case "update":
                    System.out.println("Actividad actualizada con ID: " + objectId);
                    break;
                case "delete":
                    System.out.println("Actividad eliminada con ID: " + objectId);
                    break;
            }
        }

        return ResponseEntity.ok().build();
    }
}