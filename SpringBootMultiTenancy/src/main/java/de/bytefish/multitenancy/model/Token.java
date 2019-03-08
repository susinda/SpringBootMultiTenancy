package de.bytefish.multitenancy.model;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userEmail;
    private String token;
    private Date expiry;
    private boolean used;

    public Token() {}


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiry() {
		return expiry;
	}
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	
	 
    @Override
    public String toString() {
        return "Role{" +
            "id=" + id +
            ", token='" + token + '\'' +
            ", user='" + userEmail + '\'' +
            ", expiry='" + expiry + '\'' +
            ", used='" + used + '\'' +
            '}';
    }

}