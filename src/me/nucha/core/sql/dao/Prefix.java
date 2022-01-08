package me.nucha.core.sql.dao;

public class Prefix {

	private String id;
	private String prefix;
	private String description;

	public Prefix(String id, String prefix, String description) {
		this.id = id;
		this.prefix = prefix;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getDescription() {
		return description;
	}

}
