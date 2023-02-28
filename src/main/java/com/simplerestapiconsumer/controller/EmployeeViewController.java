package com.simplerestapiconsumer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
		model.addAttribute("empCases", emp.getBody().getCasesActive()+emp.getBody().getCasesPending());
		return req.getRequestURI().toString().equals("/account-details")?"account-details":"details-change";
	}
	
	@GetMapping("/list-employees")
	public String retrieveEmployees(@CookieValue(name="token") String token, Model model) {
		ResponseEntity<List<Employee>> emps = restTemplate.exchange("http://localhost:8080/employees", HttpMethod.GET, entityGenerator.entityGenerator(token, null), new ParameterizedTypeReference<List<Employee>>() {});
		model.addAttribute("employees",emps.getBody());
		model.addAttribute("role", tokenParser.getAuthFromToken(token));
		return "employee-list";
	}
	
	@GetMapping("/update-employee-form")
	public String updateEmployeeForm(@CookieValue(name="token") String token, Model model, @RequestParam(name="id") int id) {
		Employee emp = restTemplate.exchange("http://localhost:8080/employees/id?id="+id, HttpMethod.GET, entityGenerator.entityGenerator(token, null), Employee.class).getBody();
		model.addAttribute("employee", emp);
		return "employee-form";
	}
	
	@GetMapping("/save-new-employee-form")
	public String addNewEmployeeForm(@CookieValue(name="token") String token, Model model) {
		Employee emp = new Employee();
		SocialMedia sm = new SocialMedia();
		emp.setSocialMedia(sm);
		model.addAttribute("employee", emp);
		return "employee-form";
	}
}
