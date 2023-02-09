package com.simplerestapiconsumer.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.simplerestapiconsumer.entity.AddEmployeeDTO;
import com.simplerestapiconsumer.entity.Cases;
import com.simplerestapiconsumer.entity.Customer;
import com.simplerestapiconsumer.entity.Employee;
import com.simplerestapiconsumer.entity.Product;
import com.simplerestapiconsumer.util.EntityGenerator;
import com.simplerestapiconsumer.util.TokenParser;

@Controller
@Scope(value=BeanDefinition.SCOPE_PROTOTYPE)
public class CaseViewController {
	
	private EntityGenerator entityGenerator = new EntityGenerator();
	
	private RestTemplate restTemplate;
	private final Logger log;
	private TokenParser parser;
	
	public CaseViewController(RestTemplate restTemplate, Logger log, TokenParser parser) {
		this.restTemplate = restTemplate;
		this.log = log;
		this.parser = parser;
	}
	
	@GetMapping("/case-details")
	public String showCasesForCustomer(@CookieValue(name="token") String token, @RequestParam(name="cusID") int cusID, Model model) {
		List<Cases> getCases = restTemplate.exchange("http://localhost:8080/cases/customers?id="+cusID, HttpMethod.GET, entityGenerator.entityGenerator(token, null), new ParameterizedTypeReference<List<Cases>>() {}).getBody();
		model.addAttribute("cases", getCases);
		model.addAttribute("role", parser.getAuthFromToken(token));
		return "list-customer-cases";
	}
	
	@GetMapping("/new-case")
	public String showFormForNewCase(@CookieValue(name="token") String token, Model model, @RequestParam("id") int id) {
		Customer cus = restTemplate.exchange("http://localhost:8080/customers/id?id="+id, HttpMethod.GET, entityGenerator.entityGenerator(token, null), Customer.class).getBody();
		List<Product> prods = restTemplate.exchange("http://localhost:8080/products", HttpMethod.GET, entityGenerator.entityGenerator(token, null), new ParameterizedTypeReference<List<Product>>() {}).getBody();
		System.out.println(prods.toString());
		Cases cases = new Cases();
		cases.setCustomer(cus);
		model.addAttribute("case", cases);
		model.addAttribute("productList", prods);
		return "new-case-form";
	}
	
	//TODO Desc field so this has purpose
	@GetMapping("/update-case")
	public String showSelectedCase(@CookieValue(name="token") String token, @RequestParam(name="caseID") int caseID, Model model) {
		Cases gotCase = restTemplate.exchange("http://localhost:8080/cases/id?caseID="+caseID, HttpMethod.GET, entityGenerator.entityGenerator(token, null), Cases.class).getBody();
		model.addAttribute("case", gotCase);
		return "case-form";
	}
	
	@GetMapping("/add-employee-to-case")
	public String showFormToAddEmployeeToCase(@CookieValue(name="token") String token, @RequestParam(name="caseID") int caseID, Model model) {
		AddEmployeeDTO wrapper = new AddEmployeeDTO(caseID, null);
		List<Employee> employees = restTemplate.exchange("http://localhost:8080/employees", HttpMethod.GET, entityGenerator.entityGenerator(token, null), new ParameterizedTypeReference<List<Employee>>() {}).getBody();
		model.addAttribute("employeeList", employees);
		model.addAttribute("case", wrapper);
		return "add-employee-to-case";
	}
}
