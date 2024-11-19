package com.atishek.gateway.configuration;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
public class RoutingConfig {

    @Bean
    public RouteLocator createCustomRoutes(RouteLocatorBuilder builder) {

        return builder.routes().
                route(p -> p
                        .path("/api/atishek/accounts/**")
                        .filters(f -> f
                                .rewritePath("/api/atishek/accounts/(?<segment>.*)", "/${segment}"))
/*   uncomment if circuit breaker config is removed from accounts service .
                             .circuitBreaker(config -> config
                                                .setName("gatewayAccountsCircuitBreaker")
                                                .setFallbackUri("forward:/contactSupport")))
  */                      .uri("lb://ACCOUNTS")
                )
                .route(p -> p.
                        path("/api/atishek/loans/**")
                        .filters(f -> f
                                .rewritePath("/api/atishek/loans/?<segment>.*", "/${segment}")
                                .retry(config -> config
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setStatuses(HttpStatus.valueOf(500), HttpStatus.valueOf(503))
                                        .setBackoff(Duration.ofMillis(500), Duration.ofSeconds(2), 2, true))
                               )
                        .uri("lb://LOANS"))
                .route(p -> p.path("/api/atishek/cards/**")
                                .filters(f -> f.rewritePath("/api/atishek/cards/?<segment>.*", "/${segment}")
                                        .requestRateLimiter(config -> config
                                                .setRateLimiter(redisRateLimiter())
                                                .setKeyResolver(getKeyResolver())))
                                .uri("lb://CARDS")
                )
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter(){
        return new RedisRateLimiter(1,1, 1);
    }

    @Bean
    public KeyResolver getKeyResolver(){
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
                .defaultIfEmpty("anonymous");
    }

}
