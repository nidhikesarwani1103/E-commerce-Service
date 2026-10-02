package dev.nidhi.paymentservice.controllers;

import dev.nidhi.paymentservice.models.CreatePaymentRequest;
import dev.nidhi.paymentservice.models.Payment;
import dev.nidhi.paymentservice.repositories.PaymentRepository;
import dev.nidhi.paymentservice.services.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentService paymentService,
                             PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    @Value("${server.port}")
    private String port;

    @PostMapping(value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Payment> createPayment
            (@RequestBody @Valid CreatePaymentRequest paymentRequest) {
        Payment payment = paymentService.createPayment(paymentRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payment);
    }

    @GetMapping("/{id}/reconcile")
    public ResponseEntity<Void> reconcilePayment(@PathVariable Long id){
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        paymentService.reconcilePayment(payment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello from Payment Service running" +
                " on Port: "+ port);
    }
}
