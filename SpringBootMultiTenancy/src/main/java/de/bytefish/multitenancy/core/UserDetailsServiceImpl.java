package de.bytefish.multitenancy.core;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.bytefish.multitenancy.model.ApplicationRole;
import de.bytefish.multitenancy.model.ApplicationUser;
import de.bytefish.multitenancy.repositories.IUserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private IUserRepository applicationUserRepository;

	public UserDetailsServiceImpl(IUserRepository applicationUserRepository) {
		this.applicationUserRepository = applicationUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ApplicationUser applicationUser = applicationUserRepository.findByEmail(username);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(username);
		}

		List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
		for (ApplicationRole role : applicationUser.getRoles()) {
			SimpleGrantedAuthority sga = new SimpleGrantedAuthority(role.getName());
			authorityList.add(sga);
		}
		return new User(applicationUser.getEmail(), applicationUser.getPassword(), authorityList);
	}
}