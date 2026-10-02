package dev.nidhi.orderservice.client;

import dev.nidhi.orderservice.dtos.CreatePaymentRequest;
import dev.nidhi.orderservice.dtos.PaymentResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentServiceClient {

    private final RestClient restClient;

    public PaymentServiceClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder clientBuilder) {
        restClient = clientBuilder
                .baseUrl("http://payment-service")
                .build();
    }

    public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {
        return restClient
                .post()
                .uri("/payments")
                .body(createPaymentRequest)
                .retrieve()
                .body(PaymentResponse.class);
    }

    public String testConnection(){
        System.out.println("PaymentServiceClient.testConnection: Calling payment service...");
        return restClient
                .get()
                .uri("/payments/hello")
                .retrieve()
                .body(String.class);
    }
}
