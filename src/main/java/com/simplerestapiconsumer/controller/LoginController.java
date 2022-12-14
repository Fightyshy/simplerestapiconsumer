package com.simplerestapiconsumer.controller;

import java.util.Collections;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplerestapiconsumer.entity.EmailWrapper;
import com.simplerestapiconsumer.entity.Login;
import com.simplerestapiconsumer.entity.ResetPW;

@Controller
public class LoginController {

	private RestTemplate restTemplate;
	private final Logger log;
	private JavaMailSender mailSender;
	
	public LoginController(RestTemplate restTemplate, Logger log, JavaMailSender mailSender) {
		this.restTemplate = restTemplate;
		this.log = log;
		this.mailSender = mailSender;
	}

	//issue token - with /password-recovery
	@PostMapping("/issue-pw-token")
	public String requestPWResetToken(@ModelAttribute("email") @Valid EmailWrapper email, BindingResult br){
		if(br.hasErrors()) {
			return "password-recovery";
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Location", "/home");
		
		HttpEntity<EmailWrapper> entity = new HttpEntity<>(email, headers);
		ResponseEntity<String> token = restTemplate.exchange("http://localhost:8080/users/issue-pw-token", HttpMethod.POST, entity, String.class);
		sendPWResetAuthLink(email.getEmail(), token.getBody());
		return "redirect:/loginpage";
	}
	
	//actual login
	@PostMapping("/retrieve-token")
	public String retrieveTokenFromServer(@ModelAttribute("login") Login login, BindingResult br, HttpServletResponse res) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Location", "/home");
		//https://stackoverflow.com/questions/10358345/making-authenticated-post-requests-with-spring-resttemplate-for-android
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter()); //converts requestbody to json for post

		HttpEntity<Login> entity = new HttpEntity<>(login, headers); //no need to convert, insert requestbody directly
		
		ResponseEntity<String> token = restTemplate.exchange("http://localhost:8080/users/authenticate", HttpMethod.POST, entity, String.class);
		
		if(token.getStatusCode().equals(HttpStatus.OK)) {
			Cookie cookie = new Cookie("token", (String) token.getBody().toString());
			res.addCookie(cookie);
			
			return "redirect:/home";
		}else {
			log.info("Failed to log in "+login.getUsername()+" due to invalid credentials");
			return "redirect:/loginpage";
		}
//		ResponseCookie tokenCookie = ResponseCookie.from("token", token.getBody().toString()).httpOnly(true).secure(false).build();
//		
//		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, tokenCookie.toString()).build();
	}
	
	//processes token with /password-reset
	@PostMapping("/resetpw")
	public String resetUserPassword(@ModelAttribute("pwreset") ResetPW resetPW) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Location", "/loginpage");
		
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		HttpEntity<ResetPW> entity = new HttpEntity<>(resetPW, headers);
		ResponseEntity<Object> reset = restTemplate.exchange("http://localhost:8080/users/resetpw", HttpMethod.POST, entity, Object.class);
		
		if(reset.getStatusCode().equals(HttpStatus.OK)) {
			return "redirect:/loginpage";
		}else {
			return null;
		}
	}
	
	@GetMapping("/logout")
	public String logoutUser(HttpServletRequest req) {
		return "redirect:http://localhost:8080/logout";
	}
	
	@GetMapping("/logindefault")
	public String altLoginPage(Model model) {
		model.addAttribute("login", new Login());
		return "loginpage";
	}
	
	@GetMapping("/loginpage")
	public String showMyLoginPage(@CookieValue(name="token", required=false) String token, Model model) {
		if(token!=null) {
			return "redirect:/home";
		}
		Login login = new Login();
		model.addAttribute("login", login);
		return "loginpage";
		
	}
	
	@GetMapping("/password-reset")
	public String showPasswordResetPage(Model model, @RequestParam("pwtoken") String token) {
		UriComponentsBuilder recovery = UriComponentsBuilder.fromUriString("http://localhost:8080/tokenChecker").queryParam("token", token);
		ResponseEntity<Object> checker = restTemplate.exchange(recovery.toUriString(), HttpMethod.GET, entityGenerator(token), Object.class);
		if(checker.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
			return "error/pwreset";
		}
		ResetPW reset = new ResetPW();
		reset.setToken(token);
		model.addAttribute("pwreset", reset);
		return "password-reset";
	}
	
	@GetMapping("/password-recovery")
	public String showPasswordRecovery(Model model) {
		EmailWrapper email = new EmailWrapper();
		model.addAttribute("email", email);
		return "password-recovery";
	}
	
	@GetMapping("/passowrd-change")
	public String changePassword(@CookieValue(name="token") String token, Model model) {
		ResetPW reset = new ResetPW();
		reset.setToken(token);
		model.addAttribute("pwreset", reset);
		return "password-change";
	}
	
	@GetMapping("/access-denied")
	public String showAccessDenied() {
		
		return "access-denied";
		
	}
	
	//Temp until emp controller available
	@GetMapping("/checkMail")
	public boolean checkValidEmail(@RequestBody EmailWrapper email) {
		HttpHeaders headers = new HttpHeaders();
		
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Location", "/loginpage");
		
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		HttpEntity<EmailWrapper> entity = new HttpEntity<>(email, headers);
		ResponseEntity<Object> checker = restTemplate.exchange("http://localhost:8080/users/emailChecker", HttpMethod.GET, entity, Object.class);
		
		return checker.getStatusCode()==HttpStatus.OK?true:false;
	}
	
	private void sendPWResetAuthLink(String email, String token) {
		SimpleMailMessage mail = new SimpleMailMessage();
		UriComponentsBuilder recovery = UriComponentsBuilder.fromUriString("http://localhost:8081/password-reset").queryParam("pwtoken", token);
		
		mail.setTo(email);
		mail.setSubject("Test passowrd recovery mail");
		mail.setText("Test email of issuing password recovery link:\n"
				+ "<a href='"+recovery.toUriString()+"/>");
		
		mailSender.send(mail);
		log.info("Password recovery email sent out to "+ email);
	}
	
	private <T> HttpEntity<T> entityGenerator(T input){
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		headers.set("Authorization", "Bearer"+token);
		
		if(input==null) {
			return new HttpEntity<T>(headers);		
		}else {
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			return new HttpEntity<T>(input, headers);
		}
	}
}
