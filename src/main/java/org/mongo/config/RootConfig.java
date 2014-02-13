package org.mongo.config;


import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Locator;
import net.spy.memcached.FailureMode;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.transcoders.Transcoder;

import org.mongo.utils.CustomSerializingTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@PropertySource({"classpath:social.properties"})
@ComponentScan(basePackages={"org.mongo.service","org.mongo.domain"})
@EnableScheduling
public class RootConfig {
	
	@Autowired
	Environment env;
	
	protected static Logger logger = LoggerFactory
			.getLogger(RootConfig.class);
	
	/*public @Bean SchedulerService service(){
		return new SchedulerService();
	}*/
	
	public @Bean
	AuthDescriptor descriptor() {
		AuthDescriptor ad = new AuthDescriptor(new String[] { "PLAIN" },
				new PlainCallbackHandler(
						env.getProperty("memcachier.username"),
						env.getProperty("memcachier.password")));
		return ad;
	}
	/**
	 * https://code.google.com/p/spymemcached/wiki/SpringIntegration
	 * @return
	 * @throws Exception
	 */
	public @Bean
		MemcachedClient memCachier() throws Exception {
		MemcachedClient mc = new MemcachedClient(new ConnectionFactoryBuilder()
				.setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
				.setAuthDescriptor(descriptor())
				.setFailureMode(FailureMode.Redistribute)
				.setHashAlg(HashAlgorithm.KETAMA_HASH)
				.setUseNagleAlgorithm(false)
				.setLocatorType(Locator.CONSISTENT)
				.setOpTimeout(1000)
				.setTranscoder(transcoder())
				.setTimeoutExceptionThreshold(1998).build(),
				AddrUtil.getAddresses(env.getProperty("memcachier.server")));
		 return mc;
	}
	Transcoder<Object> transcoder(){
		CustomSerializingTranscoder transcoder = new CustomSerializingTranscoder();
		transcoder.setCompressionThreshold(1000);
		return transcoder;
	}
	
	
	
}
