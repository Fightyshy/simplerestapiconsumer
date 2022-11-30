package com.simplerestapiconsumer.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simplecrmapiconsumer.validation.AlphabetOnly;
import com.simplecrmapiconsumer.validation.Alphanumeric;
import com.simplecrmapiconsumer.validation.NumbersInStringOnly;

public class Address {

	private Integer id;

	@NotNull
	@NotEmpty
	private String typeOfAddress;

	@NotNull
	@NotEmpty
	private String line1;

	private String line2;

	private String line3;

	@NotNull
	@NotEmpty
	@Alphanumeric
	private String postcode;

	@NotNull
	@NotEmpty
	@AlphabetOnly
	private String country;

	private String province;

	@NotNull
	@NotEmpty
	private String city;

	@NotNull
	@NumbersInStringOnly
	private String phoneNumber;

	@NumbersInStringOnly
	private String faxNumber;
	
	@JsonIgnore
	private Customer customer;
	
//	@JsonIgnore
//	private Employee employee;

	public Address() {
	
	}

	public Address(Integer id, @NotNull @NotEmpty String typeOfAddress, @NotNull @NotEmpty String line1, String line2,
		String line3, @NotNull @NotEmpty String postcode, @NotNull @NotEmpty String country, String province,
		@NotNull @NotEmpty String city, @NotNull String phoneNumber, String faxNumber, Customer customer) {
	this.id = id;
	this.typeOfAddress = typeOfAddress;
	this.line1 = line1;
	this.line2 = line2;
	this.line3 = line3;
	this.postcode = postcode;
	this.country = country;
	this.province = province;
	this.city = city;
	this.phoneNumber = phoneNumber;
	this.faxNumber = faxNumber;
	this.customer = customer;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTypeOfAddress() {
		return typeOfAddress;
	}

	public void setTypeOfAddress(String typeOfAddress) {
		this.typeOfAddress = typeOfAddress;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

//	public Employee getEmployee() {
//		return employee;
//	}
//
//	public void setEmployee(Employee employee) {
//		this.employee = employee;
//	}
}
