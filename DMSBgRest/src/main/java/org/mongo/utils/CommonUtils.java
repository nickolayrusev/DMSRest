package org.mongo.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mongo.domain.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {
	private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);
	public static final String parseSum(String sum){
		if(StringUtils.isBlank(sum)){
			return StringUtils.EMPTY;
		}
		sum = sum.replace(".", StringUtils.EMPTY) ;
		for (int i = sum.length()-1; i >= 0 ; i--) {
			if(Character.isDigit(sum.charAt(i))){
				String letters = sum.substring(i+1,sum.length()).replace(" ",	StringUtils.EMPTY);
				String digits = sum.substring(0,i+1);
				return digits + " " +letters;
			}
		}
		return StringUtils.EMPTY;
	}
	/**
	 * Parse query string and returns value by key
	 * @param queryString
	 * @param key
	 * @return the value, or empty string if not found
	 */
	public static final String parseQueryString(String queryString,String key) {
		List<NameValuePair> lstValues = URLEncodedUtils.parse(queryString, Charset.forName("UTF-8"));
		for (NameValuePair nameValuePair : lstValues) {
			if(nameValuePair.getName().equalsIgnoreCase(key)){
				return nameValuePair.getValue();
			}
		}
		return StringUtils.EMPTY;
	}
	/**
	 * spage (type of campaign) :
	 * 0 - people ; 1 - organization ; 2 - other
	 * @param page
	 * @param type
	 * @return list of campaigns
	 */
	public static final List<Campaign> parseCampaignByPage(Integer page,Integer type){
		type = type==null ? 0 : type;
		page = page==null ? 0 : page;
		List<Campaign> lstCampaigns = new ArrayList<Campaign>(); 
		
		Document doc = null;
		try {
			doc = Jsoup.connect(Constants.WEBSITE + "?page=4&spage="+String.valueOf(type)+"&p="+String.valueOf(page)).get();
			logger.info("hitting url: " + doc.baseUri());
		} catch (IOException e) {
			logger.error("io exc",e);
			return Collections.emptyList();
		}
		
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
			campaign.setSum(parseSum(sumText));
			campaign.setDate(dateText);
			campaign.setSmallImageUrl(imgUrl);
			campaign.setBigImageUrl(imgUrl.replace("file1", "file2"));
			
			lstCampaigns.add(campaign);
		}
		
		return lstCampaigns;
	}
}
