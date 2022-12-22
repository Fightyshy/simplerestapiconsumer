package com.simplerestapiconsumer.controller;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplerestapiconsumer.entity.Cases;
import com.simplerestapiconsumer.entity.Employee;
import com.simplerestapiconsumer.util.TokenParser;

@Controller
public class EmployeeViewController {
	private final Logger log;
	private final RestTemplate restTemplate;
	private final TokenParser tokenParser;

	public EmployeeViewController(Logger log, RestTemplate restTemplate, TokenParser tokenParser) {
		this.log = log;
		this.restTemplate = restTemplate;
		this.tokenParser = tokenParser;
	}
	
//	@GetMapping("/account-details")
//	public String displayAccountDetails(@CookieValue(name="token") String token, Model model) {
//		String username = tokenParser.getUsernameFromToken(token);
//		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/username").queryParam("username", username);
//		ResponseEntity<Employee> emp = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entityGenerator(token, null), Employee.class);
//		model.addAttribute("employee", emp);
//		return "account-details";
//	}
//	
//	@GetMapping("/details-change")
//	public String changeEmployeeDetails(@CookieValue(name="token") String token, Model model) {
//		String username = tokenParser.getUsernameFromToken(token);
//		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/username").queryParam("username", username);
//		ResponseEntity<Employee> emp = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entityGenerator(token, null), Employee.class);
//		model.addAttribute("employee", emp);
//		return "details-change";
//	}
	
	@GetMapping({"/account-details", "/details-change"})
	public String retrieveEmployeeAccountDetails(@CookieValue(name="token") String token, Model model, HttpServletRequest req) {
		String username = tokenParser.getUsernameFromToken(token);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/employees/username").queryParam("username", username);
		ResponseEntity<Employee> emp = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entityGenerator(token, null), Employee.class);
		model.addAttribute("employee", emp.getBody());
		return req.getRequestURI().toString().equals("/account-details")?"account-details":"details-change";
	}
	
	//TODO resttemplate retrieval with custom DTO for display
	@GetMapping("/cases-history")
	public String retrieveCasesHistoryForUser(@CookieValue(name="token") String token, Model model) throws JsonMappingException, JsonProcessingException {
		//https://stackoverflow.com/questions/9381665/how-can-we-configure-the-internal-jackson-mapper-when-using-resttemplate
		//putting as bean seems to break everything but this specific case, attempt at later date
		ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build().findAndRegisterModules();
    	MappingJackson2HttpMessageConverter convert = new MappingJackson2HttpMessageConverter();
    	convert.setObjectMapper(mapper);
		restTemplate.getMessageConverters().add(0, convert);
		
		ResponseEntity<Cases[]> cases = restTemplate.exchange("http://localhost:8080/employees/users/cases", HttpMethod.GET, entityGenerator(token, null), Cases[].class);
		model.addAttribute("cases", cases.getBody());
		return "case-history";
	}
	
	private <T> HttpEntity<T> entityGenerator(String token, T input){
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Bearer"+token);
		
		if(input==null) {
			return new HttpEntity<T>(headers);		
		}else {
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			return new HttpEntity<T>(input, headers);
		}
	}
}
