package de.bytefish.multitenancy.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class ApplicationRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    
    @ManyToMany(mappedBy = "roles")
    private Collection<ApplicationUser> users;

    public ApplicationRole() {}

    public ApplicationRole(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }

	public Collection<ApplicationUser> getUsers() {
		return users;
	}

	public void setUsers(Collection<ApplicationUser> users) {
		this.users = users;
	}
}