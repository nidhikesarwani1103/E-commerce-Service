package dev.nidhi.productservice.controllers;

import dev.nidhi.productservice.clients.PaymentClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PaymentTestController {

    private final PaymentClient paymentClient;

    @GetMapping("/test-payment-connection")
    public ResponseEntity<String> testPaymentConnection(){
        return ResponseEntity.ok()
                .body(paymentClient.testConnection());
    }
}
