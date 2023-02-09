package com.simplerestapiconsumer.util;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//@Component
public class EntityGenerator {
	
//	@Autowired
//	private RestTemplate restTemplate;
	
	public <T> HttpEntity<T> entityGenerator(String token, T input){
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Bearer"+token);
		
		if(input==null) {
			return new HttpEntity<T>(headers);		
		}else {
//			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			return new HttpEntity<T>(input, headers);
		}
	}
}
