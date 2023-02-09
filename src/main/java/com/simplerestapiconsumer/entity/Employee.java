package com.simplerestapiconsumer.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
@JsonIdentityInfo(
		scope = Employee.class,
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class Employee extends Person{

	@PositiveOrZero(message="Please input a number greater than or equal to 0")
	private Integer casesActive;

	@PositiveOrZero(message="Please input a number greater than or equal to 0")
	private Integer casesPending;

	@PositiveOrZero(message="Please input a number greater than or equal to 0")
	private Integer casesResolved;

	@PositiveOrZero(message="Please input a number greater than or equal to 0")
	private Integer casesClosed;

	private Set<Cases> cases;

	private List<Address> address;

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public Employee(Integer id, @NotNull @NotEmpty String firstName, String middleName,
			@NotNull @NotEmpty String lastName,
			@NotNull @Past(message = "Date has to be in the past") LocalDate dateOfBirth, String phoneNumber,
			@Email(message = "Please input a valid email address") @NotNull String emailAddress,
			SocialMedia socialMedia,
			@PositiveOrZero(message = "Please input a number greater than or equal to 0") Integer casesActive,
			@PositiveOrZero(message = "Please input a number greater than or equal to 0") Integer casesPending,
			@PositiveOrZero(message = "Please input a number greater than or equal to 0") Integer casesResolved,
			@PositiveOrZero(message = "Please input a number greater than or equal to 0") Integer casesClosed,
			Set<Cases> cases, List<Address> address) {
		super(id, firstName, middleName, lastName, dateOfBirth, phoneNumber, emailAddress, socialMedia);
		this.casesActive = casesActive;
		this.casesPending = casesPending;
		this.casesResolved = casesResolved;
		this.casesClosed = casesClosed;
		this.cases = cases;
		this.address = address;
	}

	public Employee() {
	}

	public Integer getCasesActive() {
		return casesActive;
	}

	public void setCasesActive(Integer casesActive) {
		this.casesActive = casesActive;
	}

	public Integer getCasesPending() {
		return casesPending;
	}

	public void setCasesPending(Integer casesPending) {
		this.casesPending = casesPending;
	}

	public Integer getCasesResolved() {
		return casesResolved;
	}

	public void setCasesResolved(Integer casesResolved) {
		this.casesResolved = casesResolved;
	}

	public Integer getCasesClosed() {
		return casesClosed;
	}

	public void setCasesClosed(Integer casesClosed) {
		this.casesClosed = casesClosed;
	}

	public Set<Cases> getCases() {
		return cases;
	}

	public void setCases(Set<Cases> cases) {
		this.cases = cases;
	}

	//Set checker solution
	//https://stackoverflow.com/questions/9318909/contains-method-not-calling-overridden-equals-method
	@Override
	public boolean equals(Object emp) {
		if(emp instanceof Employee) {
			Employee comparator = (Employee) emp;
			return this.id.equals(comparator.id);
		}
		return false;
		
//		if(emp.getClass()!=this.getClass()) {
//			return false;
//		}
//		
//		Employee emped = (Employee) emp;
//		
//		return emped.getId()==this.getId()?true:false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return id.hashCode(); //id is immutable in this case (either deleted along with entity or never changes
	}
	
	
	
	
}
