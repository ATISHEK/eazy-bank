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
                        .filters(f -> f.rewritePath("/api/accounts/(?<segment>.*)", "/${segment}"))
                        .uri("lb://ACCOUNTS")
                )
                .route(p -> p.
                        path("/api/atishek/loans/**")
                        .filters(f -> f.rewritePath("/api/loanss/?<segment>.*", "/${segment}"))
                        .uri("lb://LOANS"))
                .build();
    }

}
