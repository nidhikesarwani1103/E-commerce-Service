package dev.nidhi.paymentservice.controllers;

import dev.nidhi.paymentservice.dtos.RazorPayWebhookEntity;
import dev.nidhi.paymentservice.dtos.RazorpayWebhookPayload;
import dev.nidhi.paymentservice.services.PaymentService;
import dev.nidhi.paymentservice.services.RazorpayWebhookSignatureVerifier;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/webhooks/razorpay")
@AllArgsConstructor
public class RazorpayWebhookController {

    private final RazorpayWebhookSignatureVerifier signatureVerifier;
    private final JsonMapper jsonMapper;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature)
            throws NoSuchAlgorithmException {

        System.out.println("========== RAZORPAY WEBHOOK HIT ==========");

        System.out.println("Signature: " + signature);
        System.out.println("Payload: " + payload);

        boolean valid = signatureVerifier.verify(payload, signature);

        System.out.println("Signature valid: " + valid);

        if (!valid) {
            System.out.println("========== INVALID SIGNATURE ==========");
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        try {

            RazorpayWebhookPayload webhook =
                    jsonMapper.readValue(payload, RazorpayWebhookPayload.class);

            RazorPayWebhookEntity entity =
                    webhook.payload().payment().entity();

            System.out.println("Event: " + webhook.event());
            System.out.println("Provider Order ID: " + entity.orderId());
            System.out.println("Provider Payment ID: " + entity.id());
            System.out.println("Amount: " + entity.amount());

            if ("payment.captured".equals(webhook.event())) {

                paymentService.handlePaymentCaptured(
                        entity.orderId(),
                        entity.id(),
                        entity.amount()
                );

            } else if ("payment.failed".equals(webhook.event())) {

                paymentService.handlePaymentFailed(
                        entity.orderId(),
                        entity.id(),
                        entity.amount()
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body("Webhook processing failed");
        }

        System.out.println("Razorpay webhook received!");

        return ResponseEntity.ok().build();
    }
}
