package com.simplerestapiconsumer.controller;

import java.util.Collections;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplerestapiconsumer.entity.Employee;

@RestController
public class EmployeeConsumerController {
	private Logger log;
	private RestTemplate restTemplate;
	
	public EmployeeConsumerController(Logger log, RestTemplate restTemplate) {
		this.log = log;
		this.restTemplate = restTemplate;
	}
	
	@PostMapping("/saveNewEmployee")
	public String saveNewEmployee(@CookieValue(name="token") String token, @ModelAttribute("employee") @Valid Employee employee, BindingResult br, Model model){
		if(br.hasErrors()) {
			model.addAttribute("employee", employee);
			return "new-employee-form";
		}
		
		HttpEntity<Employee> entity = entityGenerator(token, employee);
		ResponseEntity<Employee> wrapper = restTemplate.exchange("http://localhost:8080/employees", HttpMethod.POST, entity, Employee.class);
		log.info("Employee with the name"+ wrapper.getBody().getFirstName()+" "+wrapper.getBody().getLastName()+" was added to the database, please assign a user account to them");
		log.info("Creating user based on preset parameters");

		ResponseEntity<Object> newUser = GenerateUserFromDetails(token, employee);
		log.info("User for employeeID" + wrapper.getBody().getId()+ " with username "+newUser.getBody());
		
		return "redirect:/home";
		//TODO User account generation with
		//Password = random alphanumeric, notify user to change after first login or set function to do first login password change that doubles as activator sequence
	}

	@PostMapping("/updateEmployee")
	public String updateEmployee(@CookieValue(name="token") String token, @ModelAttribute("employee") @Valid Employee employee, BindingResult br, Model model) {
		if(br.hasErrors()) {
			model.addAttribute("employee", employee);
			return "new-employee-form";
		}
		
		ResponseEntity<Employee> wrapper = restTemplate.exchange("http://localhost:8080/employees", HttpMethod.PUT, entityGenerator(token, employee), Employee.class);
		model.addAttribute("employee", wrapper.getBody());
		return "account-details";
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
	
	private ResponseEntity<Object> GenerateUserFromDetails(String token, Employee employee) {
		StringBuilder username = new StringBuilder();
		username.append(employee.getFirstName().substring(0, 3));
		username.append(employee.getLastName().substring(0,3));
		username.append(employee.getDateOfBirth().getYear());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/users")
				.queryParam("username", username.toString())
				.queryParam("password", "ABC123") //temp
				.queryParam("empID", employee.getId());
		
		return restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entityGenerator(token, null), Object.class);
	}
}
