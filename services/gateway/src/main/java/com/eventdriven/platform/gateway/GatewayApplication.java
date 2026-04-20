package com.eventdriven.platform.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @RestController
    static class GatewayInfoController {

        @GetMapping("/internal/service-info")
        Map<String, Object> serviceInfo() {
            return Map.of(
                    "serviceName", "gateway",
                    "responsibility", "Entry point and request routing",
                    "routes", List.of("/auth/**", "/users/**", "/products/**", "/orders/**", "/inventory/**")
            );
        }
    }
}
