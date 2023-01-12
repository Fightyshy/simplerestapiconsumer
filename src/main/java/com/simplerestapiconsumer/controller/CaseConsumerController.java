package com.simplerestapiconsumer.controller;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplerestapiconsumer.entity.Cases;
import com.simplerestapiconsumer.enums.CaseStatus;
import com.simplerestapiconsumer.util.TokenParser;

@RestController
public class CaseConsumerController {
	
	private RestTemplate restTemplate;
	private Logger log;
//	private TokenParser parser;
	
	public CaseConsumerController(RestTemplate restTemplate, Logger log, TokenParser parser) {
		this.restTemplate = restTemplate;
		this.log = log;
//		this.parser = parser;
	}

	@GetMapping({"/resolveCase", "/closeCase", "/pendingCase", "/activeCase"})
	public String updateCaseStatus(@CookieValue(name="token") String token, @RequestParam("id") int id, Model model, HttpServletRequest req) throws Exception {
		String status = "";
		switch(req.getRequestURI().toString()) {
			case "/resolveCase":
				status = CaseStatus.RESOLVED.toString();
				break;
			case "/closeCase":
				status = CaseStatus.CLOSED.toString();
				break;
			case "/pendingCase":
				status = CaseStatus.PENDING.toString();
				break;
			case "/activeCase":
				status = CaseStatus.ACTIVE.toString();
				break;
			default:
				status = "";
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/cases/status").queryParam("id", id).queryParam("status", status);
		Cases wrapper = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, entityGenerator(token, null), Cases.class).getBody();
		
		StringBuilder logOutput = new StringBuilder().append("Case ID ").append(wrapper.getCustomer().getId()).append(" has been updated to ").append(status).append(" status");
		log.info(logOutput.toString());
		return "redirect:/case-details?cusID="+wrapper.getCustomer().getId(); //TODO Fix redirect
	}
	
	@PostMapping("/setEndDate")
	public String setCaseEndDate(@CookieValue(name="token") String token, @ModelAttribute("case") Cases cases) {
		Cases wrapper = restTemplate.exchange("http://localhost:8080/cases", HttpMethod.PUT, entityGenerator(token, cases), Cases.class).getBody();
		return wrapper.getCasesStatus().toString().equals(HttpStatus.NOT_FOUND.toString())?"redirect:/error":"redirect:/case-detail?cusID"+wrapper.getCustomer().getId();
	}
	
	@GetMapping("/deleteCase")
	public String deleteCase(@CookieValue(name="token") String token, @RequestParam("id") int id, @RequestParam("cusID") int cusID, Model model) {
		ResponseEntity<Object> wrapper = restTemplate.exchange("http://localhost:8080/cases/id?id="+id, HttpMethod.DELETE, entityGenerator(token, null), Object.class);
		return "redirect:/case-details?cusID="+cusID;
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
