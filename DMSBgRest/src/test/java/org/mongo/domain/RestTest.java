package org.mongo.domain;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongo.config.RootConfig;
import org.mongo.config.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ContextConfiguration( classes={WebConfig.class,RootConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public  class RestTest {
	
	private static final Logger logger = LoggerFactory.getLogger(RestTest.class);
	
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mvc;

	@Before
	public void setUp() {
		this.mvc = webAppContextSetup(this.wac).build();
	}
	@Test
	public void campaigns() throws Exception {
		MvcResult andReturn = this.mvc
				.perform(get("/campaign?page=1&type=0").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		logger.info("response as string:"
				+ andReturn.getResponse().getContentAsString());
	}
	
	@Test
	public void testString() throws Exception {
		MvcResult andReturn = this.mvc
				.perform(get("/testString").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		logger.info("response as string:"
				+ andReturn.getResponse().getContentAsString());
	}
}
