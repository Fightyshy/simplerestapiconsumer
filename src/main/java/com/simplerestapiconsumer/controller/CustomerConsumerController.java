package com.simplerestapiconsumer.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplerestapiconsumer.entity.Address;
import com.simplerestapiconsumer.entity.Customer;
import com.simplerestapiconsumer.util.EntityGenerator;


@Controller
public class CustomerConsumerController {
	
	private EntityGenerator entityGenerator = new EntityGenerator();
	
	private final Logger log;
	private RestTemplate restTemplate;

	public CustomerConsumerController(Logger log, RestTemplate restTemplate) {
		this.log = log;
		this.restTemplate = restTemplate;
	}

	@GetMapping("/getCustomerByID")
	public void retrieveCustomerByID(@CookieValue (name="token") String token, @RequestParam (name="id") int id) {
		HttpEntity<String> entity = entityGenerator.entityGenerator(token, null);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/id").queryParam("id", id);
		
		ResponseEntity<Customer> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Customer.class);
		log.info(wrapper.getBody().toString());
	}
	
	@GetMapping("/getCustomerByFirstname")
	public void retrieveCustomerFirstname(@CookieValue (name="token") String token, @RequestParam(name="firstname") String firstName) {
		HttpEntity<String> entity = entityGenerator.entityGenerator(token, null);
 
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/firstname").queryParam("firstname", firstName);
		
		ResponseEntity<List<Customer>> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<Customer>>() {});
		log.info(wrapper.getBody().toString());
	}
	@GetMapping("/getCustomerByLastname")
	public void retrieveCustomerLastname(@CookieValue (name="token") String token, @RequestParam(name="lastname") String lastName) {
		HttpEntity<String> entity = entityGenerator.entityGenerator(token, null);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customer/lastname").queryParam("lastname", lastName);
		ResponseEntity<List<Customer>> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, new ParameterizedTypeReference<List<Customer>>() {});
		log.info(wrapper.getBody().toString());
	}
	
	@PostMapping("/saveCustomer")
	public ModelAndView saveNewCustomerDetails(@CookieValue(name="token") String token, @Valid @ModelAttribute(name="customer") Customer customer, BindingResult result) {
		if(result.hasErrors()) {
			ModelAndView model = new ModelAndView("new-customer-form.html");
			model.addObject("customer", customer);
			return model;
		}
		HttpEntity<Customer> entity = entityGenerator.entityGenerator(token, customer);
		ResponseEntity<Customer> wrapper = restTemplate.exchange("http://localhost:8080/customers", HttpMethod.POST, entity, Customer.class);
		log.info("A new customer ("+wrapper.getBody().toString()+") was added to the database");
		ModelAndView model = new ModelAndView("redirect:/home");
		return model;
	}
	
	@PostMapping("/saveCustomerAddress")
	public ModelAndView saveNewCustomerAddress(@CookieValue(name="token") String token, @Valid @ModelAttribute(name="address") Address address, BindingResult result, @RequestParam(name="cusId") int id) {
		if(result.hasErrors()) {
			ModelAndView model = new ModelAndView("new-address-form.html");
			model.addObject("address", address);
			model.addObject("cusId", id);
			return model;
		}
		address.setId(0);
		HttpEntity<Address> entity = entityGenerator.entityGenerator(token, address);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/id/addresses").queryParam("cusId", id);
		ResponseEntity<Address> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, Address.class);
		log.info("A new address was added to customer ("+wrapper.getBody().toString());
		ModelAndView model = new ModelAndView("redirect:/show-customer?id="+id);
		return model;
	}
	
	@PostMapping("/updateCustomer")
	public ModelAndView updateCustomerDetails(@CookieValue(name="token") String token, @Valid @ModelAttribute(name="customer") Customer customer, BindingResult result) {
		if(result.hasErrors()) {
			ModelAndView model = new ModelAndView("new-address-form.html");
			model.addObject("customer", customer);
			return model;
		}
		HttpEntity<Customer> entity = entityGenerator.entityGenerator(token, customer);
		ResponseEntity<Customer> wrapper = restTemplate.exchange("http://localhost:8080/customers", HttpMethod.PUT, entity, Customer.class);
		
		log.info("A customer ("+wrapper.getBody().toString()+") was updated in the database");
		ModelAndView model = new ModelAndView("redirect:/show-customer?id="+wrapper.getBody().getId());
		model.setViewName("redirect:show-customer?id="+wrapper.getBody().getId());
		return model;
	}
	
	
	//need to cross customer id somehow
	@PostMapping("/updateCustomerAddress")
	public ModelAndView updateCustomerAddress(@CookieValue(name="token") String token, @Valid @ModelAttribute(name="address") Address address, BindingResult result, @RequestParam(name="cusId") int cusId) {
		if(result.hasErrors()) {
			ModelAndView model = new ModelAndView("new-address-form.html");
			model.addObject("address", address);
			model.addObject("cusId", cusId);
			return model;
		}
		HttpEntity<Address> entity  = entityGenerator.entityGenerator(token, address);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/customers/id/addresses").queryParam("cusId", cusId);
		ResponseEntity<Address> wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, entity, Address.class);
		ModelAndView model = new ModelAndView("redirect:/show-customer?id="+cusId);
		model.setViewName("redirect:show-customer?id="+cusId);
		return model;
	}
	
	@GetMapping("/deleteCustomer")
	public ModelAndView deleteCustomerDetails(@CookieValue(name="token") String token, @RequestParam int id) {
		HttpEntity<String> entity = entityGenerator.entityGenerator(token, null);
		ResponseEntity<Object> wrapper = restTemplate.exchange(UriComponentsBuilder.fromUriString("http://localhost:8080/customers/id").queryParam("id", id).toUriString(), HttpMethod.DELETE, entity, Object.class);
		log.info("A customer was deleted");
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:home");
		return model;
	}
	
	@GetMapping("/deleteCustomerAddress")
	public ModelAndView deleteCustomerAddress(@CookieValue(name="token") String token, @RequestParam(name="id") int id, @RequestParam(name="addressId") int addressId) {
		HttpEntity<String> entity = entityGenerator.entityGenerator(token, null);
		ResponseEntity<Object> wrapper = restTemplate.exchange(UriComponentsBuilder.fromUriString("http://localhost:8080/customers/id/addresses").queryParam("customerid", id).queryParam("addressid", addressId).toUriString(), HttpMethod.DELETE, entity, Object.class);
		log.info("A customer's address was deleted");
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:show-customer?id="+id);
		return model;
	}
}