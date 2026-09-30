package dev.nidhi.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    /*
        KeyResolver determines the identity of the client for rate limiting.
        In our implementation, it extracts the client's IP address, which
        becomes the key used by the Redis-backed RequestRateLimiter to maintain
        separate rate-limit state for each client.
     */
    @Bean
    public KeyResolver ipKeyResolver(){
        return exchange -> Mono.just(
                                                    exchange.getRequest()
                                                            .getRemoteAddress()
                                                            .getAddress()
                                                            .getHostAddress()
        );
    }
}
