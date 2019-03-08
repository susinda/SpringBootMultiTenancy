// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.multitenancy.web.resources;

import de.bytefish.multitenancy.RegistrationService;
import de.bytefish.multitenancy.model.ApplicationUser;
import de.bytefish.multitenancy.repositories.IUserRepository;
import de.bytefish.multitenancy.web.model.RegisterRequestDTO;
import de.bytefish.multitenancy.web.model.StringResponse;
import de.bytefish.multitenancy.web.model.VerificationRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Path("/sign-up")
public class UserRegistrationResource {

    private final IUserRepository repository;
    
    @Autowired
    RegistrationService registrationService;

    @Autowired
    public UserRegistrationResource(IUserRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApplicationUser> getAll() {
        // Return the DTO List:
        return StreamSupport.stream(repository.findAll().spliterator(), false)
        		//.map(u -> new ApplicationUser (u.getFirstName(), u.getLastName(), u.getEmail(), ""))
                .collect(Collectors.toList());
    }

    @GET
    @Path("{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationUser get(@PathParam("email") String email) {
        ApplicationUser user = repository.findByEmail(email);
        return user;
    }

    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/admin")
    public StringResponse registerTenant(RegisterRequestDTO trrDTO) {
    	StringResponse res = registrationService.registerTenant(trrDTO.getUserEmail(), trrDTO.getName());
    	return res;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user")
    public StringResponse registerUser(RegisterRequestDTO trrDTO) {
    	StringResponse res = registrationService.registerUser(trrDTO.getUserEmail(), trrDTO.getName());
    	return res;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/verify")
    public StringResponse verifyUser(VerificationRequestDTO vrDTO) {
    	StringResponse res = registrationService.verifyUser(vrDTO.getToken(), vrDTO.getFirstName(), vrDTO.getLastName(), vrDTO.getPassword());
    	return res;
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("id") long id) {
        repository.delete(id);
    }
}
