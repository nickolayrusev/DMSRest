package org.mongo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource({"classpath:social.properties"})
@ComponentScan(basePackages={"org.mongo.service","org.mongo.domain"})
public class RootConfig {
	 
	@Autowired
	Environment env;
	
}
