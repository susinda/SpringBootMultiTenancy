package de.bytefish.multitenancy.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.bytefish.multitenancy.model.ApplicationUser;
import de.bytefish.multitenancy.model.ApplicationRole;
import de.bytefish.multitenancy.model.Tenant;
import de.bytefish.multitenancy.model.Token;
import de.bytefish.multitenancy.repositories.IRoleRepository;
import de.bytefish.multitenancy.repositories.ITenantRepository;
import de.bytefish.multitenancy.repositories.ITokenRepository;
import de.bytefish.multitenancy.repositories.IUserRepository;
import de.bytefish.multitenancy.web.dto.StringResponse;

@Service
public class RegistrationService  {
	
    private IUserRepository userRepository;
    private ITenantRepository tenantRepository;
    private IRoleRepository  roleRepository;
    private ITokenRepository tokenRepository;
    
    @Autowired
    public EmailServiceImpl emailService;
    
    @Autowired
    public ApplicationConfiguration configs;
    
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    public RegistrationService(IUserRepository applicationUserRepository, ITenantRepository tenantRepository,
    		IRoleRepository  roleRepository, ITokenRepository tokenRepository) {
        this.userRepository = applicationUserRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
    }

    
    public StringResponse registerTenant(String userEmail, String tenantName) {
    	StringResponse sr = new StringResponse();
        ApplicationUser admin = userRepository.findByEmail(userEmail);
        if (admin != null) {
           sr.setResult("User with given email address is already registered");
           return sr;
        }
        
        Tenant tenant = tenantRepository.findByTenantName(tenantName);
        if (tenant != null) {
           sr.setResult("Tenant name is not avaialble, pls try a different name");
           return sr;
        }
        
        
        tenant = new Tenant();
        tenant.setTenantName(tenantName);
        tenant.setAdminEmail(userEmail);
        tenant.setCreatedDate(new Date());
        tenant.setVerified(false);
        tenantRepository.save(tenant);
        
        ApplicationRole adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null ) {
        	adminRole = new ApplicationRole();
        	adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
        
        List<ApplicationRole> roles = new ArrayList<ApplicationRole>();
        roles.add(adminRole);
        
        admin = new ApplicationUser();
        admin.setEmail(userEmail);
        admin.setVerified(false);
        admin.setTenant(tenant);
        admin.setRoles(roles);
        userRepository.save(admin);
        
        Collection<ApplicationUser> existingAdminUsers = adminRole.getUsers();
        if (existingAdminUsers == null) {
        	existingAdminUsers = new ArrayList<ApplicationUser>();
        	existingAdminUsers.add(admin);
        }
        roleRepository.save(adminRole);
        
        Token token = new Token();
        token.setUserEmail(userEmail);
        token.setUsed(false);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiry( get24HoursTime());
        tokenRepository.save(token);

        String url = configs.getServerVerifyUrl();
        String mailBody = String.format(configs.getEmailVerifyTemplate(), url, token.getToken());
        
    	emailService.sendSimpleMessage(userEmail, "Account verification required", mailBody);

        sr.setResult("Success");
        return sr;
    }
    
    
    public StringResponse registerUser(String userEmail, String tenantName) {
    	StringResponse sr = new StringResponse();
        ApplicationUser applicationUser = userRepository.findByEmail(userEmail);
        if (applicationUser != null) {
           sr.setResult("User with given email address is already registered");
           return sr;
        }
        
        Tenant tenant = tenantRepository.findByTenantName(tenantName);
        if (tenant == null) {
           sr.setResult("No Tenant found for the given TenantName");
           return sr;
        }
        
        ApplicationRole userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null ) {
        	userRole = new ApplicationRole();
        	userRole.setName("ROLE_USER");
        	roleRepository.save(userRole);
        }
        List<ApplicationRole> roles = new ArrayList<ApplicationRole>();
        roles.add(userRole);
        
        applicationUser = new ApplicationUser();
        applicationUser.setEmail(userEmail);
        applicationUser.setVerified(false);
        applicationUser.setTenant(tenant);
        applicationUser.setRoles(roles);
        userRepository.save(applicationUser);
        
        Collection<ApplicationUser> existingUserRoleUsers = userRole.getUsers();
        if (existingUserRoleUsers == null) {
        	existingUserRoleUsers = new ArrayList<ApplicationUser>();
        	existingUserRoleUsers.add(applicationUser);
        }
        roleRepository.save(userRole);
        
        Token token = new Token();
        token.setUserEmail(userEmail);
        token.setUsed(false);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiry( get24HoursTime());
        tokenRepository.save(token);
        
    	emailService.sendSimpleMessage(userEmail, "Account verification required", "http://localhost:8080/email-verify/"+ token.getToken());

        sr.setResult("Success");
        return sr;
    }

    
    public StringResponse verifyUser(String token, String firstName, String lastName, String password) {
    	StringResponse sr = new StringResponse();
    	Token t = tokenRepository.findByToken(token);
    	if (t == null) {
    		sr.setResult("to such token found");
    		return sr;
    	} else {
    		if (new Date().after(t.getExpiry())) {
    			sr.setResult("to such token found");
        		return sr;
    		}
    		if (t.isUsed()) {
    			sr.setResult("Token is already used");
        		return sr;
    		}
    		
    		ApplicationUser applicationUser = userRepository.findByEmail(t.getUserEmail());
            if (applicationUser == null) {
            	sr.setResult("No user with given email address");
               return sr;
            }   
            
            for (ApplicationRole role : applicationUser.getRoles()) {
            	System.out.println(role.getName());
            	if ("ADMIN".equals(role.getName())) {
            		Tenant tenant = tenantRepository.findByAdminEmail(t.getUserEmail());
                    tenant.setVerified(true);
                    tenantRepository.save(tenant);
            	}
            }
            applicationUser.setFirstName(firstName);
            applicationUser.setLastName(lastName);
            applicationUser.setPassword(passwordEncoder().encode(password));
            applicationUser.setVerified(true);
            userRepository.save(applicationUser);
            tokenRepository.delete(t);
            
         }
    	sr.setResult("Success");
    	return sr;
    }

    
	private Date get24HoursTime() {
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); 
        cal.add(Calendar.HOUR_OF_DAY, 24); // adds one day
        return cal.getTime();
	}
    
}