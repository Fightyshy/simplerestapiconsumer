package com.simplerestapiconsumer.controller;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.simplerestapiconsumer.entity.Employee;

@RestController
public class EmployeeConsumerController {
	private Logger log;
	private RestTemplate restTemplate;
	
	public EmployeeConsumerController(Logger log, RestTemplate restTemplate) {
		this.log = log;
		this.restTemplate = restTemplate;
	}
	
//	@PostMapping("/updateDetails")
//	public ResponseEntity<Object> updateUserDetails(@RequestBody Employee employee){
//		
//	}
}
