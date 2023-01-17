package com.simplerestapiconsumer.controller;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

@Controller
public class ProductConsumerController {
	private RestTemplate restTemplate;
	private Logger log;
	
	public ProductConsumerController(RestTemplate restTemplate, Logger log) {
		this.restTemplate = restTemplate;
		this.log = log;
	}
	 //TODO PLACEHODLER
	//TODO CRUD FOR MANAGERS+
}
