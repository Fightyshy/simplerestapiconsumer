package com.simplerestapiconsumer.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
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
import com.simplerestapiconsumer.entity.SocialMedia;
import com.simplerestapiconsumer.util.EntityGenerator;
import com.simplerestapiconsumer.util.TokenParser;

@Controller
public class EmployeeViewController {
	private EntityGenerator entityGenerator = new EntityGenerator();
	
	private final Logger log;
	private final RestTemplate restTemplate;
	private final TokenParser tokenParser;

	public EmployeeViewController(Logger log, RestTemplate restTemplate, TokenParser tokenParser) {
		this.log = log;
		this.restTemplate = restTemplate;
		this.tokenParser = tokenParser;
	}
	
	@GetMapping({"/account-details", "/details-change"})
	public String retrieveEmployeeAccountDetails(@CookieValue(name="token") String token, Model model, HttpServletRequest req) {
		String username = tokenParser.getUsernameFromToken(token);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/employees/username").queryParam("username", username);
		ResponseEntity<Employee> emp = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entityGenerator.entityGenerator(token, null), Employee.class);
		model.addAttribute("employee", emp.getBody());
		return req.getRequestURI().toString().equals("/account-details")?"account-details":"details-change";
	}
	
	@GetMapping("/list-employees")
	public String retrieveEmployees(@CookieValue(name="token") String token, Model model) {
		ResponseEntity<List<Employee>> emps = restTemplate.exchange("http://localhost:8080/employees", HttpMethod.GET, entityGenerator.entityGenerator(token, null), new ParameterizedTypeReference<List<Employee>>() {});
		model.addAttribute("employees",emps.getBody());
		return "employee-list";
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
		
		ResponseEntity<Cases[]> cases = restTemplate.exchange("http://localhost:8080/employees/users/cases", HttpMethod.GET, entityGenerator.entityGenerator(token, null), Cases[].class);
		model.addAttribute("cases", cases.getBody());
		return "case-history";
	}
	
	@GetMapping("/save-new-employee-form")
	public String addNewEmployeeForm(@CookieValue(name="token") String token, Model model) {
		Employee emp = new Employee();
		SocialMedia sm = new SocialMedia();
		emp.setSocialMedia(sm);
		model.addAttribute("employee", emp);
		return "new-employee-form";
	}
}
