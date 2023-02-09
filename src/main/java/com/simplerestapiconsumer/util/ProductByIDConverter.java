package com.simplerestapiconsumer.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.simplerestapiconsumer.entity.Product;

public class ProductByIDConverter implements Converter<String, Product> {

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public Product convert(String source) {
		Product prod = restTemplate.getForObject("http://localhost:8080/products/id?id="+source, Product.class);
		return prod;
	}

}
