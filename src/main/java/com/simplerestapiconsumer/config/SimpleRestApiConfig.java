package com.simplerestapiconsumer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan("com.simplerestapiconsumer")
public class SimpleRestApiConfig {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    //https://dzone.com/articles/logger-injection-with-springs-injectionpoint
    @Bean
    @Scope("prototype")
    Logger logger(InjectionPoint ip) {
    	return LoggerFactory.getLogger(ip.getMethodParameter().getContainingClass());
    }
}
