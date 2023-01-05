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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplerestapiconsumer.entity.Address;
import com.simplerestapiconsumer.entity.Customer;
import com.simplerestapiconsumer.entity.SocialMedia;
import com.simplerestapiconsumer.util.TokenParser;

@Controller
public class CustomerViewController {
	
	private final Logger log;
	private RestTemplate restTemplate;
	private TokenParser tokenProvider;
	
	public CustomerViewController(Logger log, RestTemplate restTemplate, TokenParser tokenProvider) {
		this.log=log;
		this.restTemplate=restTemplate;
		this.tokenProvider = tokenProvider;
	}
	
	@GetMapping("/home")
	public ModelAndView retrieveAllCustomers(@CookieValue (name="token") String token, HttpServletRequest request) {		
		HttpEntity<String> entity = entityGenerator(token, null);
		
		ResponseEntity<List<Customer>> wrapper = restTemplate.exchange("http://localhost:8080/customers", HttpMethod.GET, entity, new ParameterizedTypeReference<List<Customer>>() {});
//		https://www.baeldung.com/spring-resttemplate-json-list
		
		for(Customer cus: wrapper.getBody()) {
			log.info(cus.toString());
		}
		log.info(tokenProvider.getAuthFromToken(token).toString());
		request.removeAttribute("cusId");
		//https://stackoverflow.com/questions/38700790/how-to-return-a-html-page-from-a-restful-controller-in-spring-boot
		ModelAndView model = new ModelAndView();
		model.setViewName("home.html");
		model.addObject("customer", wrapper.getBody());
		model.addObject("username", tokenProvider.getUsernameFromToken(token));
		model.addObject("role", tokenProvider.getAuthFromToken(token));
		return model;
		
	}
	
	@GetMapping("/show-customer")
	public ModelAndView showCustomerDetailsByID(@CookieValue(name="token") String token, @RequestParam(name="id") int id, HttpServletRequest request) {
		HttpEntity<String> entity = entityGenerator(token, null);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/id").queryParam("id", id);
		ResponseEntity<Customer> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Customer.class);
		
		request.setAttribute("cusId",id);
		ModelAndView model = new ModelAndView();
		model.setViewName("show-customer.html");
		model.addObject("customer", wrapper.getBody());
		return model;
	}
	
	@GetMapping("/update-customer-form")
	public ModelAndView showCustomerUpdateForm(@CookieValue(name="token") String token, @RequestParam(name="id") int id) {
		HttpEntity<String> entity = entityGenerator(token, null);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/id").queryParam("id", id);
		ResponseEntity<Customer> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Customer.class);
		
		ModelAndView model = new ModelAndView();
		model.setViewName("customer-form.html");
		model.addObject("customer", wrapper.getBody());
		return model;
	}
	
	@GetMapping("/update-customer-address-form")
	public ModelAndView showCustomerAddressForm(@CookieValue(name="token") String token, @RequestParam(name="id") int id, @RequestParam(name="addressId") int addressId) {
		HttpEntity<String> entity = entityGenerator(token, null);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/customer-address").queryParam("id", id).queryParam("addressId", addressId);
		ResponseEntity<Address> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Address.class);
		
		ModelAndView model = new ModelAndView();
		model.setViewName("address-form.html");
		model.addObject("cusId", id);
		model.addObject("address", wrapper.getBody());
		return model;
	}
	
	@GetMapping("/save-new-customer-form")
	public ModelAndView showNewCustomerForm() {
		Customer customer = new Customer();
		SocialMedia sm = new SocialMedia();
		customer.setSocialMedia(sm);
		ModelAndView model = new ModelAndView();
		model.setViewName("new-customer-form.html");
		model.addObject("customer", customer);
		return model;
	}
	
	@GetMapping("/save-new-customer-address-form")
	public ModelAndView showNewCustomerAddressForm(@RequestParam(name="cusId") int id) {
		Address address = new Address();
		ModelAndView model = new ModelAndView();
		model.setViewName("new-address-form.html");
		model.addObject("address", address);
		model.addObject("cusId", id);
		return model;
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
