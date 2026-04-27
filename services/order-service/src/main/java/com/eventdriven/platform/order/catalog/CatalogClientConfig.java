package com.eventdriven.platform.order.catalog;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(CatalogClientProperties.class)
public class CatalogClientConfig {

    @Bean
    RestClient catalogRestClient(RestClient.Builder builder, CatalogClientProperties properties) {
        return builder.baseUrl(properties.getBaseUrl()).build();
    }
}
