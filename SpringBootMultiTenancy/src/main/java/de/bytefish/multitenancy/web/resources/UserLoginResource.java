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

@Component
@Path("/login")
public class UserLoginResource {

    private final IUserRepository repository;
    
    //@Autowired
    //private BCryptPasswordEncoder passwordEncoder;
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Autowired
    public UserLoginResource(IUserRepository repository) {
        this.repository = repository;
    }

    

    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public LoginResponse post(LoginRequest loginInfo) {
    	LoginResponse res = new LoginResponse();
    	User existing = repository.findByEmail(loginInfo.getEmail());
        if (existing == null) {
            System.out.println("There is already an account registered with that email");
            return null;
        }
        boolean isSame = passwordEncoder().matches(loginInfo.getPassword(), existing.getPassword());
        if (isSame) {
        	res.setResult("Authenticated");
        } else {
        	res.setResult("Failed");
        }
        return res;
    }

   
}
