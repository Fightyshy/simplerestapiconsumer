package com.simplerestapiconsumer.entity;

import java.util.List;

import com.simplecrmapiconsumer.validation.AlphabetOnly;

public class Customer extends Person {

	private String prefComms;
	
	@AlphabetOnly
	private String occupation;
	
	@AlphabetOnly
	private String industry;
	
	private List<Address> address;

	public Customer(String prefComms, String occupation, String industry,
			List<com.simplerestapiconsumer.entity.Address> address) {
		this.prefComms = prefComms;
		this.occupation = occupation;
		this.industry = industry;
		this.address = address;
	}

	public Customer() {
		
	}

	public String getPrefComms() {
		return prefComms;
	}

	public void setPrefComms(String prefComms) {
		this.prefComms = prefComms;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return this.getFirstName()+" "+this.getLastName();
	}

	
}
