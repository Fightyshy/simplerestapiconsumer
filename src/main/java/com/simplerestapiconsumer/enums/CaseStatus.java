package com.simplerestapiconsumer.enums;

public enum CaseStatus {
	ACTIVE ("active"), //being worked on
	PENDING("pending"), //unknown status indicator
	RESOLVED("resolved"), //closed with resolution
	CLOSED("closed"); //closed with no resolution
	
	private final String displayName;
	
	private CaseStatus(String displayName) {
		this.displayName = displayName;
	}


	public String getDisplayName() {
		return displayName;
	}
}
