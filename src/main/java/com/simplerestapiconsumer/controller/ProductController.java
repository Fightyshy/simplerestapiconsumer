package com.simplerestapiconsumer.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.simplerestapiconsumer.entity.Product;
import com.simplerestapiconsumer.util.EntityGenerator;

@Controller
public class ProductController {
	private EntityGenerator entityGenerator = new EntityGenerator();
	
	private RestTemplate restTemplate;
	private Logger log;
	
	public ProductController(RestTemplate restTemplate, Logger log) {
		this.restTemplate = restTemplate;
		this.log = log;
	}
	
	//View endpoints
	
	@GetMapping("/list-products")
	public String showAllProducts(@CookieValue(name="token") String token, Model model) {
		List<Product> listProds = restTemplate.exchange("http://localhost:8080/products", HttpMethod.GET, entityGenerator.entityGenerator(token, null), new ParameterizedTypeReference<List<Product>>() {}).getBody();
		model.addAttribute("product", listProds);
		return "show-products";
	}
	
	@GetMapping("/save-product-form")
	public String showProductForm(@CookieValue(name="token") String token, Model model) {
		Product prod = new Product(0, null, null);
		model.addAttribute("product", prod);
		return "product-form";
	}
	
	@GetMapping("/update-product-form")
	public String showProductUpdateForm(@CookieValue(name="token") String token, Model model, @RequestParam("prodID") int prodID) {
		ResponseEntity<Product> prod = restTemplate.exchange("http://localhost:8080/products/id?id="+prodID, HttpMethod.GET, entityGenerator.entityGenerator(token, null), Product.class);
		model.addAttribute("product", prod.getBody());
		return "product-form";
		
	}
	
	//Consumer endpoints
	
	@GetMapping("/deleteProduct")
	 public String deleteProduct(@CookieValue(name="token") String token, Model model, @RequestParam("id") int id) {
		 restTemplate.exchange("http://localhost:8080/products/id?id="+id, HttpMethod.DELETE, entityGenerator.entityGenerator(token, null), Object.class);
		 return "redirect:/list-products";
	 }

	@PostMapping("/saveProduct")
	public String saveProduct(@CookieValue(name="token") String token, Model model, @ModelAttribute("product") Product prod) {
		log.info(prod.getSummary());
		ResponseEntity<Product> savedProduct = restTemplate.exchange("http://localhost:8080/products", HttpMethod.POST, entityGenerator.entityGenerator(token, prod), Product.class);
		log.info("Product id "+ savedProduct.getBody().getId()+" has been created");
		return "redirect:/list-products";
	}
	
	@PostMapping("/updateProduct")
	public String updateProduct(@CookieValue(name="token") String token, Model model, @ModelAttribute("product") Product prod) {
		ResponseEntity<Product> updated = restTemplate.exchange("http://localhost:8080/products", HttpMethod.PUT, entityGenerator.entityGenerator(token, prod), Product.class);
		log.info("Product id " + updated.getBody().getId()+" has been updated successfully");
		return "redirect:/list-products";
	}
	 //TODO PLACEHODLER
	//TODO CRUD FOR MANAGERS+
}
