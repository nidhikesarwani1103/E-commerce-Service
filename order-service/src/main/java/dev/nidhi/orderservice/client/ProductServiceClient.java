package dev.nidhi.orderservice.client;

import dev.nidhi.orderservice.dtos.ProductResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductServiceClient {

    private final RestClient restClient;

    public ProductServiceClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://product-service")
                .build();
    }

    public ProductResponse getProductById(Long productId, String token) {
        return restClient
                .get()
                .uri("/products/{id}", productId)
                .header("Authorization", token)
                .retrieve()
                .body(ProductResponse.class);
    }
}
