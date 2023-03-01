package com.simplerestapiconsumer.entity;

public class IDWrapper {
	private int ID;
	private int caseId;

	public IDWrapper(int ID, int caseId) {
		this.ID = ID;
		this.caseId = caseId;
	}
	
	public IDWrapper() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public int getCaseId() {
		return caseId;
	}

	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}
	
}
