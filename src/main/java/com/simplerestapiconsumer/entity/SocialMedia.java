package com.simplerestapiconsumer.entity;

import javax.validation.constraints.NotNull;

import com.simplerestapiconsumer.enums.SocialMediaNames;

public class SocialMedia {

	private Integer id;
	
	@NotNull
	private String preferredSocialMedia; //Incase prefcomms is social media, "None" for no socialMedia pref
	
	private String facebookHandle;
	
	private String twitterHandle;
	
	private String instagramHandle;
	
	private String lineHandle;
	
	private String whatsappHandle;
	
	//default values to none if construction w/o args as null is for errors
	public SocialMedia() {
		this.preferredSocialMedia=SocialMediaNames.NONE.toString();
		this.facebookHandle=SocialMediaNames.NONE.toString();
		this.twitterHandle=SocialMediaNames.NONE.toString();
		this.instagramHandle=SocialMediaNames.NONE.toString();
		this.lineHandle=SocialMediaNames.NONE.toString();
		this.whatsappHandle=SocialMediaNames.NONE.toString();
	}

	public SocialMedia(String preferredSocialMedia, String facebookHandle, String twitterHandle,
			String instagramHandle, String lineHandle, String whatsappHandle) {
		this.preferredSocialMedia = preferredSocialMedia;
		this.facebookHandle = facebookHandle;
		this.twitterHandle = twitterHandle;
		this.instagramHandle = instagramHandle;
		this.lineHandle = lineHandle;
		this.whatsappHandle = whatsappHandle;
	}
	
	public SocialMedia(String preferredSocialMedia) {
		this.preferredSocialMedia = preferredSocialMedia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPreferredSocialMedia() {
		return preferredSocialMedia;
	}

	public void setPreferredSocialMedia(String preferredSocialMedia) {
		this.preferredSocialMedia = preferredSocialMedia;
	}

	public String getFacebookHandle() {
		return facebookHandle;
	}

	public void setFacebookHandle(String facebookHandle) {
		this.facebookHandle = nullChecker(facebookHandle);
	}

	public String getTwitterHandle() {
		return twitterHandle;
	}

	public void setTwitterHandle(String twitterHandle) {
		this.twitterHandle = nullChecker(twitterHandle);
	}

	public String getInstagramHandle() {
		return instagramHandle;
	}

	public void setInstagramHandle(String instagramHandle) {
		this.instagramHandle = nullChecker(instagramHandle);
	}

	public String getLineHandle() {
		return lineHandle;
	}

	public void setLineHandle(String lineHandle) {
		this.lineHandle = nullChecker(lineHandle);
	}

	public String getWhatsappHandle() {
		return whatsappHandle;
	}

	public void setWhatsappHandle(String whatsappHandle) {
		this.whatsappHandle = nullChecker(whatsappHandle);
	}
	
	public boolean validSMChecker(String input) {
		for(SocialMediaNames names:SocialMediaNames.values()) {
			if(names.toString().equals(input)) {
				return true;
			}
		}
		return false;
	}
	
	private String nullChecker(String input) {
		if(input==null||input.isEmpty()||input.isBlank()) {
			return SocialMediaNames.NONE.toString();
		}else {
			return input;
		}
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	
}
