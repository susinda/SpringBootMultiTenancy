
package de.bytefish.multitenancy.repositories;

import org.springframework.data.repository.CrudRepository;
import de.bytefish.multitenancy.model.ApplicationRole;

public interface IRoleRepository extends CrudRepository<ApplicationRole, Long> {
	public ApplicationRole findByName(String name);
}
