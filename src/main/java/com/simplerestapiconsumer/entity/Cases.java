package com.simplerestapiconsumer.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
@JsonIdentityInfo(
		scope = Cases.class,
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class Cases {

	private Integer id;
	
	@NotNull
	private String casesStatus; //Not null
	
	@NotNull
	private LocalDateTime startDate; //Not null

	private LocalDateTime endDate;
	
	private String summary;

	private Product product;
	
	private Set<Employee> employee;

	private Customer customer;
	
	public Cases() {
		this.casesStatus = "PENDING";
		this.startDate = LocalDateTime.now();
	}
	
	public Cases(@NotNull String casesStatus, String summary, Product product, Customer customer) {
		this.casesStatus = casesStatus;
		this.startDate = LocalDateTime.now();
		this.endDate = null;
		this.summary = summary;
		this.product  = product;
		this.customer = customer;
	}
	
	
	public Cases(Integer id, @NotNull String casesStatus, @NotNull LocalDateTime startDate, LocalDateTime endDate,
			String summary, Product product, Set<Employee> employee, Customer customer) {
		this.id = id;
		this.casesStatus = casesStatus;
		this.startDate = startDate;
		this.endDate = endDate;
		this.summary = summary;
		this.product = product;
		this.employee = employee;
		this.customer = customer;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCasesStatus() {
		return casesStatus;
	}

	public void setCasesStatus(String casesStatus) {
		this.casesStatus = casesStatus;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Set<Employee> getEmployee() {
		return employee;
	}

	public void setEmployee(Set<Employee> employee) {
		this.employee = employee;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public boolean equals(Object cases) {
		if(cases.getClass()!=this.getClass()) {
			return false;
		}
		
		Cases cased = (Cases) cases;
		
		if(cased.casesStatus!=this.casesStatus) {
			return false;
		}
		if(cased.startDate!=this.startDate) {
			return false;
		}
		if(cased.endDate!=this.endDate) {
			return false;
		}
		if(cased.product!=this.product) {
			return false;
		}
		if(!cased.customer.equals(this.customer)) {
			return false;
		}
		if(!cased.employee.equals(this.employee)) {
			return false;
		}
		return true;
	}
}
