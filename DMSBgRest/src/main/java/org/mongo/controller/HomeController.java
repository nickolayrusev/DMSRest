package org.mongo.controller;

import java.io.IOException;
import java.util.List;

import org.mongo.domain.Campaign;
import org.mongo.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/campaign", method = RequestMethod.GET)
	@ResponseBody
	public List<Campaign> campaigns(
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "type") Integer type)
			throws IOException {
		logger.info("page is: " + page);
		logger.info("type is: " + type);
		List<Campaign> lstCampaigns = CommonUtils.parseCampaignByPage(page,type);
		return lstCampaigns;
	}
	
	
	@RequestMapping(value = "/testString",method=RequestMethod.GET)
    @ResponseBody
	public String getAandB()  {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("a", "a");
		node.put("b", "b");
		return  "a";
    }
	
}
