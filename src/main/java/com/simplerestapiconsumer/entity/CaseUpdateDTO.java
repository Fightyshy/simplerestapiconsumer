package com.simplerestapiconsumer.entity;

public class CaseUpdateDTO {
	private int caseId;
	
	private String status;

	public CaseUpdateDTO(int caseId, String status) {
		this.caseId = caseId;
		this.status = status;
	}

	public CaseUpdateDTO() {
	}

	public int getCaseId() {
		return caseId;
	}

	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
