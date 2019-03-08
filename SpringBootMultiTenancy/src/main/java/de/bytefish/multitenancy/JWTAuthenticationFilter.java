package de.bytefish.multitenancy;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bytefish.multitenancy.model.ApplicationUser;
import de.bytefish.multitenancy.repositories.IUserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static de.bytefish.multitenancy.SecurityConstants.EXPIRATION_TIME;
import static de.bytefish.multitenancy.SecurityConstants.HEADER_STRING;
import static de.bytefish.multitenancy.SecurityConstants.SECRET;
import static de.bytefish.multitenancy.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	//@Autowired 
	private final IUserRepository userRepository;
	  
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, IUserRepository repository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = repository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
           ApplicationUser creds = new ObjectMapper()
                    .readValue(req.getInputStream(), ApplicationUser.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

    	String token = JWT.create()
                .withSubject(((User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        
        ApplicationUser aUser = userRepository.findByEmail(((User) auth.getPrincipal()).getUsername());
        if (aUser.getTenant() != null) {
            res.addHeader("Tenant-Id", aUser.getTenant().getTenantName());
        }
    }
}