package com.simplerestapiconsumer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplerestapiconsumer.util.CustomerByIDConverter;
import com.simplerestapiconsumer.util.EmployeeByIDConverter;
import com.simplerestapiconsumer.util.ProductByIDConverter;

@Configuration
@ComponentScan("com.simplerestapiconsumer")
public class SimpleRestApiConfig implements WebMvcConfigurer{
    @Bean
    RestTemplate restTemplate() {
    	RestTemplate restTemplate = new RestTemplate();
    	restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }
    
    @Bean
    ObjectMapper objectMapper() {
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.findAndRegisterModules();
    	return mapper;
    }
    
    //https://dzone.com/articles/logger-injection-with-springs-injectionpoint
    @Bean
    @Scope("prototype")
    Logger logger(InjectionPoint ip) {
    	return LoggerFactory.getLogger(ip.getMethodParameter().getContainingClass());
    }
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ProductByIDConverter());
        registry.addConverter(new CustomerByIDConverter());
        registry.addConverter(new EmployeeByIDConverter());
    }
}
