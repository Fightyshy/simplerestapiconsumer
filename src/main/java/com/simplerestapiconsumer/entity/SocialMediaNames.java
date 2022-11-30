package com.simplerestapiconsumer.entity;

public enum SocialMediaNames {
	FACEBOOK("Facebook"),
	TWITTER("Twitter"),
	INSTAGRAM("Instagram"),
	LINE("Line"),
	WHATSAPP("Whatsapp"),
	NO_PREFERENCE("No preference"),
	NONE("None"); //Special to denote "empty" SocialMedia object
	
	private final String displayName;
	
	private SocialMediaNames(String displayName) {
		this.displayName = displayName;
	}


	public String getDisplayName() {
		return displayName;
	}
	
	
}
