// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.multitenancy.web.configuration;


import de.bytefish.multitenancy.JWTAuthenticationFilter;
import de.bytefish.multitenancy.web.filters.TenantNameFilter;
import de.bytefish.multitenancy.web.resources.AdminMgtResource;
import de.bytefish.multitenancy.web.resources.UserProfileResource;
import de.bytefish.multitenancy.web.resources.UserRegistrationResource;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * Jersey Configuration (Resources, Modules, Filters, ...)
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {

        // Register the Filters:
        register(TenantNameFilter.class);
        register(JWTAuthenticationFilter.class);

        // Register the Resources:
        register(UserRegistrationResource.class);
        register(AdminMgtResource.class);
        register(UserProfileResource.class);


        // Uncomment to disable WADL Generation:
        //property("jersey.config.server.wadl.disableWadl", true);

        // Uncomment to add Request Tracing:
        //property("jersey.config.server.tracing.type", "ALL");
        //property("jersey.config.server.tracing.threshold", "TRACE");
    }
}
