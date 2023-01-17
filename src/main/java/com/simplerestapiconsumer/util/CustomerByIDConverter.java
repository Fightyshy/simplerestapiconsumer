package com.simplerestapiconsumer.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.client.RestTemplate;

import com.simplerestapiconsumer.entity.Customer;

public class CustomerByIDConverter implements Converter<String, Customer> {

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public Customer convert(String source) {
		Customer cus = restTemplate.getForObject("http://localhost:8080/customer/id?id="+source, Customer.class);
		return cus;
	}

}
