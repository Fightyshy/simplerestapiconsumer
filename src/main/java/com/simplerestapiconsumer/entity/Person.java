package com.simplerestapiconsumer.entity;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.springframework.format.annotation.DateTimeFormat;

import com.simplecrmapiconsumer.validation.AlphabetOnly;
import com.simplecrmapiconsumer.validation.NumbersInStringOnly;

public class Person {

	private Integer id;

	@NotNull
	@NotEmpty
	@AlphabetOnly(message="Please use alphabetical characters only")
	private String firstName;
	
	@AlphabetOnly(message="Please use alphabetical characters only")
	private String middleName;
	
	@NotNull
	@NotEmpty
	@AlphabetOnly(message="Please use alphabetical characters only")
	private String lastName;
	
	@NotNull
	@Past(message="Date has to be in the past")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate dateOfBirth; //use this instead of java.Date.util (depreciated)
	//sauce https://stackoverflow.com/questions/43039614/insert-fetch-java-time-localdate-objects-to-from-an-sql-database-such-as-h2
	
	@NumbersInStringOnly(message="Please use numbers only")
	private String phoneNumber;
	
	@Email(message="Please input a valid email address")
	@NotNull
	private String emailAddress;
	
	private SocialMedia socialMedia;
	
	public Person() {
		this.firstName = "";
		this.lastName = "";
		this.dateOfBirth = LocalDate.now();
	}

	public Person(Integer id, @NotNull @NotEmpty String firstName, String middleName,
			@NotNull @NotEmpty String lastName,
			@NotNull @Past(message = "Date has to be in the past") LocalDate dateOfBirth, String phoneNumber,
			@Email(message = "Please input a valid email address") @NotNull String emailAddress,
			SocialMedia socialMedia) {
		this.id = id;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
		this.socialMedia = socialMedia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public SocialMedia getSocialMedia() {
		return socialMedia;
	}

	public void setSocialMedia(SocialMedia socialMedia) {
		this.socialMedia = socialMedia;
	}
}
