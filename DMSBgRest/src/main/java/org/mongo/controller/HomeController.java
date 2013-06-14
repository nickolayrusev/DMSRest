package org.mongo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
	
	@RequestMapping(value = "/campaign",method=RequestMethod.GET)
    @ResponseBody
	public List<Campaign> campaigns(@RequestParam(required=false,value="page") Integer page) throws IOException  {
		logger.info("page is: "+page);
		List<Campaign> lstCampaigns = new ArrayList<Campaign>(); 
		Document doc = Jsoup.connect("http://dmsbg.com/index.php?page=4&spage=1&p=0").get();
		Elements links = doc.select("a.news_item");
		for (Element element : links) {
			String hrefAttribute = element.attr("href");
			String item = CommonUtils.parseQueryString(hrefAttribute,"item");
			Element titleElement = element.select(".title").first();
			String titleText = titleElement.text();
			String dmsText = titleElement.children().first().text();
			Element dateElement = element.select(".date").first();
			String dateText = dateElement.text();
			Element anonceElement = element.select(".anonce").first();
			String anonceText = anonceElement.text();
			Elements sumElements = anonceElement.select("strong");
			String sumText = sumElements.text();
			
			String imgUrl = element.select("img").attr("src");
			
			Campaign campaign = new Campaign();
			campaign.setId(Long.parseLong(item));
			campaign.setTitle(titleText);
			campaign.setText(dmsText);
			campaign.setDescription(anonceText);
			campaign.setSum(sumText);
			campaign.setDate(dateText);
			campaign.setSmallImageUrl(imgUrl);
			campaign.setBigImageUrl(imgUrl.replace("file1", "file2"));
			
			lstCampaigns.add(campaign);
		}
		
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
