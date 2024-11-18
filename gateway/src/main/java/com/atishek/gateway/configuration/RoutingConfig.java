package com.atishek.gateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfig {

    @Bean
    public RouteLocator createCustomRoutes(RouteLocatorBuilder builder) {

        return builder.routes().
                route(p -> p
                        .path("/api/atishek/accounts/**")
                        .filters(f -> f
                                .rewritePath("/api/atishek/accounts/(?<segment>.*)", "/${segment}"))
//                                .circuitBreaker(config -> config
//                                                .setName("gatewayAccountsCircuitBreaker")
//                                                .setFallbackUri("forward:/contactSupport")))
                        .uri("lb://ACCOUNTS")
                )
                .route(p -> p.
                        path("/api/atishek/loans/**")
                        .filters(f -> f
                                .rewritePath("/api/atishek/loans/?<segment>.*", "/${segment}")
                               )
                        .uri("lb://LOANS"))
                .route(p -> p.path("/api/atishek/cards/**")
                                .filters(f -> f.rewritePath("/api/atishek/cards/?<segment>.*", "/${segment}")
                                        .circuitBreaker(cb -> cb.setName("GatewayCardsCircuitBreaker")))
                                .uri("lb://CARDS")
                )
                .build();
    }

}
