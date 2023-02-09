package com.simplerestapiconsumer.entity;


public class AddEmployeeDTO {
	private int caseID;
	
	private Employee employee;

	public AddEmployeeDTO(int caseID, Employee employee) {
		this.caseID = caseID;
		this.employee = employee;
	}

	public AddEmployeeDTO() {
	}

	public int getCaseID() {
		return caseID;
	}

	public void setCaseID(int caseID) {
		this.caseID = caseID;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	
}
