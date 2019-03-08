// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.multitenancy.web.resources;

import org.springframework.stereotype.Component;

import javax.ws.rs.*;

@Component
@Path("/mgtconsole")
public class AdminMgtResource {

    

    @GET
   // @Produces(MediaType.APPLICATION_JSON)
    public String getAll() {
        return "mgt console returned";
    }


}
