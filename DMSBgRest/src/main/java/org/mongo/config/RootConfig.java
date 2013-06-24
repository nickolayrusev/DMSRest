package org.mongo.config;

import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Locator;
import net.spy.memcached.FailureMode;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.spring.MemcachedClientFactoryBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
	
	public @Bean
	AuthDescriptor descriptor() {
		AuthDescriptor ad = new AuthDescriptor(new String[] { "PLAIN" },
				new PlainCallbackHandler(
						env.getProperty("memcachier.username"),
						env.getProperty("memcachier.password")));
		return ad;
	}

	public @Bean
		MemcachedClient memCachier() throws Exception {
		MemcachedClientFactoryBean factoryBean = new MemcachedClientFactoryBean();
		factoryBean.setAuthDescriptor(descriptor());
		factoryBean.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY);
		factoryBean.setServers(env.getProperty("memcachier.server"));
		factoryBean.setUseNagleAlgorithm(false);
		factoryBean.setHashAlg(HashAlgorithm.KETAMA_HASH);
		factoryBean.setOpTimeout(1000);
		factoryBean.setLocatorType(Locator.CONSISTENT);
		factoryBean.setFailureMode(FailureMode.Redistribute);
		factoryBean.setTimeoutExceptionThreshold(1998);
		return (MemcachedClient) factoryBean.getObject();
	}
	
	
	
}
