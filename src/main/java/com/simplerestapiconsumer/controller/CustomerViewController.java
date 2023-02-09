package com.simplerestapiconsumer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplerestapiconsumer.entity.Address;
import com.simplerestapiconsumer.entity.Customer;
import com.simplerestapiconsumer.entity.SocialMedia;
import com.simplerestapiconsumer.util.EntityGenerator;
import com.simplerestapiconsumer.util.TokenParser;

@Controller
public class CustomerViewController {
	
	private EntityGenerator entityGenerator = new EntityGenerator();
	
	private final Logger log;
	private RestTemplate restTemplate;
	private TokenParser tokenProvider;
	
	public CustomerViewController(Logger log, RestTemplate restTemplate, TokenParser tokenProvider) {
		this.log=log;
		this.restTemplate=restTemplate;
		this.tokenProvider = tokenProvider;
	}
	
	@GetMapping("/home")
	public String retrieveAllCustomers(@CookieValue (name="token") String token, HttpServletRequest request, Model model) {		
		HttpEntity<String> entity = entityGenerator.entityGenerator(token, null);
		
		ResponseEntity<List<Customer>> wrapper = restTemplate.exchange("http://localhost:8080/customers", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Customer>>() {});
//		https://www.baeldung.com/spring-resttemplate-json-list
		
		for(Customer cus: wrapper.getBody()) {
			log.info(cus.toString());
		}
		log.info(tokenProvider.getAuthFromToken(token).toString());
		request.removeAttribute("cusId");
		model.addAttribute("customer", wrapper.getBody());
		model.addAttribute("username", tokenProvider.getUsernameFromToken(token));
		model.addAttribute("role", tokenProvider.getAuthFromToken(token));
		return "home";
		
	}
	
	@GetMapping("/show-customer")
	public String showCustomerDetailsByID(@CookieValue(name="token") String token, @RequestParam(name="id") int id, HttpServletRequest request, Model model) {
		HttpEntity<String> entity = entityGenerator.entityGenerator(token, null);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/id").queryParam("id", id);
		ResponseEntity<Customer> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Customer.class);
		
		request.setAttribute("cusId",id);
		model.addAttribute("customer", wrapper.getBody());
		return "show-customer";
	}
	
	@GetMapping("/update-customer-form")
	public String showCustomerUpdateForm(@CookieValue(name="token") String token, @RequestParam(name="id") int id, Model model) {
		HttpEntity<String> entity = entityGenerator.entityGenerator(token, null);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/id").queryParam("id", id);
		ResponseEntity<Customer> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Customer.class);
		
		model.addAttribute("customer", wrapper.getBody());
		return "customer-form";
	}
	
	@GetMapping("/update-customer-address-form")
	public String showCustomerAddressForm(@CookieValue(name="token") String token, @RequestParam(name="id") int id, @RequestParam(name="addressId") int addressId, Model model) {
		HttpEntity<String> entity = entityGenerator.entityGenerator(token, null);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/customer-address").queryParam("id", id).queryParam("addressId", addressId);
		ResponseEntity<Address> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Address.class);

		model.addAttribute("cusId", id);
		model.addAttribute("address", wrapper.getBody());
		return "address-form";
	}
	
	@GetMapping("/save-new-customer-form")
	public String showNewCustomerForm(Model model) {
		Customer customer = new Customer();
		SocialMedia sm = new SocialMedia();
		customer.setSocialMedia(sm);
		model.addAttribute("customer", customer);
		return "customer-form";
	}
	
	@GetMapping("/save-new-customer-address-form")
	public String showNewCustomerAddressForm(@RequestParam(name="cusId") int id, Model model) {
		Address address = new Address();
		address.setId(0);
		model.addAttribute("address", address);
		model.addAttribute("cusId", id);
		return "address-form";
	}
}
