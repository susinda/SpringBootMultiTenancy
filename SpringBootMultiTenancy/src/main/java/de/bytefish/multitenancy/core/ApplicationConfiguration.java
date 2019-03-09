package de.bytefish.multitenancy.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("application.properties")
public class ApplicationConfiguration {

    @Value("${server.verify.url}")
    private String serverVerifyUrl;
    
    @Value("${email.verify.template}")
    private String emailVerifyTemplate;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

	public String getServerVerifyUrl() {
		return serverVerifyUrl;
	}

	public String getEmailVerifyTemplate() {
		return emailVerifyTemplate;
	}

}