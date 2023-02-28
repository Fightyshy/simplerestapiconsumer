package com.simplerestapiconsumer.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplerestapiconsumer.entity.Employee;
import com.simplerestapiconsumer.util.EntityGenerator;

@Controller
public class EmployeeConsumerController {
	private EntityGenerator entityGenerator = new EntityGenerator();
	
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
		
		HttpEntity<Employee> entity = entityGenerator.entityGenerator(token, employee);
		ResponseEntity<Employee> wrapper = restTemplate.exchange("http://localhost:8080/employees", HttpMethod.POST, entity, Employee.class);
		log.info("Employee with the name"+ wrapper.getBody().getFirstName()+" "+wrapper.getBody().getLastName()+" was added to the database, please assign a user account to them");
		log.info("Creating user based on preset parameters");

		ResponseEntity<Object> newUser = GenerateUserFromDetails(token, employee);
		log.info("User for employeeID" + wrapper.getBody().getId()+ " with username "+newUser.getBody());
		
		return "redirect:/home";
	}

	@PostMapping("/updateEmployee")
	public String updateEmployee(@CookieValue(name="token") String token, @ModelAttribute("employee") @Valid Employee employee, BindingResult br, Model model) {
		if(br.hasErrors()) {
			model.addAttribute("employee", employee);
			return "new-employee-form";
		}
		
		ResponseEntity<Employee> wrapper = restTemplate.exchange("http://localhost:8080/employees", HttpMethod.PUT, entityGenerator.entityGenerator(token, employee), Employee.class);
		model.addAttribute("employee", wrapper.getBody());
		return "account-details";
	}
	
	@GetMapping("/deleteEmployee")
	public String deleteEmployee(@CookieValue(name="token") String token, @RequestParam("id") Integer id, Model model) {
		ResponseEntity<Object> deleter = restTemplate.exchange("http://localhost:8080/employees/id?id="+id, HttpMethod.DELETE, entityGenerator.entityGenerator(token, null), Object.class);
		log.warn("Employee ID "+ id + " has been deleted from the system.");
		return "redirect:/list-employees";
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
		
		return restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entityGenerator.entityGenerator(token, null), Object.class);
	}
}
