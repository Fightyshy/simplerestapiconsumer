package com.simplerestapiconsumer;

import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.simplerestapiconsumer.entity.Quote;

@SpringBootApplication
public class SimplerestapiconsumerApplication {
	
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(SimplerestapiconsumerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SimplerestapiconsumerApplication.class, args);
	}
	
//	@Bean
//	public RestTemplate restTemplate (RestTemplateBuilder builder) {
//		return builder.build();
//	}
//	
//	@Bean
//	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
//		return args -> {
//			Quote quote = restTemplate.getForObject(
//					"https://quoters.apps.pcfone.io/api/random", Quote.class);
//			log.info(quote.toString());
//		};
//	}

}
