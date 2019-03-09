package de.bytefish.multitenancy.core.security;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import de.bytefish.multitenancy.model.ApplicationRole;
import de.bytefish.multitenancy.model.ApplicationUser;
import de.bytefish.multitenancy.repositories.IUserRepository;

import static de.bytefish.multitenancy.core.security.JwtSecurityConstants.HEADER_STRING;
import static de.bytefish.multitenancy.core.security.JwtSecurityConstants.SECRET;
import static de.bytefish.multitenancy.core.security.JwtSecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	IUserRepository userRepo;
    public JWTAuthorizationFilter(AuthenticationManager authManager, IUserRepository userRepo) {
        super(authManager);
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            if (user != null) {
            	ApplicationUser aUser = userRepo.findByEmail(user);
	        	List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
            	if (aUser != null) {
		              for (ApplicationRole role : aUser.getRoles()) {
		              	SimpleGrantedAuthority sga = new SimpleGrantedAuthority(role.getName());
		              	authorityList.add(sga);
		            }
            	}
                return new UsernamePasswordAuthenticationToken(user, null, authorityList);
            }
            return null;
        }
        return null;
    }
}