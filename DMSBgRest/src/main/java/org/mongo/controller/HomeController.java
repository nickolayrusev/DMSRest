package org.mongo.controller;

import java.io.IOException;
import java.util.List;

import org.mongo.domain.Campaign;
import org.mongo.services.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	CampaignService campaignService;
	
	@RequestMapping(value = "/campaign", method = RequestMethod.GET)
	@ResponseBody
	public List<Campaign> campaigns(
			@RequestParam(required = true, value = "page") Integer page,
			@RequestParam(required = true, value = "type") Integer type)
					throws IOException {
		logger.info("page is: " + page + "type is: " + type);
		//page = page==null || page<=1 ? 1 : page;
		int firstPage = (page-1) * 2;
		int secondPage = page+(page-1);
		logger.info("firstpage:"+firstPage + " secondPage:"+secondPage);
		List<Campaign> lstCampaignsFirstPage = campaignService.getCampaigns(type, firstPage);
		List<Campaign> lstCampaignsSecondPage = campaignService.getCampaigns(type, secondPage);
		List<Campaign> mergedList = Lists.newArrayList( Iterables.concat(lstCampaignsFirstPage,lstCampaignsSecondPage) );
		logger.info("first list size: "+lstCampaignsFirstPage.size() +" second list size: "+lstCampaignsSecondPage.size()+" merged list size: "+mergedList.size());
		return mergedList;
	}
	
}
