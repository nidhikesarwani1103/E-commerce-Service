package dev.nidhi.apigateway.filters;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.UUID;

@Configuration
public class CorrelationIdFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Bean
    public GlobalFilter getCorrelationIdFilter(){
        return (exchange, chain) -> {

            String correlationId = exchange.getRequest()
                                           .getHeaders()
                                           .getFirst(CORRELATION_ID_HEADER);

            if(correlationId==null || correlationId.isBlank()){
                correlationId = UUID.randomUUID().toString();
            }

            ServerHttpRequest request = exchange.getRequest()
                                                .mutate()
                                                .header(CORRELATION_ID_HEADER, correlationId)
                                                .build();

            System.out.println("Correlation ID: " + correlationId);
            return chain.filter(
                                 exchange.mutate()
                                         .request(request)
                                         .build()
            );
        };
    }
}
