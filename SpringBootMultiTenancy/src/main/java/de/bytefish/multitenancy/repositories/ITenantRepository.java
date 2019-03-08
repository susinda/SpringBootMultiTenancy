// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.multitenancy.repositories;

import org.springframework.data.repository.CrudRepository;

import de.bytefish.multitenancy.model.Tenant;

public interface ITenantRepository extends CrudRepository<Tenant, Long> {
	public Tenant findByTenantName(String name);
	public Tenant findByAdminEmail(String adminEmail);
}
