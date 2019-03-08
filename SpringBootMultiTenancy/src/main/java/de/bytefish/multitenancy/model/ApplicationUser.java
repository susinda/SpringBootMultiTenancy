package de.bytefish.multitenancy.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	private String firstName;
    private String lastName;
    private String email;
    private String password;
    
    @ManyToOne()
    @JoinColumn(name="tenant_id")
    private Tenant tenant;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( name = "users_roles", 
    joinColumns = @JoinColumn( name = "user_id", referencedColumnName = "id"), 
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")) 
    private Collection<ApplicationRole> roles;
    
    boolean verified;

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public Collection<ApplicationRole> getRoles() {
		return roles;
	}
	public void setRoles(Collection<ApplicationRole> roles) {
		this.roles = roles;
	}

	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public ApplicationUser() {
		
	}


    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", password='" + "*********" + '\'' +
            ", roles=" + roles + '\'' +
            ", tenant=" + tenant + '\'' +
            ", verified=" + verified + '\'' +
            '}';
    }
}