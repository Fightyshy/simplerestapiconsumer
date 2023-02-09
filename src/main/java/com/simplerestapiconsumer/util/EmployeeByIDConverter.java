package com.simplerestapiconsumer.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.client.RestTemplate;

import com.simplerestapiconsumer.entity.Customer;
import com.simplerestapiconsumer.entity.Employee;

public class EmployeeByIDConverter implements Converter<String, Employee> {

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public Employee convert(String source) {
		Employee emp = restTemplate.getForObject("http://localhost:8080/employees/id?id="+source, Employee.class);
		return emp;
	}

}
