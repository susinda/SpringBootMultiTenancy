package de.bytefish.multitenancy.web.model;

public class RegisterRequestDTO {
	
	String name;
	String userEmail;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

}
