// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package de.bytefish.multitenancy.repositories;

import org.springframework.data.repository.CrudRepository;
import de.bytefish.multitenancy.model.Token;

public interface ITokenRepository extends CrudRepository<Token, Long> {
	public Token findByUserEmail(String email);
	public Token findByToken(String token);
}
