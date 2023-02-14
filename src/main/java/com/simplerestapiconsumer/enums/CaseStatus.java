package com.simplerestapiconsumer.enums;

public enum CaseStatus {
	ACTIVE ("Active"), //being worked on
	PENDING("Pending"), //unknown status indicator
	RESOLVED("Resolved"), //closed with resolution
	CLOSED("Closed"); //closed with no resolution
	
	private final String displayName;
	
	private CaseStatus(String displayName) {
		this.displayName = displayName;
	}


	public String getDisplayName() {
		return displayName;
	}
}
