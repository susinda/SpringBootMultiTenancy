package de.bytefish.multitenancy;

import com.zaxxer.hikari.HikariDataSource;
import de.bytefish.multitenancy.routing.TenantAwareRoutingSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication
@EnableTransactionManagement
public class SampleJerseyApplication extends SpringBootServletInitializer {

	AbstractRoutingDataSource dataSource;
	
	public static void main(String[] args) {
		new SampleJerseyApplication()
				.configure(new SpringApplicationBuilder(SampleJerseyApplication.class))
				.properties(getDefaultProperties())
				.run(args);
	}


	@Bean
	public DataSource dataSource() {

		dataSource = new TenantAwareRoutingSource();
		Map<Object,Object> targetDataSources = new HashMap<>();

		//targetDataSources.put("TenantOne", tenantOne());
		//targetDataSources.put("TenantTwo", tenantTwo());
		
		 File[] files = Paths.get("tenants").toFile().listFiles();
	        for(File propertyFile : files) {
	            Properties tenantProperties = new Properties();
	            //DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create(this.getClass().getClassLoader());
	            System.out.println("\n\t Reloading tenant Database !!!!!! ");
	            try {
	                tenantProperties.load(new FileInputStream(propertyFile));
	               
	                String tenantId = tenantProperties.getProperty("name");
	                String driver = tenantProperties.getProperty("datasource.driver");
	                String url = tenantProperties.getProperty("datasource.url");
	                String user = tenantProperties.getProperty("datasource.username");
	                String pass = tenantProperties.getProperty("datasource.password");
	                DataSource tenantDataSource = getTenantDatasource(driver, url, user, pass);
	                if (tenantId.equals("jasiya")) {
	            	    dataSource.setDefaultTargetDataSource(tenantDataSource);
	                } else {
		                targetDataSources.put(tenantId, tenantDataSource);
	                }
	                
	            } catch (IOException e) {
	                e.printStackTrace();
	                return null;
	            }
	        }

    	//dataSource.setDefaultTargetDataSource(tenantRoot());
		dataSource.setTargetDataSources(targetDataSources);

		dataSource.afterPropertiesSet();
		
		return dataSource;
	}

	private static Properties getDefaultProperties() {

		Properties defaultProperties = new Properties();

		// Set sane Spring Hibernate properties:
		defaultProperties.put("spring.jpa.show-sql", "true");
		defaultProperties.put("spring.jpa.hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
		defaultProperties.put("spring.datasource.initialize", "true");

		// Prevent JPA from trying to Auto Detect the Database:
		//defaultProperties.put("spring.jpa.database", "postgresql");

		defaultProperties.put("spring.jpa.generate-ddl", "true");
		// Prevent Hibernate from Automatic Changes to the DDL Schema:
		defaultProperties.put("spring.jpa.hibernate.ddl-auto", "update");

		
		defaultProperties.put("spring.mail.host", "smtp.mailhost.com");
		defaultProperties.put("spring.mail.username", "mailUser");
		defaultProperties.put("spring.mail.password" , "mailPass");
		defaultProperties.put("spring.mail.port" , " 587");
		defaultProperties.put("spring.mail.properties.mail.smtp.auth", "true");
		defaultProperties.put("spring.mail.properties.mail.smtp.starttls.enable" , "true");
		return defaultProperties; 
	}

	
	private DataSource getTenantDatasource(String driver, String url, String username, String password) {

		HikariDataSource dataSource = new HikariDataSource();

		dataSource.setInitializationFailTimeout(0);
		dataSource.setMaximumPoolSize(5);
		if (driver != null) {
			dataSource.setDataSourceClassName(driver);
		}
		dataSource.setJdbcUrl(url);
		dataSource.addDataSourceProperty("url", url);
		dataSource.addDataSourceProperty("user", username);
		dataSource.addDataSourceProperty("password", password);

		return dataSource;
	}
	
	

}