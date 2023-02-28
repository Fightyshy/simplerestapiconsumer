package com.simplerestapiconsumer.entity;
public class Product {
	
	private Integer id;

	private String name;

	private String summary;

	public Product() {
	}

	public Product(Integer id, String name, String summary) {
		this.id = id;
		this.name = name;
		this.summary = summary;
	}

	public Product(String name, String summary) {
		this.name = name;
		this.summary = summary;
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
