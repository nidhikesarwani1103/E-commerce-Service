package dev.nidhi.productservice.clients;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {
    private final RestClient restClient;

    public PaymentClient(@Qualifier("loadBalancedRestClientBuilder")
                         RestClient.Builder restClientBuilder) {
        restClient = restClientBuilder
                .baseUrl("http://PAYMENT-SERVICE")
                .build();
    }

    public String testConnection(){
        return restClient
                .get()
                .uri("/payments/hello")
                .retrieve()
                .body(String.class);
    }


}
