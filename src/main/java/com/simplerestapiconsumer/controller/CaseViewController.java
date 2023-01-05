package com.simplerestapiconsumer.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.simplerestapiconsumer.entity.Cases;
import com.simplerestapiconsumer.util.TokenParser;

@Controller
@Scope(value=BeanDefinition.SCOPE_PROTOTYPE)
public class CaseViewController {
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
		List<Cases> getCases = restTemplate.exchange("http://localhost:8080/cases/customers?id="+cusID, HttpMethod.GET, entityGenerator(token, null), new ParameterizedTypeReference<List<Cases>>() {}).getBody();
		model.addAttribute("cases", getCases);
		model.addAttribute("role", parser.getAuthFromToken(token));
		return "list-customer-cases";
	}
	
	@GetMapping("/cases")
	public String showFormForNewCase(@CookieValue(name="token") String token, Model model) {
		Cases cases = new Cases();
		cases.setStartDate(LocalDateTime.now());
		cases.setCasesStatus("ACTIVE"); //temp
		model.addAttribute("case", cases);
		return "new-case-form";
	}
	
	//TODO Desc field so this has purpose
	@GetMapping("/update-case")
	public String showSelectedCase(@CookieValue(name="token") String token, @RequestParam(name="caseID") int caseID, Model model) {
		Cases gotCase = restTemplate.exchange("http://localhost:8080/cases/id?caseID="+caseID, HttpMethod.GET, entityGenerator(token, null), Cases.class).getBody();
		model.addAttribute("case", gotCase);
		return "case-form";
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
