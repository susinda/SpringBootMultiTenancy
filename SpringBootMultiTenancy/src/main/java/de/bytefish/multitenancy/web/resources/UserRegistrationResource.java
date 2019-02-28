// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.multitenancy.web.resources;

import de.bytefish.multitenancy.model.User;
import de.bytefish.multitenancy.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Path("/registration")
public class UserRegistrationResource {

    private final IUserRepository repository;
    
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//    
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Autowired
    public UserRegistrationResource(IUserRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAll() {
        // Return the DTO List:
        return StreamSupport.stream(repository.findAll().spliterator(), false)
        		.map(u -> new User (u.getFirstName(), u.getLastName(), u.getEmail(), ""))
                .collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User get(@PathParam("email") String email) {
        User user = repository.findByEmail(email);
        user.setPassword("");
        return user;
    }

    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public User post(User regUser) {
    	
    	User existing = repository.findByEmail(regUser.getEmail());
        if (existing != null) {
            System.out.println("There is already an account registered with that email");
            return null;
        }
        regUser.setPassword(passwordEncoder().encode(regUser.getPassword()));

        User result = repository.save(regUser);
        result.setPassword("");
        return result;
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("id") long id) {
        repository.delete(id);
    }
}
