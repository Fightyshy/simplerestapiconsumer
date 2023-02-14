package com.simplerestapiconsumer.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplerestapiconsumer.entity.AddEmployeeDTO;
import com.simplerestapiconsumer.entity.CaseUpdateDTO;
import com.simplerestapiconsumer.entity.Cases;
import com.simplerestapiconsumer.entity.Employee;
import com.simplerestapiconsumer.enums.CaseStatus;
import com.simplerestapiconsumer.util.EntityGenerator;
import com.simplerestapiconsumer.util.TokenParser;

//@RestController
@Controller
@Scope(value=BeanDefinition.SCOPE_PROTOTYPE)
public class CaseConsumerController {
	private EntityGenerator entityGenerator = new EntityGenerator();
	
	private RestTemplate restTemplate;
	private Logger log;
	private TokenParser parser;
	
	public CaseConsumerController(RestTemplate restTemplate, Logger log, TokenParser parser) {
		this.restTemplate = restTemplate;
		this.log = log;
		this.parser = parser;
	}

	@PostMapping("/setCaseStatus")
	public String updateCaseStatus(@CookieValue(name="token") String token, @ModelAttribute("caseUpdate") CaseUpdateDTO caseUpdate, Model model, HttpServletRequest req) throws Exception{
		System.out.println(caseUpdate.getCaseId());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/cases/status").queryParam("id", caseUpdate.getCaseId()).queryParam("status", caseUpdate.getStatus());
		Cases wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, entityGenerator.entityGenerator(token, null), Cases.class).getBody();
		StringBuilder logOutput = new StringBuilder().append("Case ID ").append(wrapper.getCustomer().getId()).append(" has been updated to ").append(wrapper.getCasesStatus()).append(" status");
		log.info(logOutput.toString());
		return "redirect:/case-details?cusID="+wrapper.getCustomer().getId();
	}
	
//	@Depreciated
//	@GetMapping({"/resolveCase", "/closeCase", "/pendingCase", "/activeCase"})
//	public String updateCaseStatus(@CookieValue(name="token") String token, @RequestParam("id") int id, Model model, HttpServletRequest req) throws Exception {
//		String status = "";
//		switch(req.getRequestURI().toString()) {
//			case "/resolveCase":
//				status = CaseStatus.RESOLVED.toString();
//				break;
//			case "/closeCase":
//				status = CaseStatus.CLOSED.toString();
//				break;
//			case "/pendingCase":
//				status = CaseStatus.PENDING.toString();
//				break;
//			case "/activeCase":
//				status = CaseStatus.ACTIVE.toString();
//				break;
//			default:
//				status = "";
//		}
//		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/cases/status").queryParam("id", id).queryParam("status", status);
//		Cases wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, entityGenerator.entityGenerator(token, null), Cases.class).getBody();
//		
//		StringBuilder logOutput = new StringBuilder().append("Case ID ").append(wrapper.getCustomer().getId()).append(" has been updated to ").append(status).append(" status");
//		log.info(logOutput.toString());
//		return "redirect:/case-details?cusID="+wrapper.getCustomer().getId(); //TODO Fix redirect
//	}
	
	@PostMapping("/setEndDate")
	public String setCaseEndDate(@CookieValue(name="token") String token, @ModelAttribute("case") Cases cases) {
		Cases wrapper = restTemplate.exchange("http://localhost:8080/cases", HttpMethod.PUT, entityGenerator.entityGenerator(token, cases), Cases.class).getBody();
		return wrapper.getCasesStatus().toString().equals(HttpStatus.NOT_FOUND.toString())?"redirect:/error":"redirect:/case-detail?cusID"+wrapper.getCustomer().getId();
	}
	
	@PostMapping("/saveNewCase")
	public String saveNewCase(@CookieValue(name="token") String token, @ModelAttribute("case") Cases cases) {
		System.out.println(cases.getProduct().getName());
		HashSet<Employee> newEmpSet = new HashSet<Employee>();
		newEmpSet.add(getCurrentEmployeeUser(token));
		cases.setEmployee(newEmpSet);
		cases.setCasesStatus(CaseStatus.ACTIVE.toString());
		cases.setStartDate(LocalDateTime.now());
		Cases wrapper = restTemplate.exchange("http://localhost:8080/cases", HttpMethod.POST, entityGenerator.entityGenerator(token, cases), Cases.class).getBody();
		return wrapper.getCasesStatus().toString().equals(HttpStatus.NOT_FOUND.toString())?"redirect:/error":"redirect:/home";
	}
	
	//TODO broken for adding beyond 2, contains equals not working
	@PostMapping("/addEmployeeToCase")
	public String addEmployeeToCase(@CookieValue(name="token") String token, @ModelAttribute("case") AddEmployeeDTO employeeToAdd, Model model) {
		Cases cases = restTemplate.exchange("http://localhost:8080/cases/id?id="+employeeToAdd.getCaseID(), HttpMethod.GET, entityGenerator.entityGenerator(token, null), Cases.class).getBody();
		
		for(Employee emp: cases.getEmployee()) {
			System.out.println(emp.equals(employeeToAdd.getEmployee()));
		}
		System.out.println(cases.getEmployee().contains(employeeToAdd.getEmployee()));
		
		if(cases.getEmployee().contains(employeeToAdd.getEmployee())) {
			List<Employee> employees = restTemplate.exchange("http://localhost:8080/employees", HttpMethod.GET, entityGenerator.entityGenerator(token, null), new ParameterizedTypeReference<List<Employee>>() {}).getBody();
			employeeToAdd.setEmployee(null);
			model.addAttribute("employeeError", "This employee has already been assigned to the case");
			model.addAttribute("case", employeeToAdd);
			model.addAttribute("employeeList", employees);
			return "add-employee-to-case";
		}else {
//			cases.getEmployee().add(employeeToAdd.getEmployee());
			cases = restTemplate.exchange("http://localhost:8080/cases/id/employees?empId="+employeeToAdd.getEmployee().getId(), HttpMethod.PUT, entityGenerator.entityGenerator(token, cases), Cases.class).getBody();
			return "redirect:/home";
		}
	}
	
	@GetMapping("/deleteCase")
	public String deleteCase(@CookieValue(name="token") String token, @RequestParam("id") int id, @RequestParam("cusID") int cusID, Model model) {
		ResponseEntity<Object> wrapper = restTemplate.exchange("http://localhost:8080/cases/id?id="+id, HttpMethod.DELETE, entityGenerator.entityGenerator(token, null), Object.class);
		return "redirect:/case-details?cusID="+cusID;
	}
	
	private Employee getCurrentEmployeeUser(String token) {
		String username = parser.getUsernameFromToken(token);
		Employee wrapper = restTemplate.exchange("http://localhost:8080/employees/username?username="+username, HttpMethod.GET, entityGenerator.entityGenerator(token, null), Employee.class).getBody();
		return wrapper;
	}
}
