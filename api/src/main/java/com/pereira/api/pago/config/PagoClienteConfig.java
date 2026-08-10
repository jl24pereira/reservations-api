package com.pereira.api.pago.config;

import com.pereira.api.shared.config.PagoProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 *
 * @author Jose Luis Pereira
 */
@Configuration
public class PagoClienteConfig {

    @Bean
    RestClient pagoRestClient(PagoProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder().baseUrl(properties.baseUrl()).requestFactory(requestFactory).build();
    }

}
