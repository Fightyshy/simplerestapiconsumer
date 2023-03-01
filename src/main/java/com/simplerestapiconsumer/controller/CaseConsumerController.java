package com.simplerestapiconsumer.controller;

import java.time.LocalDateTime;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplerestapiconsumer.entity.AddEmployeeDTO;
import com.simplerestapiconsumer.entity.CaseUpdateDTO;
import com.simplerestapiconsumer.entity.Cases;
import com.simplerestapiconsumer.entity.Employee;
import com.simplerestapiconsumer.entity.IDWrapper;
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

	@PostMapping({"/setCaseStatusCus", "/setCaseStatusEmp", "/setCaseStatusAll"})
	public String updateCaseStatus(@CookieValue(name="token") String token, @ModelAttribute("caseUpdate") CaseUpdateDTO caseUpdate, Model model, HttpServletRequest req) throws Exception{
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/cases/status").queryParam("id", caseUpdate.getCaseId()).queryParam("status", caseUpdate.getStatus());
		Cases wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, entityGenerator.entityGenerator(token, null), Cases.class).getBody();
		StringBuilder logOutput = new StringBuilder().append("Case ID ").append(wrapper.getCustomer().getId()).append(" has been updated to ").append(wrapper.getCasesStatus()).append(" status");
		log.info(logOutput.toString());
		
		StringBuilder destination = new StringBuilder().append("redirect:/");

		switch(req.getRequestURI().toString()) {
		case "/setCaseStatusCus":
			destination.append("case-details?cusID="+wrapper.getCustomer().getId());
			break;
		case "/setCaseStatusEmp":
			destination.append("cases-history");
			break;
		case "/setCaseStatusAll":
			destination.append("all-cases");
			break;
		default:
			destination.append("error");
			break;
		}
		
		return destination.toString();
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
//  @Depreciated //(See below endpoint)
//	@PostMapping("/addEmployeeToCase")
//	public String addEmployeeToCase(@CookieValue(name="token") String token, @ModelAttribute("case") AddEmployeeDTO employeeToAdd, Model model) {
//		Cases cases = restTemplate.exchange("http://localhost:8080/cases/id?id="+employeeToAdd.getCaseID(), HttpMethod.GET, entityGenerator.entityGenerator(token, null), Cases.class).getBody();
//		
//		for(Employee emp: cases.getEmployee()) {
//			System.out.println(emp.equals(employeeToAdd.getEmployee()));
//		}
//		System.out.println(cases.getEmployee().contains(employeeToAdd.getEmployee()));
//		
//		if(cases.getEmployee().contains(employeeToAdd.getEmployee())) {
//			List<Employee> employees = restTemplate.exchange("http://localhost:8080/employees", HttpMethod.GET, entityGenerator.entityGenerator(token, null), new ParameterizedTypeReference<List<Employee>>() {}).getBody();
//			employeeToAdd.setEmployee(null);
//			model.addAttribute("employeeError", "This employee has already been assigned to the case");
//			model.addAttribute("case", employeeToAdd);
//			model.addAttribute("employeeList", employees);
//			return "add-employee-to-case";
//		}else {
////			cases.getEmployee().add(employeeToAdd.getEmployee());
//			cases = restTemplate.exchange("http://localhost:8080/cases/id/employees?empId="+employeeToAdd.getEmployee().getId(), HttpMethod.PUT, entityGenerator.entityGenerator(token, cases), Cases.class).getBody();
//			return "redirect:/home";
//		}
//	}
	
	//TODO employee limit check
	//TODO correct redirect
	@PostMapping("/addEmployeeToCaseByID")
	public String addEmployeeToCaseByID(@CookieValue(name="token") String token, @ModelAttribute("empToAdd") IDWrapper wrapper, RedirectAttributes redir, @RequestHeader(value = HttpHeaders.REFERER, required=false) final String ref) {
		Cases cases = restTemplate.exchange("http://localhost:8080/cases/id?id="+wrapper.getCaseId(), HttpMethod.GET, entityGenerator.entityGenerator(token, null), Cases.class).getBody();
		Employee emp = restTemplate.exchange("http://localhost:8080/employees/id?id="+wrapper.getID(), HttpMethod.GET, entityGenerator.entityGenerator(token, null), Employee.class).getBody();

		if(cases.getEmployee().contains(emp)) {
			redir.addFlashAttribute("errorMsg", "This employee is already assigned to the case");
			return "redirect:"+ref.substring(21);
		}
		else if(cases.getEmployee().size()>5) {
			redir.addFlashAttribute("errorMsg", "The case has more than 5 employees assigned to it.");
			return "redirect:"+ref.substring(21);
		}else {
			cases.getEmployee().add(emp);
			emp.getCases().add(cases);
			restTemplate.exchange("http://localhost:8080/cases/employees/new", HttpMethod.PUT, entityGenerator.entityGenerator(token, new AddEmployeeDTO(cases, emp)), Object.class);
			return "redirect:"+ref.substring(21); //temp
		}
	}
	
	@GetMapping("/deleteCase")
	public String deleteCase(@CookieValue(name="token") String token, @RequestParam("id") int id, @RequestParam("cusID") int cusID, Model model) {
		ResponseEntity<Object> wrapper = restTemplate.exchange("http://localhost:8080/cases/id?id="+id, HttpMethod.DELETE, entityGenerator.entityGenerator(token, null), Object.class);
		return "redirect:/case-details?cusID="+cusID;
	}
	
	@GetMapping("/removeEmployeeByID")
	public String removeEmployeeByID(@CookieValue(name="token") String token, @ModelAttribute("empToRemove") IDWrapper wrapper, RedirectAttributes redir, @RequestHeader(value = HttpHeaders.REFERER, required=false) final String ref) {
		UriComponentsBuilder build = UriComponentsBuilder.fromUriString("http://localhost:8080/cases/id/employees").queryParam("id", wrapper.getID()).queryParam("empId", wrapper.getCaseId());
		System.out.println(wrapper.getID());
		System.out.println(wrapper.getCaseId());
		ResponseEntity<Object> deleter = restTemplate.exchange(build.toUriString(), HttpMethod.DELETE, entityGenerator.entityGenerator(token, null), Object.class);
		
		return "redirect:"+ref.substring(21);
	}
	
	private Employee getCurrentEmployeeUser(String token) {
		String username = parser.getUsernameFromToken(token);
		Employee wrapper = restTemplate.exchange("http://localhost:8080/employees/username?username="+username, HttpMethod.GET, entityGenerator.entityGenerator(token, null), Employee.class).getBody();
		return wrapper;
	}
}
