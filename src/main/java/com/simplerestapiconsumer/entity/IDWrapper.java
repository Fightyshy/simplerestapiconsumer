package com.simplerestapiconsumer.entity;

public class IDWrapper {
	private int ID;
	private int caseId;

	public IDWrapper(int iD, int caseId) {
		ID = iD;
		caseId = caseId;
	}
	
	public IDWrapper() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getCaseId() {
		return caseId;
	}

	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}
	
}
