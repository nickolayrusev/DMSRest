package org.mongo.domain;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongo.config.RootConfig;
import org.mongo.config.WebConfig;
import org.mongo.utils.CommonUtils;
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
	public void campaignsWithoutParams() throws Exception {
		MvcResult andReturn = this.mvc
				.perform(get("/campaign").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		logger.info("response as string:"
				+ andReturn.getResponse().getContentAsString());
	}
	
	@Test
	public void campaignsPeople() throws Exception {
		MvcResult andReturn = this.mvc
				.perform(get("/campaign?type=0&page=0").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		logger.info("response as string:"
				+ andReturn.getResponse().getContentAsString());
	}
	@Test
	public void campaignsOrganizations() throws Exception {
		MvcResult andReturn = this.mvc
				.perform(get("/campaign?type=1&page=0").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		logger.info("response as string:"
				+ andReturn.getResponse().getContentAsString());
	}
	
	@Test
	public void testSum() throws Exception {
		String first = CommonUtils.parseSum("25 000 е вро");
		String second = CommonUtils.parseSum("25 000 лева");
		logger.info(first);
		logger.info(second);
	}
	
}
